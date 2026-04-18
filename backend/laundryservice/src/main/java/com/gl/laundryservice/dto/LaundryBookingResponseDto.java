package com.gl.laundryservice.dto;

import com.gl.laundryservice.enums.LaundryStatus;
import com.gl.laundryservice.enums.LaundryType;
import com.gl.laundryservice.enums.PickupTimeSlot;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaundryBookingResponseDto {

    private Long id;
    private Long userId;
    private Long workerId;
    private Long orderId;
    private LaundryType laundryType;
    private Integer itemCount;
    private String pickupAddress;
    private String deliveryAddress;
    private LocalDate pickupDate;
    private PickupTimeSlot pickupTimeSlot;
    private LocalDate estimatedDeliveryDate;
    private BigDecimal estimatedPrice;
    private LaundryStatus status;
    private LocalDateTime createdAt;
}