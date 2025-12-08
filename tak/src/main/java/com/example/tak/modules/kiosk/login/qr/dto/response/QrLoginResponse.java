package com.example.tak.modules.kiosk.login.qr.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class QrLoginResponse {
    private boolean login_success;

    private String message;

    private Integer userId;

    private String username;
}
