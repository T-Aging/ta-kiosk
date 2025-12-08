package com.example.tak.modules.kiosk.custom.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CustomMenuItemDto {

    private Integer customId;

    private Integer userId;

    private Integer menuId;

    private String customName;

    private List<CustomMenuDetailDto> details;
}
