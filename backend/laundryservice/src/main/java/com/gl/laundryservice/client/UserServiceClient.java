package com.gl.laundryservice.client;

import com.gl.laundryservice.config.FeignConfig;
import com.gl.laundryservice.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "USER-SERVICE",
        url = "http://localhost:8082",
        configuration = FeignConfig.class
)
public interface UserServiceClient {

    @GetMapping("/api/users/profile")
    UserResponseDto getCurrentUserProfile();

    @GetMapping("/api/workers")
    List<UserResponseDto> getVerifiedWorkers();

    @GetMapping("/api/workers/{userId}")
    UserResponseDto getWorkerById(@PathVariable Long userId);
}