package com.example.tak.modules.kiosk.login.client;

import com.example.tak.modules.kiosk.login.dto.request.KioskPhoneNumLoginCentRequest;
import com.example.tak.modules.kiosk.login.dto.response.KioskPhoneNumLoginCentResponse;

public interface CentralAuthClient {
    KioskPhoneNumLoginCentResponse loginByPhoneNum(KioskPhoneNumLoginCentRequest request);
}
