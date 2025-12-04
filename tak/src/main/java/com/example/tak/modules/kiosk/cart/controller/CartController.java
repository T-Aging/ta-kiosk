package com.example.tak.modules.kiosk.cart.controller;

import com.example.tak.modules.kiosk.cart.dto.CartResponseDto;
import com.example.tak.modules.kiosk.cart.service.CartQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ta-kiosk/kiosk")
@RequiredArgsConstructor
public class CartController {
    private final CartQueryService cartQueryService;

    public CartResponseDto getCart(
            @RequestParam("storeId") Integer storeId,
            @RequestParam("sessionId") String sessionId
    ){
        return cartQueryService.getCart(storeId, sessionId);
    }
}
