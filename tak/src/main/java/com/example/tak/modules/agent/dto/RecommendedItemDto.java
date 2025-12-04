package com.example.tak.modules.agent.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RecommendedItemDto {
    private String name;

    private int price;

    @JsonProperty("menu_image")
    private String menuImage;

}
