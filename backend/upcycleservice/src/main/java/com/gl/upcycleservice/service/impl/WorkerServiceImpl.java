package com.gl.upcycleservice.service.impl;

import com.gl.upcycleservice.client.UserServiceClient;
import com.gl.upcycleservice.dto.UserResponseDto;
import com.gl.upcycleservice.dto.WorkerRegistrationRequestDto;
import com.gl.upcycleservice.dto.WorkerResponseDto;
import com.gl.upcycleservice.dto.WorkerReviewResponseDto;
import com.gl.upcycleservice.entity.WorkerProfile;
import com.gl.upcycleservice.exception.ResourceNotFoundException;
import com.gl.upcycleservice.repository.WorkerProfileRepository;
import com.gl.upcycleservice.repository.WorkerReviewRepository;
import com.gl.upcycleservice.service.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class WorkerServiceImpl implements WorkerService {

    private final WorkerProfileRepository workerProfileRepository;
    private final UserServiceClient userServiceClient;
    private final WorkerReviewRepository workerReviewRepository;

    @Override
    public WorkerResponseDto registerWorkerProfile(WorkerRegistrationRequestDto request) {
        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();

        if (currentUser == null) {
            throw new RuntimeException("Unable to fetch current user details");
        }

        if (!"WORKER".equalsIgnoreCase(currentUser.getRole())) {
            throw new RuntimeException("Only WORKER role users can register worker profile");
        }

        workerProfileRepository.findByUserId(currentUser.getId())
                .ifPresent(existing -> {
                    throw new RuntimeException("Worker profile already exists for this user");
                });

        if (request.getLaundryPricePerItem() == null &&
                request.getIroningPricePerItem() == null &&
                request.getDryCleaningPricePerItem() == null &&
                request.getStitchingPricePerItem() == null &&
                request.getAlterationPricePerItem() == null) {
            throw new RuntimeException("At least one service price must be provided");
        }

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        WorkerProfile profile = WorkerProfile.builder()
                .userId(currentUser.getId())
                .workerName(currentUser.getName())
                .skills(request.getSkills())
                .bio(request.getBio())
                .experienceYears(request.getExperienceYears())
                .laundryPricePerItem(request.getLaundryPricePerItem())
                .ironingPricePerItem(request.getIroningPricePerItem())
                .dryCleaningPricePerItem(request.getDryCleaningPricePerItem())
                .stitchingPricePerItem(request.getStitchingPricePerItem())
                .alterationPricePerItem(request.getAlterationPricePerItem())
                .otpVerified(false)
                .adminApproved(Boolean.TRUE.equals(currentUser.getVerified()))
                .verifiedBadge(false)
                .rating(0.0)
                .otpCode(otp)
                .build();

        WorkerProfile saved = workerProfileRepository.save(profile);

        System.out.println("=======================================");
        System.out.println("🔥 AUTO OTP GENERATED DURING PROFILE CREATE");
        System.out.println("👉 Worker Profile ID: " + saved.getId());
        System.out.println("👉 Worker User ID: " + saved.getUserId());
        System.out.println("👉 OTP: " + otp);
        System.out.println("=======================================");

        return mapToDto(saved);
    }

    @Override
    public List<WorkerResponseDto> getVerifiedWorkers() {
        return workerProfileRepository.findByVerifiedBadgeTrue()
                .stream()
                .filter(this::isWorkerStillPresentInUserService)
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public WorkerResponseDto getWorkerById(Long workerProfileId) {
        WorkerProfile profile = workerProfileRepository.findById(workerProfileId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Worker profile not found with id: " + workerProfileId
                ));

        if (!isWorkerStillPresentInUserService(profile)) {
            throw new ResourceNotFoundException(
                    "Worker not found in user-service for userId: " + profile.getUserId()
            );
        }

        return mapToDto(profile);
    }

    @Override
    public WorkerResponseDto markOtpVerified(Long workerProfileId) {
        WorkerProfile profile = workerProfileRepository.findById(workerProfileId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Worker profile not found with id: " + workerProfileId
                ));

        profile.setOtpVerified(true);

        if (Boolean.TRUE.equals(profile.getAdminApproved())) {
            profile.setVerifiedBadge(true);
        } else {
            profile.setVerifiedBadge(false);
        }

        WorkerProfile saved = workerProfileRepository.save(profile);
        return mapToDto(saved);
    }

    @Override
    public WorkerResponseDto syncAdminApproval(Long userId, Boolean approved) {
        WorkerProfile profile = workerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Worker profile not found for userId: " + userId
                ));

        profile.setAdminApproved(approved);

        if (Boolean.TRUE.equals(profile.getOtpVerified()) && Boolean.TRUE.equals(approved)) {
            profile.setVerifiedBadge(true);
        } else {
            profile.setVerifiedBadge(false);
        }

        WorkerProfile saved = workerProfileRepository.save(profile);
        return mapToDto(saved);
    }

    @Override
    public WorkerResponseDto getMyWorkerProfile() {
        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();

        if (currentUser == null) {
            throw new RuntimeException("Unable to fetch current user details");
        }

        WorkerProfile profile = workerProfileRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Worker profile not found for userId: " + currentUser.getId()
                ));

        return mapToDto(profile);
    }

    @Override
    public List<WorkerResponseDto> getAllWorkerProfiles() {
        return workerProfileRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<WorkerResponseDto> getPendingWorkers() {
        return workerProfileRepository.findByOtpVerifiedTrueAndAdminApprovedFalse()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private boolean isWorkerStillPresentInUserService(WorkerProfile profile) {
        try {
            UserResponseDto user = userServiceClient.getUserById(profile.getUserId());

            if (user == null || user.getId() == null) {
                return false;
            }

            return true;
        } catch (Exception e) {
            System.out.println("Skipping deleted/missing worker userId: " + profile.getUserId());
            return false;
        }
    }

    private WorkerResponseDto mapToDto(WorkerProfile profile) {

        List<WorkerReviewResponseDto> reviews = workerReviewRepository
                .findByWorkerProfileIdOrderByCreatedAtDesc(profile.getId())
                .stream()
                .limit(2)
                .map(review -> WorkerReviewResponseDto.builder()
                        .id(review.getId())
                        .workerProfileId(review.getWorkerProfileId())
                        .userId(review.getUserId())
                        .orderId(review.getOrderId())
                        .rating(review.getRating())
                        .review(review.getReview())
                        .createdAt(review.getCreatedAt())
                        .build())
                .toList();

        long reviewCount = workerReviewRepository.countByWorkerProfileId(profile.getId());

        String phone = null;
        String workerName = profile.getWorkerName();

        try {
            UserResponseDto user = userServiceClient.getUserById(profile.getUserId());
            if (user != null) {
                phone = user.getPhone();

                if (user.getName() != null && !user.getName().isBlank()) {
                    workerName = user.getName();
                }
            }
        } catch (Exception e) {
            System.out.println("Unable to fetch user details for userId: " + profile.getUserId());
        }

        return WorkerResponseDto.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .workerName(workerName)
                .phone(phone)
                .skills(profile.getSkills())
                .bio(profile.getBio())
                .otpVerified(profile.getOtpVerified())
                .adminApproved(profile.getAdminApproved())
                .verifiedBadge(profile.getVerifiedBadge())
                .rating(profile.getRating())
                .experienceYears(profile.getExperienceYears())
                .laundryPricePerItem(profile.getLaundryPricePerItem())
                .ironingPricePerItem(profile.getIroningPricePerItem())
                .dryCleaningPricePerItem(profile.getDryCleaningPricePerItem())
                .stitchingPricePerItem(profile.getStitchingPricePerItem())
                .alterationPricePerItem(profile.getAlterationPricePerItem())
                .reviewCount(reviewCount)
                .reviews(reviews)
                .build();
    }
}