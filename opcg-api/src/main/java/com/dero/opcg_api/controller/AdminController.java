package com.dero.opcg_api.controller;

import com.dero.opcg_api.service.CardSyncService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CardSyncService syncService;

    // http://localhost:8080/api/admin/sync
    @GetMapping("/sync")
    public String triggerSync() {
        return syncService.syncCardsFromApi();
    }
}