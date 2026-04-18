package com.gl.notificationservice.client;

import com.gl.notificationservice.config.FeignConfig;
import com.gl.notificationservice.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "USER-SERVICE",
        url = "http://localhost:8082",
        configuration = FeignConfig.class
)
public interface UserServiceClient {

    @GetMapping("/api/users/profile")
    UserResponseDto getCurrentUserProfile();
}