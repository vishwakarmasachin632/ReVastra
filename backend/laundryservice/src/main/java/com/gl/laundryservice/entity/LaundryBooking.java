package com.gl.laundryservice.entity;

import com.gl.laundryservice.enums.LaundryStatus;
import com.gl.laundryservice.enums.LaundryType;
import com.gl.laundryservice.enums.PickupTimeSlot;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "laundry_bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaundryBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long workerId;

    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LaundryType laundryType;

    @Column(nullable = false)
    private Integer itemCount;

    @Column(nullable = false)
    private String pickupAddress;

    @Column(nullable = false)
    private String deliveryAddress;

    @Column(name = "pickup_date", nullable = false)
    private LocalDate pickupDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "pickup_time_slot", nullable = false)
    private PickupTimeSlot pickupTimeSlot;

    @Column(name = "estimated_delivery_date", nullable = false)
    private LocalDate estimatedDeliveryDate;

    @Column(nullable = false)
    private BigDecimal estimatedPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LaundryStatus status;

    private LocalDateTime createdAt;
}