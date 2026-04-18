package com.gl.rewardservice.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardSummaryDto {
    private Long userId;
    private Integer currentBalance;
    private Integer totalEarned;
    private Integer totalRedeemed;
    private List<RewardResponseDto> history;
}