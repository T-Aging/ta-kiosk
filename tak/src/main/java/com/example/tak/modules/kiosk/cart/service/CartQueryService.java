package com.example.tak.modules.kiosk.cart.service;

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
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartQueryService {
    private final OrderHeaderRepository orderHeaderRepository;

    @Transactional(readOnly = true)
    public CartResponseDto getCart(Integer storeId, String sessionId) {

        OrderHeader header = orderHeaderRepository
                .findFirstByStore_IdAndSessionIdAndOrderStateOrderByOrderDateTimeDesc(
                        storeId,
                        sessionId,
                        OrderHeader.OrderState.CART
                )
                .orElseThrow(() -> new IllegalArgumentException("장바구니에 담긴 주문이 없습니다."));

        CartResponseDto cart = new CartResponseDto();
        cart.setType("cart");
        cart.setStoreId(header.getStore().getId());
        cart.setStoreName(header.getStore().getName());
        cart.setSessionId(header.getSessionId());
        cart.setOrderDateTime(header.getOrderDateTime());
        cart.setTotalPrice(header.getTotalPrice());

        List<CartItemDto> itemDtos = header.getOrderDetails().stream()
                .map(this::toCartItemDto)
                .toList();

        cart.setItems(itemDtos);

        return cart;
    }

    private CartItemDto toCartItemDto(OrderDetail detail) {
        CartItemDto dto = new CartItemDto();

        dto.setOrderDetailId(detail.getId());
        dto.setMenuId(detail.getMenu().getId());
        dto.setMenuName(detail.getMenu().getName());
        dto.setMenuImage(detail.getMenu().getMenuImage());

        dto.setQuantity(detail.getQuantity());

        // OrderDetail에 저장된 가격은 "1잔 기준 가격"으로 저장해둔 상태
        BigDecimal unitPrice = detail.getOrderDetailPrice();
        dto.setUnitPrice(unitPrice);

        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(detail.getQuantity()));
        dto.setLineTotalPrice(lineTotal);

        dto.setTemperature(detail.getTemperature());
        dto.setSize(detail.getSize());

        List<CartItemOptionDto> options = detail.getOrderOptions().stream()
                .map(this::toCartItemOptionDto)
                .toList();

        dto.setOptions(options);

        return dto;
    }

    private CartItemOptionDto toCartItemOptionDto(OrderOption orderOption) {
        CartItemOptionDto dto = new CartItemOptionDto();

        dto.setOptionGroupId(orderOption.getOptionGroup().getId());
        dto.setOptionGroupName(orderOption.getOptionGroup().getDisplayName());

        dto.setOptionValueId(orderOption.getOptionValue().getId());
        dto.setOptionValueName(orderOption.getOptionValue().getDisplayName());

        dto.setExtraPrice(orderOption.getExtraPrice());

        return dto;
    }
}
