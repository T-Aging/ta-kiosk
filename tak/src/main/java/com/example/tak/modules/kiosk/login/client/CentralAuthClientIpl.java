package com.example.tak.modules.kiosk.login.client;

import com.example.tak.modules.kiosk.login.dto.request.KioskPhoneNumLoginCentRequest;
import com.example.tak.modules.kiosk.login.dto.response.KioskPhoneNumLoginCentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class CentralAuthClientIpl implements CentralAuthClient{

    private final RestTemplate restTemplate;

    @Value("${central.server.base-url}")
    private String centralBaseUrl;

    @Override
    public KioskPhoneNumLoginCentResponse loginByPhoneNum(KioskPhoneNumLoginCentRequest request) {

        String url = centralBaseUrl + "/inter/ta-kiosk/auth/phone-num";

        return restTemplate.postForObject(url, request, KioskPhoneNumLoginCentResponse.class);
    }
}
