package com.example.tak.modules.sync.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderHeaderSyncMessage(
        Integer sourceOrderId,
        Integer storeId,
        Integer userId,
        Integer totalPrice,
        String orderState,
        LocalDateTime orderDateTime,
        Integer waitingNum,
        List<OrderDetailSyncMessage> details
) {
}
