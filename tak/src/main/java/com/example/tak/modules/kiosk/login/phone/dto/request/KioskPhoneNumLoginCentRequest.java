package com.example.tak.modules.kiosk.login.phone.dto.request;

import lombok.Getter;
import lombok.Setter;

// 키오스크 → 중앙
@Getter
@Setter
public class KioskPhoneNumLoginCentRequest {

    private String phoneNumber;

    private Integer storeId;
}
