package com.example.tak.modules.kiosk.login.client;

import com.example.tak.modules.kiosk.login.phone.dto.request.KioskPhoneNumLoginCentRequest;
import com.example.tak.modules.kiosk.login.phone.dto.response.KioskPhoneNumLoginCentResponse;
import com.example.tak.modules.kiosk.login.qr.dto.request.KioskQrLoginCentRequest;
import com.example.tak.modules.kiosk.login.qr.dto.response.KioskQrLoginCentResponse;

public interface CentralAuthClient {
    KioskPhoneNumLoginCentResponse loginByPhoneNum(KioskPhoneNumLoginCentRequest request);

    KioskQrLoginCentResponse loginByQr(KioskQrLoginCentRequest request);
}
