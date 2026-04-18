package com.gl.upcycleservice.dto;

import com.gl.upcycleservice.enums.ServiceType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDto {
    private ServiceType serviceType;
    private Long workerId;
    private BigDecimal totalAmount;
}