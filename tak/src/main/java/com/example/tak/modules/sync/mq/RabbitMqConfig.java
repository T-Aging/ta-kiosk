package com.example.tak.modules.sync.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

// RabbitMQ에 연결해서 특정 Exchange, Queue, Routing Key를 생성한 후 JSON 메시지를 자동으로 변환해서 보내도록 설정
// Producer → [Exchange] ── (routing key) ─→ [Queue] → Consumer
@Configuration
public class RabbitMqConfig {
    // 메시지를 어떻게 라우팅할 것인가
    public static final String EXCHANGE_KIOSK = "kiosk.exchange";
    // 메시지가 실제 저장되는 곳
    public static final String QUEUE_ORDER_SYNC = "order.sync.queue";
    // 메시지를 어떤 큐로 보낼지 선택하는 기준
    public static final String ROUTING_KEY_ORDER_SYNC = "order.sync";

    // DirectExchange -> 라우팅 키가 정확히 일치하는 큐에게만 전달하는 방식
    @Bean
    public DirectExchange kioskExchange() {
        return new DirectExchange(EXCHANGE_KIOSK);
    }

    // QUEUE_ORDER_SYNC 큐를 생성
    // durable=true -> 서버 재시작해도 메시지 유지
    // 키오스크 서버에서 전송한 주문 동기화 메시지가 실제로 저장될 공간
    @Bean
    public Queue orderSyncQueue() {
        return new Queue(QUEUE_ORDER_SYNC, true);
    }

    // "order.sync.queue" 큐를 "kiosk.exchange"에 연결하고 "routing key = order.sync"인 메시지가 오면 이 큐로 보냄
    // 이라는 MQ 라우팅 규칙 생성
    @Bean
    public Binding orderSyncBinding() {
        return BindingBuilder
                .bind(orderSyncQueue())
                .to(kioskExchange())
                .with(ROUTING_KEY_ORDER_SYNC);
    }

    // RabbitTemplate이 객체를 MQ 메시지로 변환할 때 JSOM 형식으로 자동 변환해주는 Converter 등록
    @SuppressWarnings("deprecation")
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // 스프링에서 MQ 메시지를 보낼 때 사용하는 객체
    // ConnectionFactory -> RabbitMQ 연결 정보
    // MessageConverter -> JSON 변환 자동화
    // RabbitTemplate -> 실제 메시지를 publish 하는 객체
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}
