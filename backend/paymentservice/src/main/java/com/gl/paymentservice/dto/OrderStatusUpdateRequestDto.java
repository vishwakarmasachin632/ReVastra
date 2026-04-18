package com.gl.paymentservice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusUpdateRequestDto {
    private String status;

    private String paymentStatus;
    private LocalDateTime paymentTime;
}