package com.example.tak.modules.kiosk.start.dto;

import com.example.tak.modules.agent.snapshot.dto.MenuSnapshotDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionStartRequest {
    private String storeId;
    private int menuVersion;
}
