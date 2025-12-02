package com.example.tak.modules.kiosk.dto;

import com.example.tak.modules.agent.dto.RecommendedItemDto;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConverseResponse {
    private String storeId;
    private int menuVersion;
    private String sessionId;
    private String userText;
    private String reply;
    private String intent;
    private String reason;
    private List<RecommendedItemDto> items;
    private boolean cacheHit;
}
