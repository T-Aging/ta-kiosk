package com.example.tak.modules.kiosk.login.client;

import com.example.tak.config.CentralServerProperties;
import com.example.tak.modules.kiosk.login.dto.request.KioskPhoneNumLoginCentRequest;
import com.example.tak.modules.kiosk.login.dto.response.KioskPhoneNumLoginCentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class CentralAuthClientImpl implements CentralAuthClient{

    private final RestTemplate restTemplate;
    private final CentralServerProperties centralServerProperties;

    @Override
    public KioskPhoneNumLoginCentResponse loginByPhoneNum(KioskPhoneNumLoginCentRequest request) {

        String url = centralServerProperties.getBaseUrl()
                + "/inter/ta-kiosk/auth/phone-num";

        return restTemplate.postForObject(url, request, KioskPhoneNumLoginCentResponse.class);
    }
}
