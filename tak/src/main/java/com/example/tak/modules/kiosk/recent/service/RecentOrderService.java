package com.example.tak.modules.kiosk.recent.service;

import com.example.tak.common.OrderDetail;
import com.example.tak.common.OrderHeader;
import com.example.tak.common.OrderOption;
import com.example.tak.modules.kiosk.cart.dto.CartItemDto;
import com.example.tak.modules.kiosk.cart.dto.CartItemOptionDto;
import com.example.tak.modules.kiosk.cart.dto.CartResponseDto;
import com.example.tak.modules.kiosk.cart.repository.OrderHeaderRepository;
import com.example.tak.modules.kiosk.recent.dto.RecentOrderListResponse;
import com.example.tak.modules.kiosk.recent.dto.RecentOrderSummaryDto;
import com.example.tak.modules.kiosk.websocket.session.AgentSessionInfo;
import com.example.tak.modules.kiosk.websocket.session.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecentOrderService {

    private final WebSocketSessionManager webSocketSessionManager;

    private final OrderHeaderRepository orderHeaderRepository;

    @Transactional(readOnly = true)
    public RecentOrderListResponse getRecentOrders(String wsSessionId){
        // 1) 세션에서 storeId, userId 꺼냄
        AgentSessionInfo info = webSocketSessionManager.get(wsSessionId);
        if (info==null){
            log.warn("[RecentOrder] Session not found. wsSessionId={}", wsSessionId);
            // 비어 있는 응답 반환
            return RecentOrderListResponse.builder()
                    .type("recent_orders")
                    .orders(List.of())
                    .build();
        }

        if (info.getUserId()==null){
            log.warn("[RecentOrder] userId not attached. wsSessionId={}", wsSessionId);
            return RecentOrderListResponse.builder()
                    .type("recent_orders")
                    .orders(List.of())
                    .build();
        }

        Integer storeId=Integer.valueOf(info.getStoreId());
        Integer userId = info.getUserId();

        // 2) 최근 확정 주문 5개 조회
        List<OrderHeader> headers=orderHeaderRepository
                .findTop5ByStore_IdAndUserIdAndOrderStateOrderByOrderDateTimeDesc(
                        storeId,userId,OrderHeader.OrderState.CONFIRM
                );

        // 3) DTO 에 매핑
        List<RecentOrderSummaryDto> summaries = headers.stream()
                .map(this::toSummaryDto)
                .toList();

        return RecentOrderListResponse.builder()
                .type("recent_orders")
                .orders(summaries)
                .build();
    }

    private RecentOrderSummaryDto toSummaryDto(OrderHeader header){
        List<OrderDetail> details = header.getOrderDetails();

        if(details==null || details.isEmpty()){
            return RecentOrderSummaryDto.builder()
                    .orderId(header.getId())
                    .totalPrice(header.getTotalPrice())
                    .orderDateTime(header.getOrderDateTime())
                    .daysAgo(calcDaysAgo(header))
                    .mainMenuName("알 수 없는 메뉴")
                    .mainMenuPrice(BigDecimal.ZERO)
                    .otherMenuCount(0)
                    .build();
        }

        OrderDetail mainDetail = details.stream()
                .min(Comparator.comparing(OrderDetail::getId))
                .orElse(details.get(0));

        int qty=mainDetail.getQuantity() == null ? 1: mainDetail.getQuantity();
        BigDecimal unitPrice = mainDetail.getOrderDetailPrice() != null
                ? mainDetail.getOrderDetailPrice()
                : BigDecimal.ZERO;

        // 옵션 포함된 한 줄의 금액 = order_detail_price * quantity
        BigDecimal linePrice = unitPrice.multiply(BigDecimal.valueOf(qty));

        int otherCount = Math.max(0, details.size() - 1);

        return RecentOrderSummaryDto.builder()
                .orderId(header.getId())
                .totalPrice(header.getTotalPrice())
                .orderDateTime(header.getOrderDateTime())
                .daysAgo(calcDaysAgo(header))
                .mainMenuName(mainDetail.getMenu().getName())
                .mainMenuPrice(linePrice)
                .otherMenuCount(otherCount)
                .build();
    }

    private long calcDaysAgo(OrderHeader header) {
        LocalDate orderDate = header.getOrderDateTime() != null
                ? header.getOrderDateTime().toLocalDate()
                : header.getOrderDate(); // 혹시 orderDate만 채워져 있을 수도 있으니까

        if (orderDate == null) return 0L;

        return ChronoUnit.DAYS.between(orderDate, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public CartResponseDto getRecentOrderDetail(String wsSessionId, Integer orderId){
        AgentSessionInfo info=webSocketSessionManager.get(wsSessionId);
        if(info==null || info.getUserId() == null){
            log.warn("[RecentOrderDetail] session/userId not found. wsSessionId={}", wsSessionId);

            CartResponseDto empty=new CartResponseDto();
            empty.setType("recent_order_detail");
            empty.setItems(List.of());
            return empty;
        }

        Integer storeId=Integer.valueOf(info.getStoreId());
        Integer userId = info.getUserId();

        // 1) 해당 userId의 해당 storeId인지 확인하고 조회
        OrderHeader header=orderHeaderRepository
                .findByIdAndStore_IdAndUserIdAndOrderState(
                        orderId,
                        storeId,
                        userId,
                        OrderHeader.OrderState.CONFIRM
                )
                .orElseThrow(()->new IllegalStateException("주문 내역을 찾을 수 없습니다."));

        // 2) CartResponseDto에 넣기
        CartResponseDto res = new CartResponseDto();
        res.setType("recent_order_detail");
        res.setOrderId(header.getId());
        res.setStoreId(header.getStore().getId());
        res.setStoreName(header.getStore().getName());
        res.setSessionId(header.getSessionId());
        res.setOrderDateTime(header.getOrderDateTime());
        res.setTotalPrice(header.getTotalPrice());

        res.setItems(
                header.getOrderDetails()
                        .stream()
                        .map(this::toCartItemDto)
                        .toList()
        );

        return res;
    }

    @Transactional
    public CartResponseDto addItemFromRecentOrder(
            Integer storeId,
            String sessionId,
            Integer userId,
            Integer sourceOrderId,
            Integer sourceOrderDetailId
    ) {

        // 1) 회원의 최근 확정 주문에서 원본 주문 헤더 조회
        OrderHeader sourceHeader = orderHeaderRepository
                .findByIdAndStore_IdAndUserIdAndOrderState(
                        sourceOrderId,
                        storeId,
                        userId,
                        OrderHeader.OrderState.CONFIRM
                )
                .orElseThrow(() -> new IllegalArgumentException("해당 최근 주문을 찾을 수 없습니다."));

//        // !) 그 주문 안에서 사용자가 선택한 OrderDetail 찾기
//        OrderDetail sourceDetail = sourceHeader.getOrderDetails().stream()
//                .filter(d -> d.getId().equals(sourceOrderDetailId))
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("해당 메뉴를 최근 주문에서 찾을 수 없습니다."));

        // 2) 현재 세션의 CART 주문 헤더 찾기 (없으면 새로 생성)
        OrderHeader cartHeader = orderHeaderRepository
                .findFirstByStore_IdAndSessionIdAndOrderStateOrderByOrderDateTimeDesc(
                        storeId,
                        sessionId,
                        OrderHeader.OrderState.CART
                )
                .orElseGet(() -> {
                    OrderHeader h = new OrderHeader();
                    h.setStore(sourceHeader.getStore());
                    h.setSessionId(sessionId);
                    h.setUserId(userId);
                    h.setOrderState(OrderHeader.OrderState.CART);
                    h.setOrderDateTime(LocalDateTime.now());
                    h.setOrderDate(LocalDate.now());
                    h.setTotalPrice(BigDecimal.ZERO);
                    return h;
                });

        if (sourceOrderDetailId == null){
            log.info("[RecentOrder] add whole order to cart. sourceOrderId={}", sourceOrderId);

            for (OrderDetail sourceDetail : sourceHeader.getOrderDetails()){
                copyDetailToCart(cartHeader, sourceDetail);
            }
        } else {
            OrderDetail sourceDetail = sourceHeader.getOrderDetails().stream()
                    .filter(d -> d.getId().equals(sourceOrderDetailId))
                    .findFirst()
                    .orElseThrow(()-> new IllegalArgumentException("해당 메뉴를 최근 주문에서 찾을 수 없습니다."));
            copyDetailToCart(cartHeader, sourceDetail);
        }

        // 4) 전체 장바구니 금액 다시 계산
        BigDecimal newTotal = cartHeader.getOrderDetails().stream()
                .map(d -> {
                    int qty = d.getQuantity() == null ? 1 : d.getQuantity();
                    BigDecimal unit = d.getOrderDetailPrice() != null
                            ? d.getOrderDetailPrice()
                            : BigDecimal.ZERO;
                    return unit.multiply(BigDecimal.valueOf(qty));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cartHeader.setTotalPrice(newTotal);

        // 7) 저장
        OrderHeader savedCart = orderHeaderRepository.save(cartHeader);

        // 8) CartResponseDto 매핑
        CartResponseDto result = new CartResponseDto();
        result.setType("recent_order_to_cart");
        result.setOrderId(savedCart.getId());
        result.setStoreId(savedCart.getStore().getId());
        result.setStoreName(savedCart.getStore().getName());
        result.setSessionId(savedCart.getSessionId());
        result.setOrderDateTime(savedCart.getOrderDateTime());
        result.setTotalPrice(savedCart.getTotalPrice());
        result.setWaitingNum(savedCart.getWaitingNum()); // 계속 NULL

        result.setItems(
                savedCart.getOrderDetails().stream()
                        .map(this::toCartItemDto)
                        .toList()
        );

        return result;
    }

    private CartItemDto toCartItemDto(OrderDetail orderDetail){
        CartItemDto cartItemDto=new CartItemDto();
        cartItemDto.setOrderDetailId(orderDetail.getId());
        cartItemDto.setMenuId(orderDetail.getMenu().getId());
        cartItemDto.setMenuName(orderDetail.getMenu().getName());
        cartItemDto.setMenuImage(orderDetail.getMenu().getMenuImage());

        int qty = orderDetail.getQuantity() == null ? 1 : orderDetail.getQuantity();
        cartItemDto.setQuantity(qty);

        cartItemDto.setUnitPrice(orderDetail.getOrderDetailPrice());
        cartItemDto.setLineTotalPrice(
                orderDetail.getOrderDetailPrice().multiply(BigDecimal.valueOf(qty))
        );

        cartItemDto.setTemperature(orderDetail.getTemperature());
        cartItemDto.setSize(orderDetail.getSize());

        cartItemDto.setOptions(
                orderDetail.getOrderOptions().stream()
                        .map(this::toCartItemOptionDto)
                        .toList()
        );

        return cartItemDto;
    }

    private CartItemOptionDto toCartItemOptionDto(OrderOption orderOption){
        CartItemOptionDto cartItemOptionDto = new CartItemOptionDto();
        cartItemOptionDto.setOptionGroupId(orderOption.getOptionGroup().getId());
        cartItemOptionDto.setOptionGroupName(orderOption.getOptionGroup().getDisplayName());
        cartItemOptionDto.setOptionValueId(orderOption.getOptionValue().getId());
        cartItemOptionDto.setOptionValueName(orderOption.getOptionValue().getDisplayName());
        cartItemOptionDto.setExtraPrice(orderOption.getExtraPrice());
        return cartItemOptionDto;
    }

    private void copyDetailToCart(OrderHeader cartHeader, OrderDetail sourceDetail){
        // 원본 OrderDetail을 복사해서 CART 헤더에 추가
        OrderDetail newDetail = new OrderDetail();
        newDetail.setMenu(sourceDetail.getMenu());
        newDetail.setQuantity(sourceDetail.getQuantity());
        newDetail.setTemperature(sourceDetail.getTemperature());
        newDetail.setSize(sourceDetail.getSize());
        newDetail.setOrderDetailPrice(sourceDetail.getOrderDetailPrice());

        cartHeader.addDetail(newDetail);

        // 옵션들 복사
        for (OrderOption srcOpt : sourceDetail.getOrderOptions()) {
            OrderOption newOpt = new OrderOption();
            newOpt.setOrderDetail(newDetail);
            newOpt.setOptionGroup(srcOpt.getOptionGroup());
            newOpt.setOptionValue(srcOpt.getOptionValue());
            newOpt.setExtraNum(srcOpt.getExtraNum());
            newOpt.setExtraPrice(srcOpt.getExtraPrice());
            newDetail.getOrderOptions().add(newOpt);
        }
    }

}
