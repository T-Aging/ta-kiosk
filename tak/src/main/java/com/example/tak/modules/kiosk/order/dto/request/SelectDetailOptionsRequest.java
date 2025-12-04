package com.example.tak.modules.kiosk.order.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class SelectDetailOptionsRequest {
    private List<Integer> selectedOptionValueIds;
}
