package com.example.tak.modules.agent.service;

import com.example.tak.modules.agent.client.AiAgentClient;
import com.example.tak.modules.agent.dto.AgentConverseRequest;
import com.example.tak.modules.agent.dto.AgentConverseResponse;
import com.example.tak.modules.kiosk.dto.ConverseRequest;
import com.example.tak.modules.kiosk.dto.ConverseResponse;
import com.example.tak.modules.kiosk.dto.SessionStartRequest;
import com.example.tak.modules.kiosk.dto.SessionStartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AgentService {
    private final AiAgentClient aiAgentClient;

    public SessionStartResponse startSession(SessionStartRequest req) {
        String sessionId = UUID.randomUUID().toString();

        int menuCount = aiAgentClient.warmupL1(
                req.getStoreId(),
                req.getMenuVersion()
        );
//        int menuCount=0;

        return SessionStartResponse.builder()
                .storeId(req.getStoreId())
                .menuVersion(req.getMenuVersion())
                .sessionId(sessionId)
                .menuCount(menuCount)
                .build();
    }

    public ConverseResponse converse(ConverseRequest req) {

        // 1) kiosk → agent 요청 DTO 변환
        AgentConverseRequest agentReq = new AgentConverseRequest();
        agentReq.setStoreId(req.getStoreId());
        agentReq.setMenuVersion(req.getMenuVersion());
        agentReq.setSessionId(req.getSessionId());
        agentReq.setUserText(req.getUserText());

        // 2) FastAPI 호출
        AgentConverseResponse agentRes = aiAgentClient.converse(agentReq);

        // 3) agent 응답 → kiosk 응답으로 변환
        ConverseResponse kioskRes = new ConverseResponse();
        kioskRes.setStoreId(agentRes.getStoreId());
        kioskRes.setMenuVersion(agentRes.getMenuVersion());
        kioskRes.setSessionId(agentRes.getSessionId());
        kioskRes.setUserText(agentRes.getUserText());
        kioskRes.setReply(agentRes.getReply());
        kioskRes.setIntent(agentRes.getIntent());
        kioskRes.setReason(agentRes.getReason());
        kioskRes.setItems(agentRes.getItems());
        kioskRes.setCacheHit(agentRes.isCacheHit());

        return kioskRes;
    }
}
