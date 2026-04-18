package com.gl.orderservice.orderservice.dto;

import com.gl.orderservice.orderservice.entity.enums.OrderStatus;
import com.gl.orderservice.orderservice.entity.enums.ServiceType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {

    private Long id;
    private Long userId;
    private ServiceType serviceType;
    private OrderStatus status;
    private BigDecimal totalAmount;

    // consumer info
    private String consumerName;
    private String consumerPhone;

    // payment info
    private String paymentStatus;
    private LocalDateTime paymentTime;

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}