package com.example.tak.modules.kiosk.order.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class AskDetailOptionYnResponse {
    private String type;          // "ask_detail_option_yn"
    private String menuName;
    private String temperature;
    private String size;
    private String question;
    private List<String> choices;
}
