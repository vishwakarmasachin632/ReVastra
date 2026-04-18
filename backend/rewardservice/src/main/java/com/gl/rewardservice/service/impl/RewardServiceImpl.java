package com.gl.rewardservice.service.impl;

import com.gl.rewardservice.client.UserServiceClient;
import com.gl.rewardservice.dto.*;
import com.gl.rewardservice.entity.Reward;
import com.gl.rewardservice.enums.RewardSource;
import com.gl.rewardservice.exception.InsufficientPointsException;
import com.gl.rewardservice.repository.RewardRepository;
import com.gl.rewardservice.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {

    private final RewardRepository rewardRepository;
    private final UserServiceClient userServiceClient;

    @Override
    public RewardResponseDto creditPoints(RewardCreditRequestDto request) {
        int currentBalance = getCurrentBalance(request.getUserId());
        int newBalance = currentBalance + request.getPoints();

        Reward reward = Reward.builder()
                .userId(request.getUserId())
                .pointsEarned(request.getPoints())
                .pointsRedeemed(0)
                .balance(newBalance)
                .source(request.getSource())
                .note(request.getNote())
                .createdAt(LocalDateTime.now())
                .build();

        Reward saved = rewardRepository.save(reward);
        return mapToDto(saved);
    }

    @Override
    public RewardResponseDto redeemPoints(RewardRedeemRequestDto request) {
        int currentBalance = getCurrentBalance(request.getUserId());

        if (request.getPointsToRedeem() > currentBalance) {
            throw new InsufficientPointsException("Insufficient reward points");
        }

        int newBalance = currentBalance - request.getPointsToRedeem();

        Reward reward = Reward.builder()
                .userId(request.getUserId())
                .pointsEarned(0)
                .pointsRedeemed(request.getPointsToRedeem())
                .balance(newBalance)
                .source(RewardSource.REDEEM)
                .note(request.getNote())
                .createdAt(LocalDateTime.now())
                .build();

        Reward saved = rewardRepository.save(reward);
        return mapToDto(saved);
    }

    @Override
    public RewardSummaryDto getMyRewards() {
        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();
        return buildSummary(currentUser.getId());
    }

    @Override
    public RewardSummaryDto getRewardsByUserId(Long userId) {
        return buildSummary(userId);
    }

    private RewardSummaryDto buildSummary(Long userId) {
        List<Reward> rewards = rewardRepository.findByUserIdOrderByCreatedAtDesc(userId);

        int totalEarned = rewards.stream()
                .mapToInt(r -> r.getPointsEarned() == null ? 0 : r.getPointsEarned())
                .sum();

        int totalRedeemed = rewards.stream()
                .mapToInt(r -> r.getPointsRedeemed() == null ? 0 : r.getPointsRedeemed())
                .sum();

        int currentBalance = rewards.isEmpty() ? 0 : rewards.get(0).getBalance();

        return RewardSummaryDto.builder()
                .userId(userId)
                .currentBalance(currentBalance)
                .totalEarned(totalEarned)
                .totalRedeemed(totalRedeemed)
                .history(rewards.stream().map(this::mapToDto).toList())
                .build();
    }

    private int getCurrentBalance(Long userId) {
        return rewardRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .map(Reward::getBalance)
                .orElse(0);
    }

    private RewardResponseDto mapToDto(Reward reward) {
        return RewardResponseDto.builder()
                .id(reward.getId())
                .userId(reward.getUserId())
                .pointsEarned(reward.getPointsEarned())
                .pointsRedeemed(reward.getPointsRedeemed())
                .balance(reward.getBalance())
                .source(reward.getSource())
                .note(reward.getNote())
                .createdAt(reward.getCreatedAt())
                .build();
    }
}