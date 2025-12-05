package com.example.tak.modules.kiosk.cart.dto.response;

import com.example.tak.modules.kiosk.cart.dto.CartItemDto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DeleteCartItemResponse {
    private String type = "cart_updated";

    private Integer orderId;
    private Integer storeId;
    private String storeName;
    private String sessionId;

    private LocalDateTime orderDateTime;
    private BigDecimal totalPrice;

    private List<CartItemDto> items;
}
