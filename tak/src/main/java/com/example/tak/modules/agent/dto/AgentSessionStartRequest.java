package com.example.tak.modules.agent.dto;

import com.example.tak.modules.agent.snapshot.dto.MenuSnapshotDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// snapshot 전달용
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentSessionStartRequest {
    @JsonProperty("store_id")
    private Integer storeId;

    @JsonProperty("menu_version")
    private int menuVersion;

    private MenuSnapshotDto menuSnapshot;
}
