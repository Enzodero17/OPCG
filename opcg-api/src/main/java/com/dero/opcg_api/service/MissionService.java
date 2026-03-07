package com.dero.opcg_api.service;

import com.dero.opcg_api.model.Mission;
import com.dero.opcg_api.model.User;
import com.dero.opcg_api.model.UserMission;
import com.dero.opcg_api.repository.MissionRepository;
import com.dero.opcg_api.repository.UserMissionRepository;
import com.dero.opcg_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepo;
    private final UserMissionRepository userMissionRepo;
    private final UserRepository userRepo;

    public void processAction(UUID userId, String actionType, int amountToAdd) {

        User user = userRepo.findById(userId).orElse(null);
        if (user == null) return;

        // On cherche toutes les missions qui demandent cette action précise
        List<Mission> relatedMissions = missionRepo.findByActionType(actionType);

        for (Mission mission : relatedMissions) {

            // On cherche le journal de quête du joueur pour cette mission (s'il n'existe pas, on le crée)
            UserMission userMission = userMissionRepo.findByUserIdAndMissionId(userId, mission.getId())
                    .orElseGet(() -> {
                        UserMission newUm = new UserMission();
                        newUm.setUser(user);
                        newUm.setMission(mission);
                        return newUm;
                    });

            // Si la quête n'est pas encore terminée, on la fait avancer
            if (!userMission.isCompleted()) {
                userMission.setCurrentAmount(userMission.getCurrentAmount() + amountToAdd);

                // A-t-il atteint l'objectif ?
                if (userMission.getCurrentAmount() >= mission.getTargetAmount()) {
                    userMission.setCurrentAmount(mission.getTargetAmount());
                    userMission.setCompleted(true);
                    System.out.println("🎉 Mission accomplie pour " + user.getUsername() + " : " + mission.getTitle());
                }

                // On sauvegarde sa progression
                userMissionRepo.save(userMission);
            }
        }
    }
}
