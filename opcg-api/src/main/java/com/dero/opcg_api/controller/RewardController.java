package com.dero.opcg_api.controller;

import com.dero.opcg_api.dto.RewardResponseDto;
import com.dero.opcg_api.security.SecurityUtils;
import com.dero.opcg_api.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;
    private final SecurityUtils securityUtils;

    @PostMapping("/daily/{userId}")
    public RewardResponseDto claimDaily(@PathVariable UUID userId) {
        securityUtils.requireSelf(userId);
        return rewardService.claimDailyReward(userId);
    }
}