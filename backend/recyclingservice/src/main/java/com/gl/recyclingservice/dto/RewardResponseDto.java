package com.gl.recyclingservice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardResponseDto {
    private Long id;
    private Long userId;
    private Integer pointsEarned;
    private Integer pointsRedeemed;
    private Integer balance;
    private String source;
    private String note;
    private LocalDateTime createdAt;
}