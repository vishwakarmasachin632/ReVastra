package com.gl.recyclingservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecyclingPointsResponseDto {
    private Long userId;
    private Integer currentBalance;
    private Integer totalEarned;
    private Integer totalRedeemed;
}