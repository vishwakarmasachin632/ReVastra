package com.gl.orderservice.orderservice.service;

import com.gl.orderservice.orderservice.dto.OrderRequestDto;
import com.gl.orderservice.orderservice.dto.OrderResponseDto;
import com.gl.orderservice.orderservice.dto.OrderStatsDto;
import com.gl.orderservice.orderservice.dto.OrderStatusUpdateRequestDto;

import java.util.List;

public interface OrderService {

    OrderResponseDto createOrder(OrderRequestDto request);

    OrderResponseDto getOrderById(Long orderId);

    List<OrderResponseDto> getMyOrders();

    OrderResponseDto updateOrderStatus(Long orderId, OrderStatusUpdateRequestDto request);

    List<OrderResponseDto> getOrdersForWorker();

    List<OrderResponseDto> getAllOrders();

    OrderStatsDto getOrderStats();

    List<OrderResponseDto> getOrdersByWorker(Long workerId);

    OrderResponseDto updateWorkerOrderStatus(Long orderId, String status);
}