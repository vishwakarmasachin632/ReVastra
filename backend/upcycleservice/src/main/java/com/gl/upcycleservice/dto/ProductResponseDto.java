package com.gl.upcycleservice.dto;

import com.gl.upcycleservice.enums.ProductStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {
    private Long id;
    private Long workerProfileId;
    private String workerName;
    private String title;
    private String description;
    private BigDecimal price;
    private ProductStatus status;
    private LocalDateTime createdAt;
}