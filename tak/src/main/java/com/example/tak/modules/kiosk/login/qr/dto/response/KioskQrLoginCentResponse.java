package com.example.tak.modules.kiosk.login.qr.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KioskQrLoginCentResponse {
    private boolean login_success;

    private String message;

    private Integer userId;

    private String username;
}
