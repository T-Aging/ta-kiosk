package com.example.tak.modules.kiosk.custom.client;

import com.example.tak.modules.kiosk.custom.dto.CustomMenuListResponse;

public interface CentralCustomMenuClient {
    CustomMenuListResponse getCustomMenus(Integer userId);
}
