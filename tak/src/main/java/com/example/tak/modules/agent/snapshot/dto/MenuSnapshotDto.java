package com.example.tak.modules.agent.snapshot.dto;

import java.util.List;

public record MenuSnapshotDto(
        Integer storeId,
        String storeName,
        String storeAddress,
        Integer menuVersion,
        List<MenuDto> menus
) {
}
