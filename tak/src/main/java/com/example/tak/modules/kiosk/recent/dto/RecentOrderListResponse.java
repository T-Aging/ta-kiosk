package com.example.tak.modules.kiosk.recent.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RecentOrderListResponse {

    private String type;

    private List<RecentOrderSummaryDto> orders;
}
