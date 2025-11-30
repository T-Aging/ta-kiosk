package com.example.tak.modules.websocket;

import com.example.tak.modules.agent.service.AgentService;
import com.example.tak.modules.kiosk.dto.ConverseRequest;
import com.example.tak.modules.kiosk.dto.SessionStartRequest;
import com.example.tak.modules.websocket.dto.WebSocketErrorResponse;
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

    public String route(String type, JsonNode data) throws Exception {
        return switch (type) {
            case "start" -> {
                log.info("[Router] handling START");

                SessionStartRequest request = new SessionStartRequest();
                request.setStoreId(data.get("storeId").asText());
                request.setMenuVersion(data.get("menuVersion").asInt());

                var response = agentService.startSession(request);

                yield objectMapper.writeValueAsString(response);
            }
            case "converse" -> {
                log.info("[Router] handling CONVERSE");

                ConverseRequest request = new ConverseRequest();
                request.setStoreId(data.get("storeId").asText());
                request.setMenuVersion(data.get("menuVersion").asInt());
                request.setSessionId(data.get("sessionId").asText());
                request.setUserText(data.get("userText").asText());

                var response = agentService.converse(request);

                yield objectMapper.writeValueAsString(response);
            }
            default -> {
                log.warn("[Router] UNKNOWN type: {}", type);
                WebSocketErrorResponse error=WebSocketErrorResponse.of("UNKNOWN_TYPE","지원하지 않은 메시지 타입");
                yield objectMapper.writeValueAsString(error);
            }
        };

    }
}
