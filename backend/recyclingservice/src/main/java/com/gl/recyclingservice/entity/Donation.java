package com.gl.recyclingservice.entity;

import com.gl.recyclingservice.enums.DonationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "donations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Column(nullable = false)
    private Integer itemCount;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String pickupAddress;

    @Column(nullable = false)
    private String itemType;

    @Column(nullable = false)
    private Integer pointsEarned;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DonationStatus status;

    private LocalDateTime createdAt;
}