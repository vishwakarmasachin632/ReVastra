package com.gl.recyclingservice.client;

import com.gl.recyclingservice.config.FeignConfig;
import com.gl.recyclingservice.dto.RecyclingPointsResponseDto;
import com.gl.recyclingservice.dto.RewardCreditRequestDto;
import com.gl.recyclingservice.dto.RewardResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "REWARD-SERVICE",
        url = "http://localhost:8086",
        configuration = FeignConfig.class
)
public interface RewardServiceClient {

    @PostMapping("/api/rewards/credit")
    RewardResponseDto creditPoints(@RequestBody RewardCreditRequestDto request);

    @GetMapping("/api/rewards/user/{userId}")
    RecyclingPointsResponseDto getRewardsByUserId(@PathVariable Long userId);
}