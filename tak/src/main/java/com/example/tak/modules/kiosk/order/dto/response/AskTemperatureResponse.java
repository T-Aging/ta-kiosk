package com.example.tak.modules.kiosk.order.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class AskTemperatureResponse {
    private String type; // "ask_temperature"
    private String menuName;
    private String question;
    private List<String> choices;
}
