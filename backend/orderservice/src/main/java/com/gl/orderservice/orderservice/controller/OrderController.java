package com.gl.orderservice.orderservice.controller;


import com.gl.orderservice.orderservice.dto.OrderRequestDto;
import com.gl.orderservice.orderservice.dto.OrderResponseDto;
import com.gl.orderservice.orderservice.dto.OrderStatsDto;
import com.gl.orderservice.orderservice.dto.OrderStatusUpdateRequestDto;
import com.gl.orderservice.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequestDto request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getMyOrders() {
        return ResponseEntity.ok(orderService.getMyOrders());
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderStatusUpdateRequestDto request
    ) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, request));
    }

    @GetMapping("/worker/my-orders")
    public ResponseEntity<List<OrderResponseDto>> getOrdersForWorker() {
        return ResponseEntity.ok(orderService.getOrdersForWorker());
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }



    @GetMapping("/admin/stats")
    public ResponseEntity<OrderStatsDto> getOrderStats() {
        return ResponseEntity.ok(orderService.getOrderStats());
    }
}
