package com.example.tak.modules.agent.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentConverseResponse {

    @JsonProperty("store_id")
    private String storeId;

    @JsonProperty("menu_version")
    private int menuVersion;

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("user_text")
    private String userText;

    @JsonProperty("reply")
    private String reply;

    @JsonProperty("intent")
    private String intent;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("items")
    private List<RecommendedItemDto> items;

    @JsonProperty("cache_hit")
    private boolean cacheHit;
}