package com.dero.opcg_api.controller;

import com.dero.opcg_api.dto.RewardResponseDto;
import com.dero.opcg_api.model.User;
import com.dero.opcg_api.model.UserMission;
import com.dero.opcg_api.repository.UserMissionRepository;
import com.dero.opcg_api.repository.UserRepository;
import com.dero.opcg_api.security.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {

    private final UserMissionRepository userMissionRepo;
    private final UserRepository userRepo;
    private final SecurityUtils securityUtils;

    // Afficher le journal de quêtes du joueur
    @GetMapping("/{userId}")
    public List<UserMission> getUserMissions(@PathVariable UUID userId) {
        securityUtils.requireSelf(userId);
        return userMissionRepo.findByUserId(userId);
    }

    // Réclamer la récompense d'une mission terminée
    @Transactional
    @PostMapping("/{userId}/claim/{missionId}")
    public RewardResponseDto claimReward(@PathVariable UUID userId, @PathVariable Long missionId) {
        securityUtils.requireSelf(userId);

        UserMission userMission = userMissionRepo.findByUserIdAndMissionId(userId, missionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mission introuvable."));

        if (!userMission.isCompleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette mission n'est pas encore terminée.");
        }

        if (userMission.isClaimed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tu as déjà récupéré cette récompense.");
        }

        // On paie le joueur
        User user = userMission.getUser();
        int reward = userMission.getMission().getRewardCoins();
        user.setCoins(user.getCoins() + reward);
        userRepo.save(user);

        // On marque la récompense comme récupérée
        userMission.setClaimed(true);
        userMissionRepo.save(userMission);

        return new RewardResponseDto("Bravo ! Tu as gagné " + reward + " pièces.", user.getCoins());
    }
}