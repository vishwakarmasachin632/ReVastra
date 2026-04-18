package com.gl.paymentservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletResponseDto {
    private Long id;
    private Long userId;
    private BigDecimal balance;
    private LocalDateTime lastUpdated;
}