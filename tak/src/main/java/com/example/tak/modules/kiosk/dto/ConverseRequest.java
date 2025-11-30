package com.example.tak.modules.kiosk.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConverseRequest {
    private String storeId;
    private int menuVersion;
    private String sessionId;
    private String userText;
}
