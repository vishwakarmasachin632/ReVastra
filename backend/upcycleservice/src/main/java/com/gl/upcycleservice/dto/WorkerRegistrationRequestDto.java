package com.gl.upcycleservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerRegistrationRequestDto {

    @NotBlank(message = "Skills are required")
    @Size(min = 3, max = 255, message = "Skills must be between 3 and 255 characters")
    private String skills;

    @NotBlank(message = "Bio is required")
    @Size(min = 10, max = 1000, message = "Bio must be between 10 and 1000 characters")
    private String bio;

    @Min(value = 0, message = "Experience cannot be negative")
    @Max(value = 50, message = "Experience seems unrealistic")
    private Integer experienceYears;

    @DecimalMin(value = "0.0", inclusive = false, message = "Laundry price must be greater than 0")
    private Double laundryPricePerItem;

    @DecimalMin(value = "0.0", inclusive = false, message = "Ironing price must be greater than 0")
    private Double ironingPricePerItem;

    @DecimalMin(value = "0.0", inclusive = false, message = "Dry cleaning price must be greater than 0")
    private Double dryCleaningPricePerItem;

    @DecimalMin(value = "0.0", inclusive = false, message = "Stitching price must be greater than 0")
    private Double stitchingPricePerItem;

    @DecimalMin(value = "0.0", inclusive = false, message = "Alteration price must be greater than 0")
    private Double alterationPricePerItem;
}