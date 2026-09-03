package com.dero.opcg_api.controller;

import com.dero.opcg_api.model.Mission;
import com.dero.opcg_api.model.MissionCategory;
import com.dero.opcg_api.model.User;
import com.dero.opcg_api.repository.MissionRepository;
import com.dero.opcg_api.repository.UserRepository;
import com.dero.opcg_api.service.CardSyncService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CardSyncService syncService;
    private final UserRepository userRepo;
    private final MissionRepository missionRepo;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/sync")
    public String triggerSync() {
        return syncService.syncCardsFromApi();
    }

    @GetMapping("/create-test-user")
    public User createTestUser() {
        User testUser = new User();
        testUser.setUsername("MugiwaraTest");
        testUser.setEmail("luffy@test.com");
        testUser.setPasswordHash(passwordEncoder.encode("motdepasse_temporaire"));
        testUser.setCoins(5000);

        return userRepo.save(testUser);
    }

    @GetMapping("/init-missions")
    public String initMissions() {
        List<Mission> catalog = buildMissionCatalog();

        int created = 0;
        for (Mission mission : catalog) {
            if (!missionRepo.existsByTitle(mission.getTitle())) {
                missionRepo.save(mission);
                created++;
            }
        }

        return created + " nouvelle(s) mission(s) créée(s) sur " + catalog.size() + " au total dans le catalogue.";
    }

    private List<Mission> buildMissionCatalog() {
        return List.of(
                mission("Première ouverture", "Ouvre ton tout premier booster !", 1000, "OPEN_BOOSTER", 1, MissionCategory.PERMANENT, 1, null),
                mission("Collectionneur amateur", "Ouvre 10 boosters au total.", 3000, "OPEN_BOOSTER", 10, MissionCategory.PERMANENT, 2, null),
                mission("Collectionneur compulsif", "Ouvre 50 boosters au total.", 10000, "OPEN_BOOSTER", 50, MissionCategory.PERMANENT, 3, null),
                mission("Collectionneur invétéré", "Ouvre 200 boosters au total.", 40000, "OPEN_BOOSTER", 200, MissionCategory.PERMANENT, 4, null),
                mission("Légende vivante", "Ouvre 1000 boosters au total.", 250000, "OPEN_BOOSTER", 1000, MissionCategory.PERMANENT, 5, null),

                mission("Marchand amateur", "Vends 5 cartes.", 2000, "SELL_CARD", 5, MissionCategory.PERMANENT, 1, null),
                mission("Marchand aguerri", "Vends 25 cartes.", 8000, "SELL_CARD", 25, MissionCategory.PERMANENT, 2, null),
                mission("Négociant", "Vends 100 cartes.", 25000, "SELL_CARD", 100, MissionCategory.PERMANENT, 3, null),
                mission("Magnat du marché", "Vends 500 cartes.", 100000, "SELL_CARD", 500, MissionCategory.PERMANENT, 4, null),

                mission("Première visite", "Réclame ta première récompense quotidienne.", 500, "CLAIM_DAILY_REWARD", 1, MissionCategory.PERMANENT, 1, null),
                mission("Habitué", "Réclame ta récompense quotidienne 7 fois.", 3000, "CLAIM_DAILY_REWARD", 7, MissionCategory.PERMANENT, 2, null),
                mission("Fidèle", "Réclame ta récompense quotidienne 30 fois.", 15000, "CLAIM_DAILY_REWARD", 30, MissionCategory.PERMANENT, 3, null),
                mission("Pilier de la communauté", "Réclame ta récompense quotidienne 100 fois.", 50000, "CLAIM_DAILY_REWARD", 100, MissionCategory.PERMANENT, 4, null),

                mission("Petit débutant", "Possède 10 cartes uniques différentes.", 1000, "UNIQUE_CARDS_OWNED", 10, MissionCategory.PERMANENT, 1, null),
                mission("Collection grandissante", "Possède 50 cartes uniques différentes.", 5000, "UNIQUE_CARDS_OWNED", 50, MissionCategory.PERMANENT, 2, null),
                mission("Belle vitrine", "Possède 150 cartes uniques différentes.", 20000, "UNIQUE_CARDS_OWNED", 150, MissionCategory.PERMANENT, 3, null),
                mission("Encyclopédie ambulante", "Possède 300 cartes uniques différentes.", 60000, "UNIQUE_CARDS_OWNED", 300, MissionCategory.PERMANENT, 4, null),

                mission("Session d'ouverture", "Ouvre 5 boosters cette semaine.", 2500, "OPEN_BOOSTER", 5, MissionCategory.WEEKLY, 1, null),
                mission("Ménage de printemps", "Vends 10 cartes cette semaine.", 3000, "SELL_CARD", 10, MissionCategory.WEEKLY, 1, null),
                mission("Assidu de la semaine", "Connecte-toi 5 jours différents cette semaine.", 4000, "LOGIN_DAY", 5, MissionCategory.WEEKLY, 1, null),

                mission("Première rareté", "Obtiens ta première carte Peu Commune.", 200, "FIRST_CARD_UC", 1, MissionCategory.ACHIEVEMENT, 1, "🟢"),
                mission("Ça brille !", "Obtiens ta première carte Rare.", 500, "FIRST_CARD_R", 1, MissionCategory.ACHIEVEMENT, 1, "🔵"),
                mission("Éclat rare", "Obtiens ta première carte Super Rare.", 1500, "FIRST_CARD_SR", 1, MissionCategory.ACHIEVEMENT, 1, "🟣"),
                mission("Trésor", "Obtiens ta première carte Secret Rare.", 5000, "FIRST_CARD_SEC", 1, MissionCategory.ACHIEVEMENT, 1, "🟡"),
                mission("Meneur", "Obtiens ton premier Leader.", 1000, "FIRST_CARD_L", 1, MissionCategory.ACHIEVEMENT, 1, "👑"),
                mission("Semaine parfaite", "Connecte-toi 7 jours d'affilée.", 5000, "LOGIN_STREAK", 7, MissionCategory.ACHIEVEMENT, 1, "🔥"),
                mission("Un mois entier", "Connecte-toi 30 jours d'affilée.", 20000, "LOGIN_STREAK", 30, MissionCategory.ACHIEVEMENT, 2, "🏆")
        );
    }

    private Mission mission(String title, String description, int rewardCoins, String actionType,
                            int targetAmount, MissionCategory category, int tier, String badgeIcon) {
        Mission m = new Mission();
        m.setTitle(title);
        m.setDescription(description);
        m.setRewardCoins(rewardCoins);
        m.setActionType(actionType);
        m.setTargetAmount(targetAmount);
        m.setCategory(category);
        m.setTier(tier);
        m.setBadgeIcon(badgeIcon);
        return m;
    }
}