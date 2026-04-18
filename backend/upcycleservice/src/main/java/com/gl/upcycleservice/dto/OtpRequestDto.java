package com.gl.upcycleservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpRequestDto {

    @NotNull(message = "Worker profile id is required")
    private Long workerProfileId;
}