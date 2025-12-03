package com.example.tak.modules.agent.snapshot.dto;

import java.math.BigDecimal;

public record OptionValueDto(
        Integer id,
        Integer groupId,
        String valueKey,
        String displayName,
        BigDecimal extraPrice,
        Integer sortOrder,
        Boolean active
) {
}
