package com.example.tak.modules.kiosk.order.dto.response;

import com.example.tak.modules.kiosk.order.dto.tofe.OptionGroupDto;
import lombok.Data;

import java.util.List;

@Data
public class ShowDetailOptionsResponse {
    private String type;                  // "show_detail_options"
    private String menuName;
    private List<OptionGroupDto> optionGroups;
}
