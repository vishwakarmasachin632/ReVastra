package com.gl.paymentservice.repository;

import com.gl.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT COALESCE(SUM(p.finalAmountPaid), 0) FROM Payment p WHERE p.status = com.gl.paymentservice.enums.PaymentStatus.SUCCESS")
    BigDecimal getTotalSuccessfulEarnings();

    List<Payment> findAllByOrderByCreatedAtDesc();

    @Query("SELECT COALESCE(SUM(p.originalAmount), 0) FROM Payment p WHERE p.status = com.gl.paymentservice.enums.PaymentStatus.SUCCESS")
    BigDecimal getGrossRevenue();
}