package com.gl.userservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerProfileResponseDto {

    private Long id;
    private Long userId;
    private Boolean adminApproved;
    private Boolean verifiedBadge;
}