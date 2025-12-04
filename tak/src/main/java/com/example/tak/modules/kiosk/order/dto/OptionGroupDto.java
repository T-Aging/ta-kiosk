package com.example.tak.modules.kiosk.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class OptionGroupDto {
    private String groupName;
    private int maxSelect;
    private List<OptionDto> options;
}
