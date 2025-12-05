package com.example.tak.modules.kiosk.cart.service;

import com.example.tak.common.OrderDetail;
import com.example.tak.common.OrderHeader;
import com.example.tak.modules.kiosk.cart.dto.CartItemDto;
import com.example.tak.modules.kiosk.cart.dto.CartItemOptionDto;
import com.example.tak.modules.kiosk.cart.dto.CartResponseDto;
import com.example.tak.modules.kiosk.cart.dto.response.DeleteCartItemResponse;
import com.example.tak.modules.kiosk.cart.repository.OrderHeaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartCommandService {

    private final OrderHeaderRepository orderHeaderRepository;

    @Transactional
    public DeleteCartItemResponse deleteCartItem(Integer storeId, String sessionId, Integer orderDetailId) {

        // 1) 현재 CART 상태의 최신 OrderHeader 조회
        OrderHeader header = orderHeaderRepository
                .findFirstByStore_IdAndSessionIdAndOrderStateOrderByOrderDateTimeDesc(
                        storeId, sessionId, OrderHeader.OrderState.CART
                )
                .orElseThrow(() ->
                        new IllegalArgumentException("장바구니에 담긴 주문이 없습니다."));

        // 2) 지울 대상 OrderDetail 찾기
        //    (Lazy 컬렉션이므로 size() 한 번 호출해서 초기화 보장)
        header.getOrderDetails().size();

        OrderDetail targetDetail = header.getOrderDetails().stream()
                .filter(d -> d.getId().equals(orderDetailId))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 장바구니 항목을 찾을 수 없습니다. orderDetailId=" + orderDetailId));

        // 3) 헤더의 orderDetails 목록에서 제거
        header.getOrderDetails().remove(targetDetail);
        // 양방향 연관관계 끊기(옵션은 orphanRemoval = true면 자연스럽게 같이 삭제됨)
        targetDetail.setOrderHeader(null);

        // 4) 남은 아이템이 없으면 헤더 자체 삭제 + 빈 장바구니 응답
        if (header.getOrderDetails().isEmpty()) {
            String storeName = header.getStore().getName();

            orderHeaderRepository.delete(header);

            DeleteCartItemResponse emptyRes = new DeleteCartItemResponse();
            emptyRes.setType("cart_updated");
            emptyRes.setOrderId(null);
            emptyRes.setStoreId(storeId);
            emptyRes.setStoreName(storeName);
            emptyRes.setSessionId(sessionId);
            emptyRes.setOrderDateTime(null);
            emptyRes.setTotalPrice(BigDecimal.ZERO);
            emptyRes.setItems(List.of());

            return emptyRes;
        }

        // 5) 남은 아이템 기준으로 totalPrice 재계산
        BigDecimal newTotal = header.getOrderDetails().stream()
                .map(d -> {
                    BigDecimal unitPrice = d.getOrderDetailPrice();
                    int qty = (d.getQuantity() == null || d.getQuantity() <= 0) ? 1 : d.getQuantity();
                    return unitPrice.multiply(BigDecimal.valueOf(qty));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        header.setTotalPrice(newTotal);

        // 6) 저장 (헤더 update)
        OrderHeader saved = orderHeaderRepository.save(header);

        // 7) 최신 장바구니 응답 DTO로 매핑
        return toDeleteCartItemResponse(saved);
    }

    private DeleteCartItemResponse toDeleteCartItemResponse(OrderHeader header) {
        DeleteCartItemResponse res = new DeleteCartItemResponse();
        res.setType("cart_updated");
        res.setOrderId(header.getId());
        res.setStoreId(header.getStore().getId());
        res.setStoreName(header.getStore().getName());
        res.setSessionId(header.getSessionId());
        res.setOrderDateTime(header.getOrderDateTime());
        res.setTotalPrice(header.getTotalPrice());

        List<CartItemDto> items = header.getOrderDetails().stream()
                .map(detail -> {
                    CartItemDto item = new CartItemDto();
                    item.setOrderDetailId(detail.getId());
                    item.setMenuId(detail.getMenu().getId());
                    item.setMenuName(detail.getMenu().getName());
                    item.setMenuImage(detail.getMenu().getMenuImage()); // 실제 필드명에 맞게 수정

                    int qty = (detail.getQuantity() == null || detail.getQuantity() <= 0) ? 1 : detail.getQuantity();
                    item.setQuantity(qty);

                    // unitPrice = order_detail_price
                    item.setUnitPrice(detail.getOrderDetailPrice());
                    item.setLineTotalPrice(
                            detail.getOrderDetailPrice().multiply(BigDecimal.valueOf(qty))
                    );

                    // 온도/사이즈 컬럼이 OrderDetail에 있다고 가정
                    item.setTemperature(detail.getTemperature());
                    item.setSize(detail.getSize());

                    // 옵션 매핑
                    List<CartItemOptionDto> options = detail.getOrderOptions().stream()
                            .map(opt -> {
                                CartItemOptionDto o = new CartItemOptionDto();
                                o.setOptionGroupId(opt.getOptionGroup().getId());
                                o.setOptionGroupName(opt.getOptionGroup().getDisplayName());
                                o.setOptionValueId(opt.getOptionValue().getId());
                                o.setOptionValueName(opt.getOptionValue().getDisplayName());
                                o.setExtraPrice(opt.getExtraPrice());
                                return o;
                            })
                            .toList();

                    item.setOptions(options);

                    return item;
                })
                .toList();

        res.setItems(items);

        return res;
    }

}
