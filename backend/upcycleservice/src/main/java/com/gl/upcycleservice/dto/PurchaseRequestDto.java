package com.gl.upcycleservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseRequestDto {

    @NotNull(message = "Product id is required")
    private Long productId;
}