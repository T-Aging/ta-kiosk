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

    public SessionStartResponse startSession(SessionStartRequest req){
        String sessionId= UUID.randomUUID().toString();

        int menuCount=aiAgentClient.warmupL1(
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

    public ConverseResponse converse(ConverseRequest req){

        AgentConverseRequest agentReq = new AgentConverseRequest(
                req.getStoreId(),
                req.getMenuVersion(),
                req.getSessionId(),
                req.getUserText()
        );

        AgentConverseResponse agentRes = aiAgentClient.converse(agentReq);

        return ConverseResponse.builder()
                .storeId((agentReq.getStoreId()))
                .menuVersion(agentRes.getMenuVersion())
                .sessionId(agentReq.getSessionId())
                .userText(req.getUserText())
                .reply(agentRes.getReply())
                .cacheHit(agentRes.isCacheHit())
                .build();
    }
}
