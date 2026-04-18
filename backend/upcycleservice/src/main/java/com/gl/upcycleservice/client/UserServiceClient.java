package com.gl.upcycleservice.client;

import com.gl.upcycleservice.config.FeignConfig;
import com.gl.upcycleservice.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "USER-SERVICE",
        url = "http://localhost:8082",
        configuration = FeignConfig.class
)
public interface UserServiceClient {

    @GetMapping("/api/users/profile")
    UserResponseDto getCurrentUserProfile();

    @GetMapping("/api/workers/{userId}")
    UserResponseDto getUserById(@PathVariable("userId") Long userId);
}