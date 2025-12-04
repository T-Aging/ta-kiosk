package com.example.tak.modules.kiosk.order.dto;

import lombok.Data;

@Data
public class OptionDto {
    private Integer id;
    private String name;
    private int extraPrice;

    public OptionDto(Integer id, String name, int extraPrice) {
        this.id = id;
        this.name = name;
        this.extraPrice = extraPrice;
    }
}
