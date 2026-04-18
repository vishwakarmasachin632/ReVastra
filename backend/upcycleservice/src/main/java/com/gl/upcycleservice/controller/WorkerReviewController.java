package com.gl.upcycleservice.controller;

import com.gl.upcycleservice.dto.WorkerReviewRequestDto;
import com.gl.upcycleservice.dto.WorkerReviewResponseDto;
import com.gl.upcycleservice.service.WorkerReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workers/reviews")
@RequiredArgsConstructor
public class WorkerReviewController {

    private final WorkerReviewService workerReviewService;

    @PostMapping
    public ResponseEntity<WorkerReviewResponseDto> addReview(@RequestBody WorkerReviewRequestDto request) {
        return ResponseEntity.ok(workerReviewService.addReview(request));
    }

    @GetMapping("/{workerProfileId}")
    public ResponseEntity<List<WorkerReviewResponseDto>> getReviews(@PathVariable Long workerProfileId) {
        return ResponseEntity.ok(workerReviewService.getReviewsByWorkerProfileId(workerProfileId));
    }
}