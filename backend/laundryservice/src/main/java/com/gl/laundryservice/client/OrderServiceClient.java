package com.gl.laundryservice.client;

import com.gl.laundryservice.config.FeignConfig;
import com.gl.laundryservice.dto.OrderRequestDto;
import com.gl.laundryservice.dto.OrderResponseDto;
import com.gl.laundryservice.dto.OrderStatusUpdateRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "ORDER-SERVICE",
        url = "http://localhost:8085",
        configuration = FeignConfig.class
)
public interface OrderServiceClient {

    @PostMapping("/api/orders")
    OrderResponseDto createOrder(@RequestBody OrderRequestDto request);

    @PutMapping("/api/orders/{orderId}/status")
    OrderResponseDto updateOrderStatus(@PathVariable Long orderId,
                                       @RequestBody OrderStatusUpdateRequestDto request);
}