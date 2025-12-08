package com.example.tak.modules.kiosk.login.phone.dto.request;

import lombok.Getter;
import lombok.Setter;

// 프론트 → 키오스크
@Getter
@Setter
public class PhoneNumLoginRequest {

    private String phoneNumber;

    private String sessionId;
}
