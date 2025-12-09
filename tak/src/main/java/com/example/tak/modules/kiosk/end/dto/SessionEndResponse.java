package com.example.tak.modules.kiosk.end.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class SessionEndResponse {

    private String type;

    private String message;
}
