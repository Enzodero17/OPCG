package com.dero.opcg_api.controller;

import com.dero.opcg_api.model.User;
import com.dero.opcg_api.repository.UserRepository;
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
    private final UserRepository userRepo;

    @GetMapping("/sync")
    public String triggerSync() {
        return syncService.syncCardsFromApi();
    }

    @GetMapping("/create-test-user")
    public User createTestUser() {
        User testUser = new User();
        testUser.setUsername("MugiwaraTest");
        testUser.setEmail("luffy@test.com");
        testUser.setPasswordHash("motdepasse_temporaire");
        testUser.setCoins(5000);

        return userRepo.save(testUser);
    }
}