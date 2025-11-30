package com.example.tak.modules.kiosk.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ConverseResponse {
    private String storeId;
    private int menuVersion;
    private String sessionId;
    private String userText;
    private String reply;
    private boolean cacheHit;
}
