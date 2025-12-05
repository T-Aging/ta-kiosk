package com.example.tak.modules.kiosk.order.service;

import com.example.tak.common.*;
import com.example.tak.modules.agent.snapshot.repository.MenuRepository;
import com.example.tak.modules.agent.snapshot.repository.StoreRepository;
import com.example.tak.modules.kiosk.cart.repository.OrderHeaderRepository;
import com.example.tak.modules.kiosk.order.dto.OrderItemSaveDto;
import com.example.tak.modules.kiosk.order.dto.OrderSaveDto;
import com.example.tak.modules.kiosk.order.repository.OptionValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderSaveService {
    private final OrderHeaderRepository orderHeaderRepository;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;
    private final OptionValueRepository optionValueRepository;

    @Transactional
    public OrderHeader saveOrder(OrderSaveDto dto){
        if(dto.getItems() == null || dto.getItems().isEmpty()){
            throw new IllegalArgumentException("저장할 주문 아이템이 없습니다.");
        }

        OrderItemSaveDto item = dto.getItems().get(0);

        // 1) 매장, 메뉴 조회
        Store store = storeRepository.findById((dto.getStoreId()))
                .orElseThrow(() -> new IllegalArgumentException("매장을 찾을 수 없습니다: " + dto.getStoreId()));

        Menu menu = menuRepository.findById(item.getMenuId())
                .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다: " + item.getMenuId()));

        // 2) 선택 옵션 조회
        List<OptionValue> optionValues = (item.getOptionValueIds() == null || item.getOptionValueIds().isEmpty())
                ? List.of()
                : optionValueRepository.findAllById(item.getOptionValueIds());

        // 3) 가격 계산 (기본 + 옵션 합계)
        BigDecimal basePrice = menu.getPrice();
        BigDecimal optionTotal = optionValues.stream()
                .map(OptionValue::getExtraPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int qty = (item.getQuantity() == null || item.getQuantity() <= 0) ? 1 : item.getQuantity();
        BigDecimal itemPrice = basePrice.add(optionTotal);          // 1잔 가격
        BigDecimal totalPrice = itemPrice.multiply(BigDecimal.valueOf(qty)); // 전체 주문 금액

        // 4) 기존 CART 헤더 조회 (같은 매장 + 같은 agentSessionId)
        OrderHeader header = orderHeaderRepository
                .findFirstByStore_IdAndSessionIdAndOrderStateOrderByOrderDateTimeDesc(
                        dto.getStoreId(),
                        dto.getSessionId(),
                        OrderHeader.OrderState.CART
                )
                .orElse(null);

        // 4-1) 없으면 새 헤더 생성
        if (header == null) {
            header = new OrderHeader();
            header.setStore(store);
            header.setUserId(dto.getUserId()); // 비회원이면 null
            header.setSessionId(dto.getSessionId()); // agentSessionId
            header.setOrderDateTime(LocalDateTime.now());
            header.setOrderDate(LocalDate.now());
            header.setWaitingNum(null); // 나중에 대기번호 로직 붙이기
            header.setTotalPrice(totalPrice); // 첫 아이템 금액으로 초기화
            header.setOrderState(OrderHeader.OrderState.CART);
        } else {
            // 이미 존재하는 CART에 금액 누적
            BigDecimal current = header.getTotalPrice() != null
                    ? header.getTotalPrice()
                    : BigDecimal.ZERO;
            header.setTotalPrice(current.add(totalPrice));
        }

        // 5) OrderDetail 생성
        OrderDetail detail = new OrderDetail();
        detail.setMenu(menu);
        detail.setQuantity(qty);
        detail.setOrderDetailPrice(itemPrice);

        header.addDetail(detail);

        // 6) OrderOption 생성
        for (OptionValue ov : optionValues) {
            OrderOption opt = new OrderOption();
            opt.setOrderDetail(detail);
            opt.setOptionGroup(ov.getOptionGroup());
            opt.setOptionValue(ov);
            opt.setExtraNum(1);
            opt.setExtraPrice(ov.getExtraPrice());

            detail.getOrderOptions().add(opt);
        }

        // 7) 저장 (cascade로 detail/option까지 같이 insert)
        return orderHeaderRepository.save(header);
    }
}
