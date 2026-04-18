package com.gl.upcycleservice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerReviewResponseDto {

    private Long id;
    private Long workerProfileId;
    private Long userId;
    private Long orderId;
    private Integer rating;
    private String review;
    private LocalDateTime createdAt;
}