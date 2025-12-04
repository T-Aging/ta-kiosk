package com.example.tak.modules.kiosk.order.dto.response;

import lombok.Data;

@Data
public class OrderItemCompleteResponse {
    private String type;      // "order_item_complete"
    private String message;
}
