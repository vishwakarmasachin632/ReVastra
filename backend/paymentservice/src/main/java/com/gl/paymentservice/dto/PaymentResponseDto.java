package com.gl.paymentservice.dto;

import com.gl.paymentservice.enums.PaymentMethod;
import com.gl.paymentservice.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDto {
    private Long id;
    private Long orderId;
    private Long userId;
    private BigDecimal originalAmount;
    private BigDecimal walletUsed;
    private Integer rewardPointsUsed;
    private BigDecimal finalAmountPaid;
    private PaymentMethod method;
    private PaymentStatus status;
    private String transactionRef;
    private LocalDateTime createdAt;
}