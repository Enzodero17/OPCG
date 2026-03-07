package com.dero.opcg_api.controller;

import com.dero.opcg_api.model.User;
import com.dero.opcg_api.model.UserMission;
import com.dero.opcg_api.repository.UserMissionRepository;
import com.dero.opcg_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MissionController {

    private final UserMissionRepository userMissionRepo;
    private final UserRepository userRepo;

    // Afficher le journal de quêtes du joueur
    @GetMapping("/{userId}")
    public List<UserMission> getUserMissions(@PathVariable UUID userId) {
        return userMissionRepo.findByUserId(userId);
    }

    // Réclamer la récompense d'une mission terminée
    @PostMapping("/{userId}/claim/{missionId}")
    public String claimReward(@PathVariable UUID userId, @PathVariable Long missionId) {

        UserMission userMission = userMissionRepo.findByUserIdAndMissionId(userId, missionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mission introuvable."));

        if (!userMission.isCompleted()) {
            return "Tricheur ! Cette mission n'est pas encore terminée.";
        }

        if (userMission.isClaimed()) {
            return "Tu as déjà récupéré cette récompense.";
        }

        // On paie le joueur
        User user = userMission.getUser();
        int reward = userMission.getMission().getRewardCoins();
        user.setCoins(user.getCoins() + reward);
        userRepo.save(user);

        // On marque la récompense comme récupérée
        userMission.setClaimed(true);
        userMissionRepo.save(userMission);

        return "Bravo ! Tu as gagné " + reward + " pièces. Ton nouveau solde est de " + user.getCoins() + " pièces.";
    }
}