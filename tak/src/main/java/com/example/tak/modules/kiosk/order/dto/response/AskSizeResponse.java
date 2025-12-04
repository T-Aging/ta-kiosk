package com.example.tak.modules.kiosk.order.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class AskSizeResponse {
    private String type;          // "ask_size"
    private String menuName;
    private String temperature;
    private String question;
    private List<String> choices;
}
