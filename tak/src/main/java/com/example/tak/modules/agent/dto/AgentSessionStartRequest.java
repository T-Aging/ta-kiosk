package com.example.tak.modules.agent.dto;

import com.example.tak.modules.agent.snapshot.dto.MenuSnapshotDto;
import lombok.Data;

// snapshot 전달용
@Data
public class AgentSessionStartRequest {
    private Integer storeId;
    private int menuVersion;
    private MenuSnapshotDto menuSnapshot;
}
