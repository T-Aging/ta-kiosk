package com.example.tak.modules.sync.mq;

import com.example.tak.common.OrderHeader;
import com.example.tak.modules.sync.dto.OrderHeaderSyncMessage;
import com.example.tak.modules.sync.mapper.OrderSyncMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderSyncProducer {
    private final RabbitTemplate rabbitTemplate;

    // 객체(message)를 JSON으로 자동 변환하고 Exchange+RoutingKey에 따라 큐로 전달
    public void sendOrderSync(OrderHeader orderHeader){

        // 1) 엔티티 -> DTO 변환
        OrderHeaderSyncMessage message = OrderSyncMapper.toMessage(orderHeader);

        // 2) MQ에 메시지 발행
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_KIOSK, // 보낼 Exchange 이름
                RabbitMqConfig.ROUTING_KEY_ORDER_SYNC, // 라우팅 키
                message // 전송할 실제 메시지
        );
    }
}
