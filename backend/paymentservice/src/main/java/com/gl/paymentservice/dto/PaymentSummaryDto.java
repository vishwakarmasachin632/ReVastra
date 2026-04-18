package com.gl.paymentservice.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSummaryDto {

    private long totalPayments;
    private BigDecimal totalEarnings;
}