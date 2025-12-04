package com.example.tak.modules.kiosk.order.repository;

import com.example.tak.common.OrderHeader;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderHeaderRepository extends JpaRepository<OrderHeader, Integer> {
}
