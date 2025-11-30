package com.example.tak.modules.kiosk.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionStartRequest {
    private String storeId;
    private int menuVersion;
}
