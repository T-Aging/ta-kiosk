package com.example.tak.modules.agent.snapshot.dto;

public record OptionRuleDto(
        Integer id,
        String ruleType,
        String ruleJson,

        Integer menuId,
        Integer groupId,
        Integer valueId
) {
}
