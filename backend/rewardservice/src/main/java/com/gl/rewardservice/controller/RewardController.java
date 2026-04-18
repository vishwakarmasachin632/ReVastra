package com.gl.rewardservice.controller;

import com.gl.rewardservice.dto.*;
import com.gl.rewardservice.service.RewardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;

    @PostMapping("/credit")
    public ResponseEntity<RewardResponseDto> creditPoints(@Valid @RequestBody RewardCreditRequestDto request) {
        return ResponseEntity.ok(rewardService.creditPoints(request));
    }

    @PostMapping("/redeem")
    public ResponseEntity<RewardResponseDto> redeemPoints(@Valid @RequestBody RewardRedeemRequestDto request) {
        return ResponseEntity.ok(rewardService.redeemPoints(request));
    }

    @GetMapping("/me")
    public ResponseEntity<RewardSummaryDto> getMyRewards() {
        return ResponseEntity.ok(rewardService.getMyRewards());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<RewardSummaryDto> getRewardsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(rewardService.getRewardsByUserId(userId));
    }
}