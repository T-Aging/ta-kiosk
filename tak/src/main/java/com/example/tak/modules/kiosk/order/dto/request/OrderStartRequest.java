package com.example.tak.modules.kiosk.order.dto.request;

import lombok.Data;

@Data
public class OrderStartRequest {
    private String menuName;
    private String storeId;
}
