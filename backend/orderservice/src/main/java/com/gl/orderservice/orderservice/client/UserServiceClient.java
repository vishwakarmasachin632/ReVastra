package com.gl.orderservice.orderservice.client;

import com.gl.orderservice.orderservice.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "USER-SERVICE",
        url = "http://localhost:8082"
)
public interface UserServiceClient {

    @GetMapping("/api/users/profile")
    UserResponseDto getCurrentUserProfile();
}