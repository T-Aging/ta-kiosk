package com.example.tak.modules.sync.dto;

import java.util.List;

public record OrderDetailSyncMessage(
        Integer sourceOrderDetailId,
        Integer menuId,
        Integer quantity,
        String temperature,
        String size,
        Integer orderDetailPrice,
        List<OrderOptionSyncMessage> options
) {
}
