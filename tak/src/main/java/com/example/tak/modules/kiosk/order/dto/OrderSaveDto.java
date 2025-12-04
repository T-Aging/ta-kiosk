package com.example.tak.modules.kiosk.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderSaveDto {
    private Integer storeId;
    private Integer userId;   // 비회원이면 null

    private List<OrderItemSaveDto> items;
}
