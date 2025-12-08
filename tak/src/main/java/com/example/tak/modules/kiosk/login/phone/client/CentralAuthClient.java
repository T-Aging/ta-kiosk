package com.example.tak.modules.kiosk.login.phone.client;

import com.example.tak.modules.kiosk.login.phone.dto.request.KioskPhoneNumLoginCentRequest;
import com.example.tak.modules.kiosk.login.phone.dto.response.KioskPhoneNumLoginCentResponse;

public interface CentralAuthClient {
    KioskPhoneNumLoginCentResponse loginByPhoneNum(KioskPhoneNumLoginCentRequest request);
}
