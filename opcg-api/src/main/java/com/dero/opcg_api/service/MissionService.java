package com.dero.opcg_api.service;

import com.dero.opcg_api.model.Mission;
import com.dero.opcg_api.model.MissionCategory;
import com.dero.opcg_api.model.User;
import com.dero.opcg_api.model.UserMission;
import com.dero.opcg_api.repository.MissionRepository;
import com.dero.opcg_api.repository.UserMissionRepository;
import com.dero.opcg_api.repository.UserRepository;
import com.dero.opcg_api.util.IsoWeekUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.function.IntUnaryOperator;

@Slf4j
@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepo;
    private final UserMissionRepository userMissionRepo;
    private final UserRepository userRepo;

    @Transactional
    public void processAction(UUID userId, String actionType, int amountToAdd) {
        applyToMatchingMissions(userId, actionType, currentAmount -> currentAmount + amountToAdd);
    }

    @Transactional
    public void updatePeakProgress(UUID userId, String actionType, int currentAbsoluteValue) {
        applyToMatchingMissions(userId, actionType, currentAmount -> Math.max(currentAmount, currentAbsoluteValue));
    }

    private void applyToMatchingMissions(UUID userId, String actionType, IntUnaryOperator newAmountFn) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) {
            return;
        }
        
        List<Mission> relatedMissions = missionRepo.findByActionType(actionType);
        
        for (Mission mission : relatedMissions) {
            String periodKey = resolvePeriodKey(mission);
            UserMission userMission = getOrCreateUserMission(user, mission, periodKey);

            if (userMission.isCompleted()) {
                continue;
            }

            int newAmount = newAmountFn.applyAsInt(userMission.getCurrentAmount());
            userMission.setCurrentAmount(newAmount);

            if (userMission.getCurrentAmount() >= mission.getTargetAmount()) {
                userMission.setCurrentAmount(mission.getTargetAmount());
                userMission.setCompleted(true);
                log.info("Mission accomplie pour {} : {}", user.getUsername(), mission.getTitle());
            }

            userMissionRepo.save(userMission);
        }
    }

    private String resolvePeriodKey(Mission mission) {
        return mission.getCategory() == MissionCategory.WEEKLY
                ? IsoWeekUtil.currentWeekKey()
                : IsoWeekUtil.NON_WEEKLY_PERIOD_KEY;
    }

    private UserMission getOrCreateUserMission(User user, Mission mission, String periodKey) {
        return userMissionRepo.findByUserIdAndMissionIdAndPeriodKey(user.getId(), mission.getId(), periodKey)
                .orElseGet(() -> {
                    try {
                        UserMission newUm = new UserMission();
                        newUm.setUser(user);
                        newUm.setMission(mission);
                        newUm.setPeriodKey(periodKey);
                        return userMissionRepo.saveAndFlush(newUm);
                    } catch (DataIntegrityViolationException e) {
                        return userMissionRepo.findByUserIdAndMissionIdAndPeriodKey(user.getId(), mission.getId(), periodKey)
                                .orElseThrow(() -> e);
                    }
                });
    }
}