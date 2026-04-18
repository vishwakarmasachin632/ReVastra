package com.gl.laundryservice.controller;

import com.gl.laundryservice.dto.LaundryBookingRequestDto;
import com.gl.laundryservice.dto.LaundryBookingResponseDto;
import com.gl.laundryservice.service.LaundryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/laundry")
@RequiredArgsConstructor
public class LaundryController {

    private final LaundryService laundryService;

    @PostMapping("/book")
    public ResponseEntity<LaundryBookingResponseDto> createBooking(
            @Valid @RequestBody LaundryBookingRequestDto request
    ) {
        return ResponseEntity.ok(laundryService.createBooking(request));
    }

    @GetMapping
    public ResponseEntity<List<LaundryBookingResponseDto>> getAllBookings() {
        return ResponseEntity.ok(laundryService.getAllBookings());
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<LaundryBookingResponseDto> getBookingById(@PathVariable Long bookingId) {
        return ResponseEntity.ok(laundryService.getBookingById(bookingId));
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<LaundryBookingResponseDto> cancelBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(laundryService.cancelBooking(bookingId));
    }
}