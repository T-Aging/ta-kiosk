package com.example.tak.modules.websocket;

import com.example.tak.modules.agent.service.AgentService;
import com.example.tak.modules.kiosk.dto.ConverseRequest;
import com.example.tak.modules.kiosk.dto.SessionStartRequest;
import com.example.tak.modules.websocket.dto.WebSocketErrorResponse;
import com.example.tak.modules.websocket.session.AgentSessionInfo;
import com.example.tak.modules.websocket.session.WebSocketSessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketMessageRouter {

    private final ObjectMapper objectMapper;
    private final AgentService agentService;
    private final WebSocketSessionManager webSocketSessionManager;

    public String route(String type, JsonNode data, String wsSessionId) throws Exception {
        return switch (type) {
            // 새로운 대화 세션 시작 요청
            // storeId/menuVersion 기반으로 FastAPI에 warmup 요청
            // 응답으로 받은 agentSessionId와 WebSocket 세션을 매핑 저장
            case "start" -> {
                log.info("[Router] handling START");

                // 파라미터 DTO 구성
                SessionStartRequest request = new SessionStartRequest();
                request.setStoreId(data.get("storeId").asText());
                request.setMenuVersion(data.get("menuVersion").asInt());

                // FastAPI 호출
                var response = agentService.startSession(request);
                String agentSessionId = response.getSessionId();

                // WebSocketSessionId → AgentSessionInfo 매핑 저장
                webSocketSessionManager.register(
                        wsSessionId,
                        AgentSessionInfo.of(request.getStoreId(), request.getMenuVersion(), agentSessionId)
                );

                // 응답 그대로 WebSocket으로 내려보냄
                yield objectMapper.writeValueAsString(response);
            }

            // 기존 대화 세션을 사용하여 AI와 대화를 이어가는 요청
            // sessionId는 클라이언트가 보내지 않아도, WebSocketSessionManager에서 자동으로 찾아서 사용함
            case "converse" -> {
                log.info("[Router] handling CONVERSE, wsSessionId={}", wsSessionId);
                // WebSocketSessionManager에서 세션 정보 가져오기
                // "sessionId"를 data에서 꺼내는 대신 매니저에서 자동으로 가져올 수 있음.
                AgentSessionInfo info=webSocketSessionManager.get(wsSessionId);

                // 세션이 존재하지 않을 경우(예: 서버 재시작/클라이언트의 잘못된 호출)
                if(info==null){
                    log.warn("[Router] no session found for wsSessionId={}", wsSessionId);
                    yield objectMapper.writeValueAsString(
                            WebSocketErrorResponse.of("SESSION_NOT_FOUND", "세션이 없음. 다시 시작 권장")
                    );
                }

                info.touch();; // 마지막 업데이트 시간 갱신

                log.info("[Router] handling CONVERSE");

                // FastAPI에 보낼 DTO 구성
                ConverseRequest request = new ConverseRequest();
                request.setStoreId(info.getStoreId());
                request.setMenuVersion(info.getMenuVersion());
                request.setSessionId(info.getAgentSessionId());
                request.setUserText(data.get("userText").asText());

                // FastAPI 대화 요청
                var response = agentService.converse(request);

                // WebSocket 전송
                yield objectMapper.writeValueAsString(response);
            }

            // 그 외의 타입은 모두 에러 처리
            default -> {
                log.warn("[Router] UNKNOWN type: {}", type);
                WebSocketErrorResponse error=WebSocketErrorResponse.of("UNKNOWN_TYPE","지원하지 않은 메시지 타입");
                yield objectMapper.writeValueAsString(error);
            }
        };

    }
}
