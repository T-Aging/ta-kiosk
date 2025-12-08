package com.example.tak.modules.kiosk.login.phone.dto.response;

import lombok.Getter;
import lombok.Setter;

// 중앙 → 키오스크
@Getter
@Setter
public class KioskPhoneNumLoginCentResponse {

    private boolean login_success;

    private String message;

    private Integer userId;

    private String username;

    private String maskedPhone;
}
