package com.example.tak.modules.kiosk.recent.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecentOrderAddToCartRequest {

    private Integer orderId;

    private Integer orderDetailId;
}
