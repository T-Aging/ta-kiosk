package com.example.tak.modules.agent.snapshot.dto;

import java.util.List;

public record OptionGroupDto(
        Integer id,
        String groupKey,
        String displayName,
        String selectionType,
        Integer minSelect,
        Integer maxSelect,
        Integer sortOrder,
        Boolean required,
        Boolean active,

        List<OptionValueDto> values,
        List<OptionRuleDto> rules
) {
}
