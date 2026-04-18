package com.gl.recyclingservice.repository;

import com.gl.recyclingservice.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByUserIdOrderByCreatedAtDesc(Long userId);
}