package com.example.tak.modules.agent.client;

import com.example.tak.modules.agent.dto.AgentConverseRequest;
import com.example.tak.modules.agent.dto.AgentConverseResponse;
import com.example.tak.modules.agent.dto.AgentSessionStartRequest;
import com.example.tak.modules.agent.dto.AgentSessionStartResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiAgentClient {

    // WebClientConfig에서 만든 Bean 주입
    private final WebClient aiAgentWebClient;

    // FastAPI /session/start 호출해서 L1 스냅샷 워밍업
    // 성공하면 menu_count 리턴, 실패하면 0 리턴
    public int warmupL1(AgentSessionStartRequest req) {
        log.info("[AiAgentClient] warmupL1 호출");

        AgentSessionStartResponse resp = aiAgentWebClient.post()
                .uri("/ta-kiosk/ai-agent/session/start")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(AgentSessionStartResponse.class)
                .doOnNext(r -> log.info("[AiAgentClient] warmupL1 응답: {}", r))
                .doOnError(ex -> log.error("[AiAgentClient] warmupL1 호출 중 오류", ex))
                .onErrorResume(ex-> Mono.just(new AgentSessionStartResponse("ERROR", 0)))
                .block();

        return (resp != null) ? resp.getMenuCount() : 0;
    }

    // FastAPI /converse 호출 (대화 엔드포인트)
    public AgentConverseResponse converse(AgentConverseRequest req){
        log.info("[AiAgentClient] converse 호출: {}", req);
        return aiAgentWebClient.post()
                .uri("/ta-kiosk/ai-agent/converse")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(AgentConverseResponse.class)
                .doOnNext(r -> log.info("[AiAgentClient] converse 응답: {}", r))
                .doOnError(ex -> log.error("[AiAgentClient] converse 호출 중 오류", ex))
                .onErrorResume(ex-> {
                            AgentConverseResponse fallback = new AgentConverseResponse();
                            fallback.setStoreId(req.getStoreId());
                            fallback.setMenuVersion(req.getMenuVersion());
                            fallback.setReply("ERROR: AI Agent 호출 실패");
                            fallback.setCacheHit(false);
                            return Mono.just(fallback);
                })
                .block();

    }

    public void endSession(String sessionId){
        log.info("[AiAgentClient] endSession 호출: sessionId={}", sessionId);

        aiAgentWebClient.post()
                .uri("/ta-kiosk/ai-agent/session/end")
                .bodyValue(Map.of("sessionId", sessionId))
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(r -> log.info("[AiAgentClient] endSession 성공: sessionId={}", sessionId))
                .doOnError(ex -> log.error("[AiAgentClient] endSession 호출 중 오류, sessionId={}", sessionId, ex))
                .onErrorResume(ex-> Mono.empty())
                .block();
    }
}
