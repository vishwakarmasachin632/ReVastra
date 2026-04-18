package com.gl.upcycleservice.service.impl;

import com.gl.upcycleservice.client.UserServiceClient;
import com.gl.upcycleservice.dto.UserResponseDto;
import com.gl.upcycleservice.dto.WorkerReviewRequestDto;
import com.gl.upcycleservice.dto.WorkerReviewResponseDto;
import com.gl.upcycleservice.entity.WorkerProfile;
import com.gl.upcycleservice.entity.WorkerReview;
import com.gl.upcycleservice.exception.ResourceNotFoundException;
import com.gl.upcycleservice.repository.WorkerProfileRepository;
import com.gl.upcycleservice.repository.WorkerReviewRepository;
import com.gl.upcycleservice.service.WorkerReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkerReviewServiceImpl implements WorkerReviewService {

    private final WorkerReviewRepository workerReviewRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final UserServiceClient userServiceClient;

    @Override
    public WorkerReviewResponseDto addReview(WorkerReviewRequestDto request) {
        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();

        if (!"USER".equalsIgnoreCase(currentUser.getRole())) {
            throw new RuntimeException("Only consumers can submit reviews");
        }

        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        workerReviewRepository.findByOrderIdAndUserId(request.getOrderId(), currentUser.getId())
                .ifPresent(existing -> {
                    throw new RuntimeException("Review already submitted for this order");
                });

        WorkerProfile workerProfile = workerProfileRepository.findById(request.getWorkerProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Worker profile not found"));

        WorkerReview review = WorkerReview.builder()
                .workerProfileId(request.getWorkerProfileId())
                .userId(currentUser.getId())
                .orderId(request.getOrderId())
                .rating(request.getRating())
                .review(request.getReview())
                .createdAt(LocalDateTime.now())
                .build();

        WorkerReview saved = workerReviewRepository.save(review);

        updateWorkerAverageRating(workerProfile.getId());

        return mapToDto(saved);
    }

    @Override
    public List<WorkerReviewResponseDto> getReviewsByWorkerProfileId(Long workerProfileId) {
        return workerReviewRepository.findByWorkerProfileIdOrderByCreatedAtDesc(workerProfileId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private void updateWorkerAverageRating(Long workerProfileId) {
        List<WorkerReview> reviews = workerReviewRepository.findByWorkerProfileIdOrderByCreatedAtDesc(workerProfileId);

        double avg = reviews.stream()
                .mapToInt(WorkerReview::getRating)
                .average()
                .orElse(0.0);

        WorkerProfile workerProfile = workerProfileRepository.findById(workerProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Worker profile not found"));

        workerProfile.setRating(avg);
        workerProfileRepository.save(workerProfile);
    }

    private WorkerReviewResponseDto mapToDto(WorkerReview review) {
        return WorkerReviewResponseDto.builder()
                .id(review.getId())
                .workerProfileId(review.getWorkerProfileId())
                .userId(review.getUserId())
                .orderId(review.getOrderId())
                .rating(review.getRating())
                .review(review.getReview())
                .createdAt(review.getCreatedAt())
                .build();
    }
}