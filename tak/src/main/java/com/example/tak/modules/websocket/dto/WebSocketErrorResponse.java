package com.example.tak.modules.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketErrorResponse {

    private String type = "error";
    private ErrorData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorData{
        private String code;
        private String message;
    }

    public static WebSocketErrorResponse of(String code, String message){
        return new WebSocketErrorResponse("error", new ErrorData(code, message));
    }
}
