package com.example.tak.modules.kiosk.order.orderflow;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrderFlowStateManager {
    private final Map<String, OrderFlowState> states = new ConcurrentHashMap<>();

    public OrderFlowState getOrCreate(String wsSessionId){
        return states.computeIfAbsent(wsSessionId, id -> {
            OrderFlowState s =new OrderFlowState();
            s.setWsSessionId(id);
            s.setStep(OrderStep.SELECT_MENU);
            return s;
        });
    }

    public OrderFlowState get(String wsSessionId){
        return states.get(wsSessionId);
    }

    public void remove(String wsSessionId){
        states.remove(wsSessionId);
    }
}
