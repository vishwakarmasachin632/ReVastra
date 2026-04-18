package com.gl.laundryservice;

import com.gl.laundryservice.client.OrderServiceClient;
import com.gl.laundryservice.client.UserServiceClient;
import com.gl.laundryservice.dto.*;
import com.gl.laundryservice.entity.LaundryBooking;
import com.gl.laundryservice.enums.LaundryStatus;
import com.gl.laundryservice.enums.LaundryType;
import com.gl.laundryservice.enums.PickupTimeSlot;
import com.gl.laundryservice.exception.ResourceNotFoundException;
import com.gl.laundryservice.repository.LaundryBookingRepository;
import com.gl.laundryservice.service.impl.LaundryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LaundryServiceImplTest {

    @Mock
    private LaundryBookingRepository laundryBookingRepository;

    @Mock
    private OrderServiceClient orderServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private LaundryServiceImpl laundryService;

    @Test
    void createBooking_ShouldCreateBookingAndOrder() {
        LaundryBookingRequestDto request = LaundryBookingRequestDto.builder()
                .workerId(2L)
                .laundryType(LaundryType.WASH_AND_FOLD)
                .itemCount(5)
                .pickupAddress("Bhopal")
                .deliveryAddress("Indore")
                .pickupDate(LocalDate.now().plusDays(1))
                .pickupTimeSlot(PickupTimeSlot.SLOT_9_AM)
                .estimatedPrice(BigDecimal.valueOf(250))
                .build();

        when(userServiceClient.getCurrentUserProfile()).thenReturn(
                UserResponseDto.builder().id(1L).role("USER").build()
        );

        when(userServiceClient.getWorkerById(2L)).thenReturn(
                UserResponseDto.builder().id(2L).role("WORKER").verified(true).build()
        );

        when(orderServiceClient.createOrder(any())).thenReturn(
                OrderResponseDto.builder().id(10L).userId(1L).workerId(2L).build()
        );

        when(laundryBookingRepository.save(any(LaundryBooking.class))).thenAnswer(invocation -> {
            LaundryBooking booking = invocation.getArgument(0);
            booking.setId(1L);
            return booking;
        });

        LaundryBookingResponseDto response = laundryService.createBooking(request);

        assertEquals(1L, response.getId());
        assertEquals(10L, response.getOrderId());
        assertEquals(LaundryStatus.BOOKED, response.getStatus());
        assertEquals(LocalDate.now().plusDays(1), response.getPickupDate());
        assertEquals(PickupTimeSlot.SLOT_9_AM, response.getPickupTimeSlot());
        assertEquals(LocalDate.now().plusDays(3), response.getEstimatedDeliveryDate());
    }

    @Test
    void createBooking_ShouldThrow_WhenPickupDatePast() {
        LaundryBookingRequestDto request = LaundryBookingRequestDto.builder()
                .workerId(2L)
                .laundryType(LaundryType.WASH_AND_FOLD)
                .itemCount(2)
                .pickupAddress("Bhopal")
                .deliveryAddress("Indore")
                .pickupDate(LocalDate.now().minusDays(1))
                .pickupTimeSlot(PickupTimeSlot.SLOT_1_PM)
                .estimatedPrice(BigDecimal.valueOf(100))
                .build();

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                laundryService.createBooking(request));

        assertEquals("Pickup date cannot be in the past", ex.getMessage());
    }

    @Test
    void cancelBooking_ShouldUpdateStatusAndSyncOrder() {
        LaundryBooking booking = LaundryBooking.builder()
                .id(1L)
                .orderId(10L)
                .status(LaundryStatus.BOOKED)
                .build();

        when(laundryBookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(laundryBookingRepository.save(any(LaundryBooking.class))).thenAnswer(invocation ->
                invocation.getArgument(0));

        LaundryBookingResponseDto response = laundryService.cancelBooking(1L);

        assertEquals(LaundryStatus.CANCELLED, response.getStatus());
        verify(orderServiceClient).updateOrderStatus(eq(10L), any(OrderStatusUpdateRequestDto.class));
    }

    @Test
    void getBookingById_ShouldThrow_WhenNotFound() {
        when(laundryBookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                laundryService.getBookingById(1L));
    }
}