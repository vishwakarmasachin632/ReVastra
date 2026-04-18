package com.gl.userservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String address;
    private Double walletBalance;
    private Boolean verified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}