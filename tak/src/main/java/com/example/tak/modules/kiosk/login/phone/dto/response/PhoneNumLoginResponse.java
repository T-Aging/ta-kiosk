package com.example.tak.modules.kiosk.login.phone.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

// 키오스크 → 프론트
@Getter
@Setter
@Builder
public class PhoneNumLoginResponse {

    private String type;

    private boolean login_success;

    private String message;

    private Integer userId;

    private String username;

    private String maskedPhone;
}
