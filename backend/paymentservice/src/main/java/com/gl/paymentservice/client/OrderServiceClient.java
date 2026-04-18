package com.gl.paymentservice.client;

import com.gl.paymentservice.config.FeignConfig;
import com.gl.paymentservice.dto.OrderResponseDto;
import com.gl.paymentservice.dto.OrderStatusUpdateRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "ORDER-SERVICE",
        url = "http://localhost:8085",
        configuration = FeignConfig.class
)
public interface OrderServiceClient {

    @GetMapping("/api/orders/{orderId}")
    OrderResponseDto getOrderById(@PathVariable Long orderId);

    @PutMapping("/api/orders/{orderId}/status")
    OrderResponseDto updateOrderStatus(
            @PathVariable("orderId") Long orderId,
            @RequestBody OrderStatusUpdateRequestDto request
    );
}