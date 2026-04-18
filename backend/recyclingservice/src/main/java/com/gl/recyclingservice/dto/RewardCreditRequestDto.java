package com.gl.recyclingservice.dto;

import com.gl.recyclingservice.enums.RewardSource;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardCreditRequestDto {
    private Long userId;
    private Integer points;
    private RewardSource source;
    private String note;
}