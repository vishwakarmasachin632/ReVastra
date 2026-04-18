package com.gl.recyclingservice.controller;

import com.gl.recyclingservice.dto.DonationRequestDto;
import com.gl.recyclingservice.dto.DonationResponseDto;
import com.gl.recyclingservice.dto.RecyclingPointsResponseDto;
import com.gl.recyclingservice.service.RecyclingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recycling")
@RequiredArgsConstructor
public class RecyclingController {

    private final RecyclingService recyclingService;

    @PostMapping("/donate")
    public ResponseEntity<DonationResponseDto> donateClothes(@Valid @RequestBody DonationRequestDto request) {
        return ResponseEntity.ok(recyclingService.donateClothes(request));
    }

    @GetMapping("/my-donations")
    public ResponseEntity<List<DonationResponseDto>> getMyDonations() {
        return ResponseEntity.ok(recyclingService.getMyDonations());
    }

    @GetMapping("/{donationId}")
    public ResponseEntity<DonationResponseDto> getDonationById(@PathVariable Long donationId) {
        return ResponseEntity.ok(recyclingService.getDonationById(donationId));
    }

    @GetMapping("/points")
    public ResponseEntity<RecyclingPointsResponseDto> getMyPoints() {
        return ResponseEntity.ok(recyclingService.getMyPoints());
    }
}