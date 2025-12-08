package com.example.tak.modules.kiosk.cart.repository;

import com.example.tak.common.OrderHeader;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderHeaderRepository extends JpaRepository<OrderHeader, Integer> {
    Optional<OrderHeader> findFirstByStore_IdAndSessionIdAndOrderStateOrderByOrderDateTimeDesc(
            Integer storeId,
            String sessionId,
            OrderHeader.OrderState orderState
    );

    List<OrderHeader> findTop5ByStore_IdAndUserIdAndOrderStateOrderByOrderDateTimeDesc(
            Integer storeId,
            Integer userId,
            OrderHeader.OrderState orderState
    );

    Optional<OrderHeader> findByIdAndStore_IdAndUserIdAndOrderState(
            Integer id,
            Integer storeId,
            Integer userId,
            OrderHeader.OrderState orderState
    );
}
