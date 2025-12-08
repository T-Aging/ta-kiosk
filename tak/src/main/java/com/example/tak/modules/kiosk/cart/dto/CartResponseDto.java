package com.example.tak.modules.kiosk.cart.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CartResponseDto {
    private String type;
    private Integer orderId;
    private Integer storeId;
    private String storeName;
    private String sessionId;
    private LocalDateTime orderDateTime;
    private BigDecimal totalPrice;
    private Integer waitingNum;
    private List<CartItemDto> items;
}

