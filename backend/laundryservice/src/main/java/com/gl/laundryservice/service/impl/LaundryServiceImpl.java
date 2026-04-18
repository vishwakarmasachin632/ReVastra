package com.gl.laundryservice.service.impl;

import com.gl.laundryservice.client.OrderServiceClient;
import com.gl.laundryservice.client.UserServiceClient;
import com.gl.laundryservice.dto.*;
import com.gl.laundryservice.entity.LaundryBooking;
import com.gl.laundryservice.enums.LaundryStatus;
import com.gl.laundryservice.enums.LaundryType;
import com.gl.laundryservice.enums.OrderServiceType;
import com.gl.laundryservice.exception.ResourceNotFoundException;
import com.gl.laundryservice.repository.LaundryBookingRepository;
import com.gl.laundryservice.service.LaundryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LaundryServiceImpl implements LaundryService {

    private final LaundryBookingRepository laundryBookingRepository;
    private final OrderServiceClient orderServiceClient;
    private final UserServiceClient userServiceClient;

    @Override
    public LaundryBookingResponseDto createBooking(LaundryBookingRequestDto request) {

        if (request.getPickupDate() == null) {
            throw new RuntimeException("Pickup date is required");
        }

        if (request.getPickupTimeSlot() == null) {
            throw new RuntimeException("Pickup time slot is required");
        }

        if (request.getPickupDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Pickup date cannot be in the past");
        }

        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();
        UserResponseDto worker = userServiceClient.getWorkerById(request.getWorkerId());

        OrderRequestDto orderRequest = OrderRequestDto.builder()
                .serviceType(mapToOrderServiceType(request.getLaundryType()))
                .workerId(worker.getId())
                .totalAmount(request.getEstimatedPrice())
                .build();

        OrderResponseDto orderResponse = orderServiceClient.createOrder(orderRequest);

        LaundryBooking booking = LaundryBooking.builder()
                .userId(currentUser.getId())
                .workerId(worker.getId())
                .orderId(orderResponse.getId())
                .laundryType(request.getLaundryType())
                .itemCount(request.getItemCount())
                .pickupAddress(request.getPickupAddress())
                .deliveryAddress(request.getDeliveryAddress())
                .pickupDate(request.getPickupDate())
                .pickupTimeSlot(request.getPickupTimeSlot())
                .estimatedDeliveryDate(request.getPickupDate().plusDays(2))
                .estimatedPrice(request.getEstimatedPrice())
                .status(LaundryStatus.BOOKED)
                .createdAt(LocalDateTime.now())
                .build();

        LaundryBooking savedBooking = laundryBookingRepository.save(booking);
        return mapToDto(savedBooking);
    }

    @Override
    public List<LaundryBookingResponseDto> getAllBookings() {
        return laundryBookingRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public LaundryBookingResponseDto getBookingById(Long bookingId) {
        LaundryBooking booking = laundryBookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Laundry booking not found with id: " + bookingId));

        return mapToDto(booking);
    }

    @Override
    public LaundryBookingResponseDto cancelBooking(Long bookingId) {
        LaundryBooking booking = laundryBookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Laundry booking not found with id: " + bookingId));

        booking.setStatus(LaundryStatus.CANCELLED);

        orderServiceClient.updateOrderStatus(
                booking.getOrderId(),
                OrderStatusUpdateRequestDto.builder()
                        .status("CANCELLED")
                        .build()
        );

        LaundryBooking updatedBooking = laundryBookingRepository.save(booking);
        return mapToDto(updatedBooking);
    }

    private OrderServiceType mapToOrderServiceType(LaundryType laundryType) {
        return switch (laundryType) {
            case WASH_AND_FOLD -> OrderServiceType.WASH_AND_FOLD;
            case DRY_CLEAN -> OrderServiceType.DRY_CLEAN;
            case IRONING -> OrderServiceType.IRONING;
            case STITCHING -> OrderServiceType.STITCHING;
            case ALTERATION -> OrderServiceType.ALTERATION;
        };
    }

    private LaundryBookingResponseDto mapToDto(LaundryBooking booking) {
        return LaundryBookingResponseDto.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .workerId(booking.getWorkerId())
                .orderId(booking.getOrderId())
                .laundryType(booking.getLaundryType())
                .itemCount(booking.getItemCount())
                .pickupAddress(booking.getPickupAddress())
                .deliveryAddress(booking.getDeliveryAddress())
                .pickupDate(booking.getPickupDate())
                .pickupTimeSlot(booking.getPickupTimeSlot())
                .estimatedDeliveryDate(booking.getEstimatedDeliveryDate())
                .estimatedPrice(booking.getEstimatedPrice())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}