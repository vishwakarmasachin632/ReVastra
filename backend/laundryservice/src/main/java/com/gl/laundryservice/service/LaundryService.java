package com.gl.laundryservice.service;

import com.gl.laundryservice.dto.LaundryBookingRequestDto;
import com.gl.laundryservice.dto.LaundryBookingResponseDto;

import java.util.List;

public interface LaundryService {

    LaundryBookingResponseDto createBooking(LaundryBookingRequestDto request);

    List<LaundryBookingResponseDto> getAllBookings();

    LaundryBookingResponseDto getBookingById(Long bookingId);

    LaundryBookingResponseDto cancelBooking(Long bookingId);
}