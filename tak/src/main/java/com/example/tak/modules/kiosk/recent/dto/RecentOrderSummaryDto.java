package com.example.tak.modules.kiosk.recent.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class RecentOrderSummaryDto {

    private Integer orderId;

    private BigDecimal totalPrice;

    private LocalDateTime orderDateTime;

    private long daysAgo;

    private String mainMenuName;

    private BigDecimal mainMenuPrice;

    private int otherMenuCount;
}
