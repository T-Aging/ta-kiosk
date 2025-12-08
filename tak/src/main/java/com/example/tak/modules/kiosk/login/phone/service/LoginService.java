package com.example.tak.modules.kiosk.login.phone.service;

import com.example.tak.modules.kiosk.login.phone.dto.request.PhoneNumLoginRequest;
import com.example.tak.modules.kiosk.login.phone.dto.response.PhoneNumLoginResponse;


public interface LoginService {
    PhoneNumLoginResponse loginByPhoneNum(PhoneNumLoginRequest request);
}
