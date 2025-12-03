package com.example.tak.modules.agent.snapshot.controller;

import com.example.tak.modules.agent.snapshot.dto.MenuSnapshotDto;
import com.example.tak.modules.agent.snapshot.service.MenuSnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inter/menu-snapshot")
@RequiredArgsConstructor
public class MenuSnapshotController {
    private final MenuSnapshotService menuSnapshotService;

    @GetMapping("/{storeId}/{menuVersion}")
    public MenuSnapshotDto getSnapshot(
            @PathVariable Integer storeId,
            @PathVariable Integer menuVersion
    ){
        return menuSnapshotService.buildSnapshot(storeId, menuVersion);
    }
}
