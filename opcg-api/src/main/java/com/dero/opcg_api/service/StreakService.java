package com.dero.opcg_api.service;

import com.dero.opcg_api.model.User;
import com.dero.opcg_api.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StreakService {

    private final UserRepository userRepo;
    private final MissionService missionService;
    private static final ZoneOffset STREAK_ZONE = ZoneOffset.UTC;

    @Transactional
    public void recordLogin(UUID userId) {
        User user = userRepo.findById(userId).orElse(null);

        if (user == null) {
            return;
        }

        LocalDate today = LocalDate.now(STREAK_ZONE);
        LocalDate lastLogin = user.getLastLoginDate();

        if (lastLogin != null && lastLogin.isEqual(today)) {
            return;
        }

        if (lastLogin != null && lastLogin.isEqual(today.minusDays(1))) {
            user.setCurrentLoginStreak(user.getCurrentLoginStreak() + 1);
        } else {
            user.setCurrentLoginStreak(1);
        }

        if (user.getCurrentLoginStreak() > user.getLongestLoginStreak()) {
            user.setLongestLoginStreak(user.getCurrentLoginStreak());
        }

        user.setLastLoginDate(today);
        userRepo.save(user);

        missionService.updatePeakProgress(userId, "LOGIN_STREAK", user.getCurrentLoginStreak());
        missionService.processAction(userId, "LOGIN_STREAK", 1);
    }
}
