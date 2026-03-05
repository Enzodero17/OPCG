package com.dero.opcg_api.controller;

import com.dero.opcg_api.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RewardController {

    private final RewardService rewardService;

    @PostMapping("/daily/{userId}")
    public String claimDaily(@PathVariable UUID userId) {
        return rewardService.claimDailyReward(userId);
    }
}