package com.example.tak.modules.kiosk.cart.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteCartItemRequest {
    private Integer orderDetailId;
}
