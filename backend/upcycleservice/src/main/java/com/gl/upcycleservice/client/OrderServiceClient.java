package com.gl.upcycleservice.client;

import com.gl.upcycleservice.config.FeignConfig;
import com.gl.upcycleservice.dto.OrderRequestDto;
import com.gl.upcycleservice.dto.OrderResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "ORDER-SERVICE",
        url = "http://localhost:8085",
        configuration = FeignConfig.class
)
public interface OrderServiceClient {

    @PostMapping("/api/orders")
    OrderResponseDto createOrder(@RequestBody OrderRequestDto request);
}