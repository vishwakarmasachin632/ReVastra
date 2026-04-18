package com.gl.rewardservice.entity;

import com.gl.rewardservice.enums.RewardSource;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rewards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Integer pointsEarned;

    private Integer pointsRedeemed;

    private Integer balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardSource source;

    private String note;

    private LocalDateTime createdAt;
}