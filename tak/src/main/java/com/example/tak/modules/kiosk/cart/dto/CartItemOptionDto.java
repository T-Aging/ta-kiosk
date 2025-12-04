package com.example.tak.modules.kiosk.cart.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemOptionDto {
    private Integer optionGroupId;
    private String optionGroupName;

    private Integer optionValueId;
    private String optionValueName;

    private BigDecimal extraPrice;
}
