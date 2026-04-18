package com.gl.orderservice.orderservice.dto;


import com.gl.orderservice.orderservice.entity.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusUpdateRequestDto {

    @NotNull(message = "Order status is required")
    private OrderStatus status;

    private String paymentStatus;
    private LocalDateTime paymentTime;
}