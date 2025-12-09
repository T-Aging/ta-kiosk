package com.example.tak.modules.kiosk.login.phone.client;

import com.example.tak.config.CentralServerProperties;
import com.example.tak.modules.kiosk.login.phone.dto.request.KioskPhoneNumLoginCentRequest;
import com.example.tak.modules.kiosk.login.phone.dto.response.KioskPhoneNumLoginCentResponse;
import com.example.tak.modules.kiosk.login.qr.dto.request.KioskQrLoginCentRequest;
import com.example.tak.modules.kiosk.login.qr.dto.response.KioskQrLoginCentResponse;
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

    @Override
    public KioskQrLoginCentResponse loginByQr(KioskQrLoginCentRequest request) {
        String url = centralServerProperties.getBaseUrl()
                + "/inter/ta-kiosk/auth/qr";

        return restTemplate.postForObject(url, request, KioskQrLoginCentResponse.class);
    }
}
