package com.gl.orderservice.orderservice;

import com.gl.orderservice.orderservice.client.UserServiceClient;
import com.gl.orderservice.orderservice.dto.OrderRequestDto;
import com.gl.orderservice.orderservice.dto.OrderResponseDto;
import com.gl.orderservice.orderservice.dto.OrderStatusUpdateRequestDto;
import com.gl.orderservice.orderservice.dto.UserResponseDto;
import com.gl.orderservice.orderservice.entity.Order;
import com.gl.orderservice.orderservice.entity.enums.OrderStatus;
import com.gl.orderservice.orderservice.entity.enums.ServiceType;
import com.gl.orderservice.orderservice.exception.ResourceNotFoundException;
import com.gl.orderservice.orderservice.repository.OrderRepository;
import com.gl.orderservice.orderservice.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private OrderServiceImpl orderService;
    private UserResponseDto currentUser;
    @BeforeEach
    void setUp() {
        currentUser = UserResponseDto.builder()
                .id(1L)
                .name("Sachin")
                .email("sachin@gmail.com")
                .role("USER")
                .build();
    }
    @Test
    void createOrder_ShouldCreateSuccessfully() {
        OrderRequestDto request = OrderRequestDto.builder()
                .serviceType(ServiceType.WASH_AND_FOLD)
                .workerId(2L)
                .totalAmount(BigDecimal.valueOf(250))
                .build();
        Order saved = Order.builder()
                .id(1L)
                .userId(1L)
                .workerId(2L)
                .serviceType(ServiceType.WASH_AND_FOLD)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(250))
                .createdAt(LocalDateTime.now())
                .build();
        when(userServiceClient.getCurrentUserProfile()).thenReturn(currentUser);
        when(orderRepository.save(any(Order.class))).thenReturn(saved);
        OrderResponseDto response = orderService.createOrder(request);
//        assertEquals(1L, response.getUserId());
        assertEquals(OrderStatus.PENDING, response.getStatus());
    }
    @Test
    void getMyOrders_ShouldReturnOrders() {
        when(userServiceClient.getCurrentUserProfile()).thenReturn(currentUser);
        when(orderRepository.findByUserId(1L)).thenReturn(List.of(
                Order.builder().id(1L).userId(1L).serviceType(ServiceType.WASH_AND_FOLD)
                        .status(OrderStatus.PENDING).totalAmount(BigDecimal.TEN).build()
        ));
        List<OrderResponseDto> result = orderService.getMyOrders();
        assertEquals(1, result.size());
    }
    @Test
    void getOrderById_ShouldThrow_WhenNotFound() {
        when(userServiceClient.getCurrentUserProfile()).thenReturn(currentUser);
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () ->
                orderService.getOrderById(1L));
    }
    @Test
    void updateOrderStatus_ShouldSetCompletedAt_WhenCompleted() {
        Order order = Order.builder()
                .id(1L)
                .userId(1L)
                .status(OrderStatus.PENDING)
                .serviceType(ServiceType.WASH_AND_FOLD)
                .totalAmount(BigDecimal.TEN)
                .build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv ->
                inv.getArgument(0));
        OrderResponseDto response = orderService.updateOrderStatus(
                1L,
                OrderStatusUpdateRequestDto.builder().status(OrderStatus.COMPLETED).build()
        );
        assertEquals(OrderStatus.COMPLETED, response.getStatus());
        assertNotNull(response.getCompletedAt());
    }
}
