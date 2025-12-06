package com.example.tak.modules.kiosk.login.service;

import com.example.tak.modules.kiosk.login.dto.request.PhoneNumLoginRequest;
import com.example.tak.modules.kiosk.login.dto.response.PhoneNumLoginResponse;


public interface LoginService {
    PhoneNumLoginResponse loginByPhoneNum(PhoneNumLoginRequest request);
}
