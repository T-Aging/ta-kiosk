package com.example.tak.modules.kiosk.custom.client;

import com.example.tak.config.CentralServerProperties;
import com.example.tak.modules.kiosk.custom.dto.CustomMenuListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class CentralCustomMenuMenuClientImpl implements CentralCustomMenuClient {

    private final RestTemplate restTemplate;
    private final CentralServerProperties centralServerProperties;

    @Override
    public CustomMenuListResponse getCustomMenus(Integer userId) {
        String url=centralServerProperties.getBaseUrl()
                + "/inter/ta-kiosk/custom-menus?userId=" + userId;

        return restTemplate.getForObject(url, CustomMenuListResponse.class);
    }
}
