package com.example.tak.modules.agent.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentConverseRequest {

    @JsonProperty("store_id")
    private String storeId;

    @JsonProperty("menu_version")
    private int menuVersion;

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("user_text")
    private String userText;
}