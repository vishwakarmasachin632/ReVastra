package com.gl.paymentservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardRedeemRequestDto {
    private Long userId;
    private Integer pointsToRedeem;
    private String note;
}