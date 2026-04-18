package com.gl.laundryservice.dto;

import com.gl.laundryservice.enums.OrderServiceType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDto {
    private OrderServiceType serviceType;
    private Long workerId;
    private BigDecimal totalAmount;
}