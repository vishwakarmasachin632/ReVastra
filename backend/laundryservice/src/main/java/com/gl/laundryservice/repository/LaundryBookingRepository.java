package com.gl.laundryservice.repository;

import com.gl.laundryservice.entity.LaundryBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LaundryBookingRepository extends JpaRepository<LaundryBooking, Long> {
    List<LaundryBooking> findByUserId(Long userId);
}