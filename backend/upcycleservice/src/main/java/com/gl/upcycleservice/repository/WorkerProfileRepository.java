package com.gl.upcycleservice.repository;

import com.gl.upcycleservice.entity.WorkerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkerProfileRepository extends JpaRepository<WorkerProfile, Long> {
    Optional<WorkerProfile> findByUserId(Long userId);
    List<WorkerProfile> findByVerifiedBadgeTrue();

    List<WorkerProfile> findByAdminApprovedTrue();
    List<WorkerProfile> findByOtpVerifiedTrueAndAdminApprovedFalse();
}