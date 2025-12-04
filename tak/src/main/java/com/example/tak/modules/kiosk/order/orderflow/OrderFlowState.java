package com.example.tak.modules.kiosk.order.orderflow;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrderFlowState {
    private String wsSessionId;
    private String storeId;

    private Integer menuId;
    private String menuName;

    private String temperature;
    private String size;

    private List<Integer> selectedOptionValueIds = new ArrayList<>();

    private OrderStep step;
    private String sessionId; // agentSessionId
}
