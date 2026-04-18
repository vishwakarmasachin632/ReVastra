package com.gl.rewardservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardRedeemRequestDto {

    @NotNull(message = "User id is required")
    private Long userId;

    @NotNull(message = "Points to redeem are required")
    @Min(value = 1, message = "Redeem points must be at least 1")
    private Integer pointsToRedeem;

    private String note;
}