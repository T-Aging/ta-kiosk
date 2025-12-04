package com.example.tak.modules.kiosk.cart.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartItemDto {
    private Integer orderDetailId;

    private Integer menuId;
    private String menuName;
    private String menuImage;

    private Integer quantity;

    // 1잔 기준 가격(기본+옵션)
    private BigDecimal unitPrice;
    private BigDecimal lineTotalPrice;
    private List<CartItemOptionDto> options;
}
