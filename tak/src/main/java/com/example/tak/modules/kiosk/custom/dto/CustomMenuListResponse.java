package com.example.tak.modules.kiosk.custom.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CustomMenuListResponse {

    private String type;

    private List<CustomMenuItemDto> items;
}
