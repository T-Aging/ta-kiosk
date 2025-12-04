package com.example.tak.modules.kiosk.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderItemSaveDto {
    private Integer menuId;
    private String menuName;

    private Integer quantity;
    private String temperature;
    private String size;

    private List<Integer> optionValueIds;
}
