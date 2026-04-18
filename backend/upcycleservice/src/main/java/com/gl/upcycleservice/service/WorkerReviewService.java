package com.gl.upcycleservice.service;

import com.gl.upcycleservice.dto.WorkerReviewRequestDto;
import com.gl.upcycleservice.dto.WorkerReviewResponseDto;

import java.util.List;

public interface WorkerReviewService {

    WorkerReviewResponseDto addReview(WorkerReviewRequestDto request);

    List<WorkerReviewResponseDto> getReviewsByWorkerProfileId(Long workerProfileId);
}