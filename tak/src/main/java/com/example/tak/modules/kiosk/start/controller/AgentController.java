package com.example.tak.modules.kiosk.start.controller;

import com.example.tak.modules.agent.service.AgentService;
import com.example.tak.modules.kiosk.start.dto.ConverseRequest;
import com.example.tak.modules.kiosk.start.dto.ConverseResponse;
import com.example.tak.modules.kiosk.start.dto.SessionStartRequest;
import com.example.tak.modules.kiosk.start.dto.SessionStartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ta-kiosk/ai-agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    // 이용 시작
    @PostMapping("/session/start")
    public SessionStartResponse startSession(@RequestBody SessionStartRequest req){
        return agentService.startSession(req);
    }

    // ai-agent 대화
    @PostMapping("/converse")
    public ConverseResponse converse(@RequestBody ConverseRequest req){
        return agentService.converse(req);
    }
}
