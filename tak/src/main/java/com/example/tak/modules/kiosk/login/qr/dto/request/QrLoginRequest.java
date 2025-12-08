package com.example.tak.modules.kiosk.login.qr.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QrLoginRequest {
    private String qrCode;

    private String sessionId;
}
