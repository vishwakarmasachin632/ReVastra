package com.gl.upcycleservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpVerifyRequestDto {

    @NotNull(message = "Worker profile id is required")
    private Long workerProfileId;

    @NotBlank(message = "OTP is required")
    private String otp;
}