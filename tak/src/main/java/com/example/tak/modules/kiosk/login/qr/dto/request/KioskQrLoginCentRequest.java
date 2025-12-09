package com.example.tak.modules.kiosk.login.qr.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KioskQrLoginCentRequest {
    private String qrCode;

    private Integer storeId;
}
