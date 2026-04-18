package com.gl.upcycleservice.repository;

import com.gl.upcycleservice.entity.WorkerReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkerReviewRepository extends JpaRepository<WorkerReview, Long> {

    List<WorkerReview> findByWorkerProfileIdOrderByCreatedAtDesc(Long workerProfileId);

    Optional<WorkerReview> findByOrderIdAndUserId(Long orderId, Long userId);

    long countByWorkerProfileId(Long workerProfileId);
}