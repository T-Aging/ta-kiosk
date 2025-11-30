package com.example.tak.modules.websocket;

import com.example.tak.modules.websocket.dto.KioskWebSocketMessage;
import com.example.tak.modules.websocket.dto.WebSocketErrorResponse;
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

    private void sendError(WebSocketSession session, String code, String message){
        try{
            WebSocketErrorResponse error = WebSocketErrorResponse.of(code, message);
            String json = objectMapper.writeValueAsString(error);
        }catch (Exception e){
            log.error("Failed to send error message", e);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket connected: sessionId={}", session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("WebSocket closed: sessionId={}, status={}", session.getId(), status);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("Received message: sessionId={}, payload={}", session.getId(), payload);

        KioskWebSocketMessage kioskWebSocketMessage;
        try {
            kioskWebSocketMessage = objectMapper.readValue(payload, KioskWebSocketMessage.class);
        } catch (Exception e){
            log.error("Invalid WebSocket messsage format", e);
            sendError(session, "INVALID_FORMAT", "잘못된 메시지 형식");
            return;
        }

        try{
            String responseJson = router.route(kioskWebSocketMessage.getType(), kioskWebSocketMessage.getData());
            session.sendMessage(new TextMessage(responseJson));
        }catch (Exception e){
            log.error("Error while processing WebSocket message", e);
            sendError(session, "INTERNAL_ERROR", "요청 처리 중 문제 발생");
        }
    }
}
