package com.gl.laundryservice.dto;

import com.gl.laundryservice.enums.LaundryType;
import com.gl.laundryservice.enums.PickupTimeSlot;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaundryBookingRequestDto {

    @NotNull(message = "Worker id is required")
    private Long workerId;

    @NotNull(message = "Laundry type is required")
    private LaundryType laundryType;

    @NotNull(message = "Item count is required")
    @Min(value = 1, message = "Item count must be at least 1")
    private Integer itemCount;

    @NotBlank(message = "Pickup address is required")
    private String pickupAddress;

    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    @NotNull(message = "Pickup date is required")
    private LocalDate pickupDate;

    @NotNull(message = "Pickup time slot is required")
    private PickupTimeSlot pickupTimeSlot;

    @NotNull(message = "Estimated price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Estimated price must be greater than 0")
    private BigDecimal estimatedPrice;
}