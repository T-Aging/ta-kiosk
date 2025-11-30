package com.example.tak.modules.websocket.dto;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.databind.JsonNode;

@Getter
@Setter
public class KioskWebSocketMessage {
    private String type;
    private JsonNode data;
}
