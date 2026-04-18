package com.gl.rewardservice.dto;

import com.gl.rewardservice.enums.RewardSource;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardCreditRequestDto {

    @NotNull(message = "User id is required")
    private Long userId;

    @NotNull(message = "Points are required")
    @Min(value = 1, message = "Points must be at least 1")
    private Integer points;

    @NotNull(message = "Reward source is required")
    private RewardSource source;

    private String note;
}