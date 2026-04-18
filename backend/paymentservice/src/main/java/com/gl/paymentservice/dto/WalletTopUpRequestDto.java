package com.gl.paymentservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletTopUpRequestDto {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.1", inclusive = true, message = "Top-up amount must be greater than 0")
    private BigDecimal amount;
}