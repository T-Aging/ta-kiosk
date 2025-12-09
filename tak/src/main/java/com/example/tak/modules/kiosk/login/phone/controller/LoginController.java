package com.example.tak.modules.kiosk.login.phone.controller;

import com.example.tak.modules.kiosk.login.phone.dto.request.PhoneNumLoginRequest;
import com.example.tak.modules.kiosk.login.phone.dto.response.PhoneNumLoginResponse;
import com.example.tak.modules.kiosk.login.phone.service.LoginService;
import com.example.tak.modules.kiosk.login.qr.dto.request.QrLoginRequest;
import com.example.tak.modules.kiosk.login.qr.dto.response.QrLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inter/ta-kiosk/auth")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/phone-num")
    public ResponseEntity<PhoneNumLoginResponse> loginByPhoneNum(
            @RequestBody PhoneNumLoginRequest request
    ) {
        return ResponseEntity.ok(loginService.loginByPhoneNum(request));
    }

    @PostMapping("/qr")
    public ResponseEntity<QrLoginResponse> loginByQr(
            @RequestBody QrLoginRequest request
    ) {
        return ResponseEntity.ok(loginService.loginByQr(request));
    }
}
