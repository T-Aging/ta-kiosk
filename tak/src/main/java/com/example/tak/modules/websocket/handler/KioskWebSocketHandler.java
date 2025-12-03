package com.example.tak.modules.websocket.handler;

import com.example.tak.modules.websocket.dto.KioskWebSocketMessage;
import com.example.tak.modules.websocket.dto.WebSocketErrorResponse;
import com.example.tak.modules.websocket.session.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;


@Slf4j
@Component
@RequiredArgsConstructor
public class KioskWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final WebSocketMessageRouter router;
    private final WebSocketSessionManager sessionManager;

    // WebSocket 에러 응답을 공통 포맷(Json)으로 전송
    // - code: 에러 식별 코드
    // - message: 사용자 메시지 또는 개발자용 메시지
    private void sendError(WebSocketSession session, String code, String message){
        try{
            WebSocketErrorResponse error = WebSocketErrorResponse.of(code, message);
            String json = objectMapper.writeValueAsString(error);
            session.sendMessage(new TextMessage(json));
        }catch (Exception e){
            log.error("Failed to send error message", e);
        }
    }

    // 클라이언트가 WebSocket에 최초 연결된 순간 호출
    // - wsSession(session.getId) 확보
    // 필요 시 초기 자원 로딩 또는 연결 록드 기록
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket connected: sessionId={}", session.getId());
    }

    // 클라이언트가 WebSocket 연결을 종료할 때 호출된다.
    // - SessionManager에서 매핑된 AI Agent 세션 정보 제거
    // - 자원 정리
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("WebSocket closed: sessionId={}, status={}", session.getId(), status);
        sessionManager.remove(session.getId());
    }

    // WebSocket으로 텍스트 메시지가 도착했을 때 호출된다.
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("Received message: sessionId={}, payload={}", session.getId(), payload);

        // 1) JSON 파싱 -> KioskWebSocketMessage 변환
        KioskWebSocketMessage kioskWebSocketMessage;
        try {
            kioskWebSocketMessage = objectMapper.readValue(payload, KioskWebSocketMessage.class);
        } catch (Exception e){
            log.error("Invalid WebSocket messsage format", e);
            sendError(session, "INVALID_FORMAT", "잘못된 메시지 형식");
            return;
        }

        // 2) 비즈니스 로직 처리 (Router로 위임, router.route() 호출)
        try{
            String responseJson = router.route(kioskWebSocketMessage.getType(), kioskWebSocketMessage.getData(), session.getId());
            // 3) WebSockt으로 결과 전송
            session.sendMessage(new TextMessage(responseJson));
        }catch (Exception e){
            log.error("Error while processing WebSocket message", e);
            sendError(session, "INTERNAL_ERROR", "요청 처리 중 문제 발생");
        }
    }
}
