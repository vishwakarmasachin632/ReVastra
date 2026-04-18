package com.gl.recyclingservice.dto;

import com.gl.recyclingservice.enums.DonationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationResponseDto {

    private Long id;
    private Long userId;
    private Integer itemCount;
    private String description;
    private String pickupAddress;
    private String itemType;
    private Integer pointsEarned;
    private DonationStatus status;
    private LocalDateTime createdAt;
}