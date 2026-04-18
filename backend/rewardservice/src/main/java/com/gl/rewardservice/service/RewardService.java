package com.gl.rewardservice.service;

import com.gl.rewardservice.dto.RewardCreditRequestDto;
import com.gl.rewardservice.dto.RewardRedeemRequestDto;
import com.gl.rewardservice.dto.RewardResponseDto;
import com.gl.rewardservice.dto.RewardSummaryDto;

public interface RewardService {

    RewardResponseDto creditPoints(RewardCreditRequestDto request);

    RewardResponseDto redeemPoints(RewardRedeemRequestDto request);

    RewardSummaryDto getMyRewards();

    RewardSummaryDto getRewardsByUserId(Long userId);
}