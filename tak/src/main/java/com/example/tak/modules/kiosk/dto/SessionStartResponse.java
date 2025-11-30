package com.example.tak.modules.kiosk.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SessionStartResponse {
    private String storeId;
    private int menuVersion;
    private String sessionId;
    private int menuCount;
}
