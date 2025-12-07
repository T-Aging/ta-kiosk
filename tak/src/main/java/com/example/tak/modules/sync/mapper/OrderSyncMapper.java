package com.example.tak.modules.sync.mapper;

import com.example.tak.common.OrderDetail;
import com.example.tak.common.OrderHeader;
import com.example.tak.common.OrderOption;
import com.example.tak.modules.sync.dto.OrderDetailSyncMessage;
import com.example.tak.modules.sync.dto.OrderHeaderSyncMessage;
import com.example.tak.modules.sync.dto.OrderOptionSyncMessage;

import java.util.List;
import java.util.stream.Collectors;

public class OrderSyncMapper {
    public static OrderHeaderSyncMessage toMessage(OrderHeader header){
        List<OrderDetailSyncMessage> detailSyncMessages=header.getOrderDetails()
                .stream()
                .map(OrderSyncMapper::toDetailMessage)
                .collect(Collectors.toList());

        return new OrderHeaderSyncMessage(
                header.getId(),
                header.getStore().getId(),
                header.getUserId(),
                header.getTotalPrice() != null ? header.getTotalPrice().intValue() : null,
                header.getOrderState().name(),
                header.getOrderDateTime(),
                detailSyncMessages
        );
    }

    private static OrderDetailSyncMessage toDetailMessage(OrderDetail detail){
        List<OrderOptionSyncMessage> optionSyncMessages = detail.getOrderOptions()
                .stream()
                .map(OrderSyncMapper::toOptionMessage)
                .collect(Collectors.toList());

        return new OrderDetailSyncMessage(
                detail.getId(),
                detail.getMenu().getId(),
                detail.getQuantity(),
                detail.getTemperature(),
                detail.getSize(),
                detail.getOrderDetailPrice() != null ? detail.getOrderDetailPrice().intValue() : null,
                optionSyncMessages
        );
    }

    private static OrderOptionSyncMessage toOptionMessage(OrderOption option){
        return new OrderOptionSyncMessage(
                option.getId(),
                null,
                option.getOptionGroup() != null ? option.getOptionGroup().getId() : null,
                option.getOptionValue() != null ? option.getOptionValue().getId() : null,
                option.getExtraNum(),
                option.getExtraPrice() != null ? option.getExtraPrice().intValue() : null
        );
    }
}
