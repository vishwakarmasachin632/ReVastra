package com.gl.paymentservice.client;

import com.gl.paymentservice.config.FeignConfig;
import com.gl.paymentservice.dto.RewardRedeemRequestDto;
import com.gl.paymentservice.dto.RewardResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "REWARD-SERVICE",
        url = "http://localhost:8086",
        configuration = FeignConfig.class
)
public interface RewardServiceClient {

    @PostMapping("/api/rewards/redeem")
    RewardResponseDto redeemPoints(@RequestBody RewardRedeemRequestDto request);
}