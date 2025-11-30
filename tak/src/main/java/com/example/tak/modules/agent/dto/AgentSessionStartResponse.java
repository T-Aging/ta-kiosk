package com.example.tak.modules.agent.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentSessionStartResponse {
    private String status;

    @JsonProperty("menu_count")
    private int menuCount;
}