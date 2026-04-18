package com.gl.upcycleservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "worker_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long workerProfileId;
    private Long userId;
    private Long orderId;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 1000)
    private String review;

    private LocalDateTime createdAt;
}