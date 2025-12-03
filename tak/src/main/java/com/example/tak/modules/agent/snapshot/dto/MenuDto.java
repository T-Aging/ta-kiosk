package com.example.tak.modules.agent.snapshot.dto;

import java.math.BigDecimal;
import java.util.List;

public record MenuDto(
        Integer id,
        String name,
        String menuImage,
        BigDecimal price,
        String description,
        Integer calorie,
        Integer sugar,
        Integer caffeine,
        String allergen,

        String category,
        List<String> tags,
        List<String> aliases,

        List<OptionGroupDto> optionGroups
) {
}
