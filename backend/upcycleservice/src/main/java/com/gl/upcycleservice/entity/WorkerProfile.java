package com.gl.upcycleservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "worker_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String workerName;

    @Column(nullable = false)
    private String skills;

    @Column(nullable = false, length = 1000)
    private String bio;

    // ✅ ADD THIS (important for UI)
    private Integer experienceYears;

    // ✅ DEFAULT VALUES (VERY IMPORTANT)
    private Boolean otpVerified = false;

    private Boolean adminApproved = false;

    private Boolean verifiedBadge = false;

    private Double rating = 0.0;

    private String otpCode;

    // ================= PRICING =================

    private Double laundryPricePerItem;

    private Double ironingPricePerItem;

    private Double dryCleaningPricePerItem;

    private Double stitchingPricePerItem;

    private Double alterationPricePerItem;
}