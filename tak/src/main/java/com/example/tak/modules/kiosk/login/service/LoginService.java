package com.example.tak.modules.kiosk.login.service;

import com.example.tak.modules.kiosk.login.phone.dto.request.PhoneNumLoginRequest;
import com.example.tak.modules.kiosk.login.phone.dto.response.PhoneNumLoginResponse;
import com.example.tak.modules.kiosk.login.qr.dto.request.QrLoginRequest;
import com.example.tak.modules.kiosk.login.qr.dto.response.QrLoginResponse;


public interface LoginService {
    PhoneNumLoginResponse loginByPhoneNum(PhoneNumLoginRequest request);

    QrLoginResponse loginByQr(QrLoginRequest request);
}
