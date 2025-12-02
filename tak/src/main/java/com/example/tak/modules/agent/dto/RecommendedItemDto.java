package com.example.tak.modules.agent.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RecommendedItemDto {
    @JsonProperty("menu_id")
    private String menuId;
}
