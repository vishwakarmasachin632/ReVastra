package com.gl.recyclingservice.service.impl;

import com.gl.recyclingservice.client.RewardServiceClient;
import com.gl.recyclingservice.client.UserServiceClient;
import com.gl.recyclingservice.dto.*;
import com.gl.recyclingservice.entity.Donation;
import com.gl.recyclingservice.enums.DonationStatus;
import com.gl.recyclingservice.enums.RewardSource;
import com.gl.recyclingservice.exception.ResourceNotFoundException;
import com.gl.recyclingservice.repository.DonationRepository;
import com.gl.recyclingservice.service.RecyclingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecyclingServiceImpl implements RecyclingService {

    private final DonationRepository donationRepository;
    private final UserServiceClient userServiceClient;
    private final RewardServiceClient rewardServiceClient;

    @Override
    public DonationResponseDto donateClothes(DonationRequestDto request) {
        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();

        int points = calculatePoints(request.getItemCount(), request.getItemType());

        Donation donation = Donation.builder()
                .userId(currentUser.getId())
                .itemCount(request.getItemCount())
                .description(request.getDescription())
                .pickupAddress(request.getPickupAddress())
                .itemType(request.getItemType())
                .pointsEarned(points)
                .status(DonationStatus.SUBMITTED)
                .createdAt(LocalDateTime.now())
                .build();

        Donation savedDonation = donationRepository.save(donation);

        RewardCreditRequestDto rewardRequest = RewardCreditRequestDto.builder()
                .userId(currentUser.getId())
                .points(points)
                .source(RewardSource.DONATION)
                .note("Reward for clothing donation")
                .build();

        rewardServiceClient.creditPoints(rewardRequest);

        return mapToDto(savedDonation);
    }

    @Override
    public List<DonationResponseDto> getMyDonations() {
        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();

        return donationRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public DonationResponseDto getDonationById(Long donationId) {
        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();

        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new ResourceNotFoundException("Donation not found with id: " + donationId));

        if (!donation.getUserId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to access this donation");
        }

        return mapToDto(donation);
    }

    @Override
    public RecyclingPointsResponseDto getMyPoints() {
        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();
        return rewardServiceClient.getRewardsByUserId(currentUser.getId());
    }

    private int calculatePoints(Integer itemCount, String itemType) {
        int base = 10;
        if ("WINTER".equalsIgnoreCase(itemType)) {
            base = 15;
        } else if ("PREMIUM".equalsIgnoreCase(itemType)) {
            base = 20;
        }
        return itemCount * base;
    }

    private DonationResponseDto mapToDto(Donation donation) {
        return DonationResponseDto.builder()
                .id(donation.getId())
                .userId(donation.getUserId())
                .itemCount(donation.getItemCount())
                .description(donation.getDescription())
                .pickupAddress(donation.getPickupAddress())
                .itemType(donation.getItemType())
                .pointsEarned(donation.getPointsEarned())
                .status(donation.getStatus())
                .createdAt(donation.getCreatedAt())
                .build();
    }
}