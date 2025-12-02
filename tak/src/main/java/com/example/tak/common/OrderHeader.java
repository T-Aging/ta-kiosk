package com.example.tak.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="order_header")
@Getter
@Setter
@NoArgsConstructor
public class OrderHeader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "order_datetime")
    private LocalDateTime orderDateTime;

    @Column(name = "order_date")
    private LocalDate orderDate;

    @Column(name = "waiting_num")
    private  Integer waitingNum;

    @Column(name = "total_price", precision = 10, scale=2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_state")
    private OrderState orderState;

    @OneToMany(mappedBy = "orderHeader", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public enum OrderState{
        PLACED, PAID, MARKING, READY, PICKED, CANCELLED, FAILD
    }

    public void addDetail(OrderDetail detail){
        orderDetails.add(detail);
        detail.setOrderHeader(this);
    }
}
