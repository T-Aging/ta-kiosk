package com.example.tak.modules.agent.service;

import com.example.tak.modules.agent.client.AiAgentClient;
import com.example.tak.modules.agent.dto.AgentConverseRequest;
import com.example.tak.modules.agent.dto.AgentConverseResponse;
import com.example.tak.modules.agent.dto.AgentSessionStartRequest;
import com.example.tak.modules.agent.snapshot.dto.MenuSnapshotDto;
import com.example.tak.modules.agent.snapshot.service.MenuSnapshotService;
import com.example.tak.modules.kiosk.dto.ConverseRequest;
import com.example.tak.modules.kiosk.dto.ConverseResponse;
import com.example.tak.modules.kiosk.dto.SessionStartRequest;
import com.example.tak.modules.kiosk.dto.SessionStartResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentService {
    private final AiAgentClient aiAgentClient;
    private final MenuSnapshotService menuSnapshotService;

    public SessionStartResponse startSession(SessionStartRequest req) {
        // 1) 세션 ID 생성 (Spring <-> FastAPI 공통 사용 세션)
        String sessionId = UUID.randomUUID().toString();

        // 2) 매장/메뉴 버전에 대한 스냅샷 생성
        Integer storeIdInt = Integer.parseInt(req.getStoreId());
        MenuSnapshotDto snapshot = menuSnapshotService.buildSnapshot(
                storeIdInt,
                req.getMenuVersion()
        );

        log.info("[AgentService] snapshot menus size={}", snapshot.menus().size());

        // 3) FastAPI로 보냃 세션 시작 요청 DTO
        AgentSessionStartRequest agentSessionStartRequest=new AgentSessionStartRequest();
        agentSessionStartRequest.setStoreId(storeIdInt);
        agentSessionStartRequest.setMenuVersion(req.getMenuVersion());
        agentSessionStartRequest.setMenuSnapshot(snapshot);

        // 4) FastAPI /session/start 호출 -> L1 캐시 워밍업
        int menuCount = aiAgentClient.warmupL1(agentSessionStartRequest);

        // 5) Kiosk 쪽으로 내려줄 응답 생성
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
