package com.gl.recyclingservice.service;

import com.gl.recyclingservice.dto.DonationRequestDto;
import com.gl.recyclingservice.dto.DonationResponseDto;
import com.gl.recyclingservice.dto.RecyclingPointsResponseDto;

import java.util.List;

public interface RecyclingService {

    DonationResponseDto donateClothes(DonationRequestDto request);

    List<DonationResponseDto> getMyDonations();

    DonationResponseDto getDonationById(Long donationId);

    RecyclingPointsResponseDto getMyPoints();
}