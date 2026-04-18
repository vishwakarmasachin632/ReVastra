package com.gl.recyclingservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationRequestDto {

    @NotNull(message = "Item count is required")
    @Min(value = 1, message = "Item count must be at least 1")
    private Integer itemCount;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Pickup address is required")
    private String pickupAddress;

    @NotBlank(message = "Item type is required")
    private String itemType;
}