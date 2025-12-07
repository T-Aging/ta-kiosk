package com.example.tak.modules.kiosk.websocket.session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WebSocketSessionManager {
    private final Map<String, AgentSessionInfo> sessions = new ConcurrentHashMap<>();

    public void register(String wsSessionId, AgentSessionInfo info){
        sessions.put(wsSessionId, info);
        log.info("[SessionManager] registered wsSessiondId={}, info={}", wsSessionId, info);
    }

    public AgentSessionInfo get(String wsSessionId){
        AgentSessionInfo info=sessions.get(wsSessionId);
        if (info == null) {
            log.debug("[SessionManager] no session found for wsSessionId={}", wsSessionId);
        }
        return info;
    }

    public void touch(String wsSessionId){
        AgentSessionInfo info=sessions.get(wsSessionId);
        if(info != null){
            info.touch();
        }
    }

    public void remove(String wsSessionId){
        AgentSessionInfo removed = sessions.remove(wsSessionId);
        log.info("[SessionManager] removed wsSessionId={}, info={}", wsSessionId, removed);
    }

    public void attachUser(String wsSessionId, Integer userId){
        AgentSessionInfo info = sessions.get(wsSessionId);
        if (info!=null){
            info.setUserId(userId);
            info.touch();
            log.info("[SessionManager] Attached userId={} to wsSessionId={}", userId, wsSessionId);
        }else {
            log.warn("[SessionManager] Cannot attach userId. Session not found for wsSessionId={}", wsSessionId);
        }
    }
}
