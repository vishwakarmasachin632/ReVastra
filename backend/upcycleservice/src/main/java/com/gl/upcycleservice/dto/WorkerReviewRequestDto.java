package com.gl.upcycleservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerReviewRequestDto {

    private Long workerProfileId;
    private Long orderId;
    private Integer rating;
    private String review;
}