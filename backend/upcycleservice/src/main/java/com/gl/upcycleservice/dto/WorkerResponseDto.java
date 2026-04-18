package com.gl.upcycleservice.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerResponseDto {

    private Long id;
    private Long userId;
    private String workerName;
    private String skills;
    private String phone;
    private String bio;

    private Boolean otpVerified;
    private Boolean adminApproved;
    private Boolean verifiedBadge;

    private Double rating;

    // ✅ ADD THIS (important for UI)
    private Integer experienceYears;

    // ✅ SERVICE PRICING (IMPORTANT FOR YOUR REQUIREMENT)
    private Double laundryPricePerItem;
    private Double ironingPricePerItem;
    private Double dryCleaningPricePerItem;
    private Double stitchingPricePerItem;
    private Double alterationPricePerItem;

    // ✅ REVIEWS (YOU ALREADY HAVE THIS 👍)
    private Long reviewCount;
    private List<WorkerReviewResponseDto> reviews;
}