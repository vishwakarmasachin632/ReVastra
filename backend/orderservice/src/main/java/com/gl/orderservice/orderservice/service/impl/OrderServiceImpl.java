package com.gl.orderservice.orderservice.service.impl;

import com.gl.orderservice.orderservice.client.UserServiceClient;
import com.gl.orderservice.orderservice.dto.*;
import com.gl.orderservice.orderservice.entity.Order;
import com.gl.orderservice.orderservice.entity.enums.OrderStatus;
import com.gl.orderservice.orderservice.exception.ResourceNotFoundException;
import com.gl.orderservice.orderservice.repository.OrderRepository;
import com.gl.orderservice.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserServiceClient userServiceClient;

    @Override
    public OrderResponseDto createOrder(OrderRequestDto request) {
        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();

        Order order = Order.builder()
                .userId(currentUser.getId())
                .workerId(request.getWorkerId())
                .serviceType(request.getServiceType())
                .status(OrderStatus.PENDING)
                .totalAmount(request.getTotalAmount())
                .consumerName(currentUser.getName())
                .consumerPhone(currentUser.getPhone())
                .createdAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);
        return mapToDto(savedOrder);
    }

    @Override
    public OrderResponseDto getOrderById(Long orderId) {
        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        boolean isOwner = order.getUserId() != null && order.getUserId().equals(currentUser.getId());
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getRole());
        boolean isAssignedWorker = "WORKER".equalsIgnoreCase(currentUser.getRole())
                && order.getWorkerId() != null
                && order.getWorkerId().equals(currentUser.getId());

        if (!isOwner && !isAdmin && !isAssignedWorker) {
            throw new RuntimeException("You are not authorized to access this order");
        }

        return mapToDto(order);
    }

    @Override
    public List<OrderResponseDto> getMyOrders() {
        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();

        return orderRepository.findByUserId(currentUser.getId())
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public OrderResponseDto updateOrderStatus(Long orderId, OrderStatusUpdateRequestDto request) {
        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        boolean isOwner = order.getUserId() != null && order.getUserId().equals(currentUser.getId());
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getRole());
        boolean isAssignedWorker = "WORKER".equalsIgnoreCase(currentUser.getRole())
                && order.getWorkerId() != null
                && order.getWorkerId().equals(currentUser.getId());

        if (!isOwner && !isAdmin && !isAssignedWorker) {
            throw new RuntimeException("You are not authorized to update this order");
        }

        if ("WORKER".equalsIgnoreCase(currentUser.getRole()) && !Boolean.TRUE.equals(currentUser.getVerified())) {
            throw new RuntimeException("Worker is not verified yet");
        }

        order.setStatus(request.getStatus());

        if (request.getPaymentStatus() != null && !request.getPaymentStatus().isBlank()) {
            order.setPaymentStatus(request.getPaymentStatus());
        }

        if (request.getPaymentTime() != null) {
            order.setPaymentTime(request.getPaymentTime());
        }

        if (request.getStatus() == OrderStatus.COMPLETED) {
            order.setCompletedAt(LocalDateTime.now());
        }

        return mapToDto(orderRepository.save(order));
    }

    @Override
    public List<OrderResponseDto> getOrdersForWorker() {
        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();

        if (!"WORKER".equalsIgnoreCase(currentUser.getRole())) {
            throw new RuntimeException("Only workers can access assigned orders");
        }

        if (!Boolean.TRUE.equals(currentUser.getVerified())) {
            throw new RuntimeException("Worker is not verified yet");
        }

        List<OrderStatus> visibleStatuses = List.of(
                OrderStatus.PAID,
                OrderStatus.ACCEPTED,
                OrderStatus.PICKED_UP,
                OrderStatus.IN_PROGRESS,
                OrderStatus.OUT_FOR_DELIVERY
        );

        return orderRepository.findByWorkerIdAndStatusIn(currentUser.getId(), visibleStatuses)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<OrderResponseDto> getAllOrders() {
        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();

        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            throw new RuntimeException("Only admin can access all orders");
        }

        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public OrderStatsDto getOrderStats() {
        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();

        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            throw new RuntimeException("Only admin can access order stats");
        }

        return OrderStatsDto.builder()
                .totalOrders(orderRepository.count())
                .pendingOrders(orderRepository.countByStatus(OrderStatus.PENDING))
                .confirmedOrders(orderRepository.countByStatus(OrderStatus.ACCEPTED))
                .inProgressOrders(orderRepository.countByStatus(OrderStatus.IN_PROGRESS))
                .paidOrders(orderRepository.countByStatus(OrderStatus.PAID))
                .completedOrders(orderRepository.countByStatus(OrderStatus.COMPLETED))
                .cancelledOrders(orderRepository.countByStatus(OrderStatus.CANCELLED))
                .build();
    }

    @Override
    public List<OrderResponseDto> getOrdersByWorker(Long workerId) {
        List<OrderStatus> visibleStatuses = List.of(
                OrderStatus.PAID,
                OrderStatus.ACCEPTED,
                OrderStatus.PICKED_UP,
                OrderStatus.IN_PROGRESS,
                OrderStatus.OUT_FOR_DELIVERY
        );

        return orderRepository.findByWorkerIdAndStatusIn(workerId, visibleStatuses)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public OrderResponseDto updateWorkerOrderStatus(Long orderId, String status) {
        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();

        if (!"WORKER".equalsIgnoreCase(currentUser.getRole())) {
            throw new RuntimeException("Only worker can update worker order status");
        }

        if (!Boolean.TRUE.equals(currentUser.getVerified())) {
            throw new RuntimeException("Worker is not verified yet");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (order.getWorkerId() == null || !order.getWorkerId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not assigned to this order");
        }

        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid order status: " + status);
        }

        if (!isAllowedWorkerStatus(newStatus)) {
            throw new RuntimeException("Worker cannot update order to status: " + status);
        }

        order.setStatus(newStatus);

        if (newStatus == OrderStatus.COMPLETED) {
            order.setCompletedAt(LocalDateTime.now());
        }

        return mapToDto(orderRepository.save(order));
    }

    private boolean isAllowedWorkerStatus(OrderStatus status) {
        return status == OrderStatus.ACCEPTED
                || status == OrderStatus.PICKED_UP
                || status == OrderStatus.IN_PROGRESS
                || status == OrderStatus.OUT_FOR_DELIVERY
                || status == OrderStatus.COMPLETED;
    }

    private OrderResponseDto mapToDto(Order order) {
        return OrderResponseDto.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .serviceType(order.getServiceType())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .consumerName(order.getConsumerName())
                .consumerPhone(order.getConsumerPhone())
                .paymentStatus(order.getPaymentStatus())
                .paymentTime(order.getPaymentTime())
                .createdAt(order.getCreatedAt())
                .completedAt(order.getCompletedAt())
                .build();
    }
}