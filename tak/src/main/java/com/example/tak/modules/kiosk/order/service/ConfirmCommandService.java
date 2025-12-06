package com.example.tak.modules.kiosk.order.service;

import com.example.tak.common.OrderDetail;
import com.example.tak.common.OrderHeader;
import com.example.tak.common.OrderOption;
import com.example.tak.modules.kiosk.cart.dto.CartItemDto;
import com.example.tak.modules.kiosk.cart.dto.CartItemOptionDto;
import com.example.tak.modules.kiosk.cart.dto.CartResponseDto;
import com.example.tak.modules.kiosk.cart.repository.OrderHeaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ConfirmCommandService {

    private final OrderHeaderRepository orderHeaderRepository;

    @Transactional
    public CartResponseDto confirmOrder(Integer storeId, String sessionId, Integer userId) {

        // 1) CART 상태의 가장 최신 주문 찾기
        OrderHeader header = orderHeaderRepository
                .findFirstByStore_IdAndSessionIdAndOrderStateOrderByOrderDateTimeDesc(
                        storeId,
                        sessionId,
                        OrderHeader.OrderState.CART
                )
                .orElseThrow(() ->
                        new IllegalArgumentException("확정할 장바구니가 없습니다.")
                );
        header.setUserId(userId);

        // 2) 상태 변경 (CART → CONFIRM)
        header.setOrderState(OrderHeader.OrderState.CONFIRM);

        // 3) 저장
        OrderHeader saved = orderHeaderRepository.save(header);

        // 4) CartResponseDto 형태로 반환
        CartResponseDto res = new CartResponseDto();
        res.setType("order_confirm");
        res.setOrderId(saved.getId());
        res.setStoreId(saved.getStore().getId());
        res.setStoreName(saved.getStore().getName());
        res.setSessionId(saved.getSessionId());
        res.setOrderDateTime(saved.getOrderDateTime());
        res.setTotalPrice(saved.getTotalPrice());

        res.setItems(
                saved.getOrderDetails()
                        .stream()
                        .map(this::toCartItemDto)
                        .toList()
        );

        return res;
    }

    private CartItemDto toCartItemDto(OrderDetail detail) {

        CartItemDto dto = new CartItemDto();
        dto.setOrderDetailId(detail.getId());
        dto.setMenuId(detail.getMenu().getId());
        dto.setMenuName(detail.getMenu().getName());
        dto.setMenuImage(detail.getMenu().getMenuImage());

        int qty = detail.getQuantity() == null ? 1 : detail.getQuantity();
        dto.setQuantity(qty);

        dto.setUnitPrice(detail.getOrderDetailPrice());
        dto.setLineTotalPrice(
                detail.getOrderDetailPrice().multiply(BigDecimal.valueOf(qty))
        );

        dto.setTemperature(detail.getTemperature());
        dto.setSize(detail.getSize());

        dto.setOptions(
                detail.getOrderOptions().stream()
                        .map(this::toCartItemOptionDto)
                        .toList()
        );

        return dto;
    }

    private CartItemOptionDto toCartItemOptionDto(OrderOption opt) {

        CartItemOptionDto dto = new CartItemOptionDto();
        dto.setOptionGroupId(opt.getOptionGroup().getId());
        dto.setOptionGroupName(opt.getOptionGroup().getDisplayName());
        dto.setOptionValueId(opt.getOptionValue().getId());
        dto.setOptionValueName(opt.getOptionValue().getDisplayName());
        dto.setExtraPrice(opt.getExtraPrice());
        return dto;
    }
}