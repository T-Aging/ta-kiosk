package com.example.tak.modules.websocket.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentSessionInfo {
    private String storeId;
    private int menuVersion;
    private String agentSessionId; // FastAPI session_id
    private Instant lastUpdated;

    public static AgentSessionInfo of(String storeId, int menuVersion, String agentSessionId){
        return new AgentSessionInfo(storeId, menuVersion, agentSessionId, Instant.now());
    }

    public void touch(){
        this.lastUpdated=Instant.now();
    }
}
