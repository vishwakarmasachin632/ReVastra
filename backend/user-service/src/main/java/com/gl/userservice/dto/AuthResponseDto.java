package com.gl.userservice.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {

    private String token;
    private String message;
    private Long userId;
    private String name;
    private String email;
    private String role;
    private Boolean verified;
}