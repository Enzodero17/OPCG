package com.dero.opcg_api.controller;

import com.dero.opcg_api.model.Mission;
import com.dero.opcg_api.model.User;
import com.dero.opcg_api.repository.MissionRepository;
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
    private final MissionRepository missionRepo;

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

    @GetMapping("/init-missions")
    public String initMissions() {
        if (missionRepo.count() > 0) {
            return "Les missions existent déjà dans la base de données !";
        }

        Mission m1 = new Mission();
        m1.setTitle("Première ouverture");
        m1.setDescription("Ouvre ton tout premier booster !");
        m1.setRewardCoins(1000);
        m1.setActionType("OPEN_BOOSTER");
        m1.setTargetAmount(1);
        missionRepo.save(m1);

        Mission m2 = new Mission();
        m2.setTitle("Collectionneur compulsif");
        m2.setDescription("Ouvre 10 boosters au total.");
        m2.setRewardCoins(5000);
        m2.setActionType("OPEN_BOOSTER");
        m2.setTargetAmount(10);
        missionRepo.save(m2);

        Mission m3 = new Mission();
        m3.setTitle("Marchand amateur");
        m3.setDescription("Vends 5 cartes.");
        m3.setRewardCoins(2000);
        m3.setActionType("SELL_CARD");
        m3.setTargetAmount(5);
        missionRepo.save(m3);

        return "Les 3 missions de base ont été créées avec succès !";
    }
}