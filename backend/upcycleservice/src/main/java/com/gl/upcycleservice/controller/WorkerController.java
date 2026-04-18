package com.gl.upcycleservice.controller;

import com.gl.upcycleservice.dto.OtpRequestDto;
import com.gl.upcycleservice.dto.OtpVerifyRequestDto;
import com.gl.upcycleservice.dto.WorkerRegistrationRequestDto;
import com.gl.upcycleservice.dto.WorkerResponseDto;
import com.gl.upcycleservice.service.OtpService;
import com.gl.upcycleservice.service.WorkerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workers")
@RequiredArgsConstructor
public class WorkerController {

    private final WorkerService workerService;
    private final OtpService otpService;

    // Worker creates own profile after login
    @PostMapping("/register")
    public ResponseEntity<WorkerResponseDto> registerWorkerProfile(
            @Valid @RequestBody WorkerRegistrationRequestDto request
    ) {
        return ResponseEntity.ok(workerService.registerWorkerProfile(request));
    }

    // Consumer-facing verified workers list
    @GetMapping
    public ResponseEntity<List<WorkerResponseDto>> getVerifiedWorkers() {
        return ResponseEntity.ok(workerService.getVerifiedWorkers());
    }

    // Worker / admin view a specific worker profile by profile id
    @GetMapping("/{workerProfileId}")
    public ResponseEntity<WorkerResponseDto> getWorkerById(@PathVariable Long workerProfileId) {
        return ResponseEntity.ok(workerService.getWorkerById(workerProfileId));
    }

    // Logged-in worker can see own worker profile
    @GetMapping("/me")
    public ResponseEntity<WorkerResponseDto> getMyWorkerProfile() {
        return ResponseEntity.ok(workerService.getMyWorkerProfile());
    }

    // Send OTP for worker profile verification
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@Valid @RequestBody OtpRequestDto request) {
        return ResponseEntity.ok(otpService.sendOtp(request.getWorkerProfileId()));
    }

    // Verify OTP for worker profile
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@Valid @RequestBody OtpVerifyRequestDto request) {
        return ResponseEntity.ok(otpService.verifyOtp(request.getWorkerProfileId(), request.getOtp()));
    }

    // Admin/internal: sync approval from user-service
    @PutMapping("/admin/approval-sync/{userId}")
    public ResponseEntity<WorkerResponseDto> syncAdminApproval(
            @PathVariable Long userId,
            @RequestParam Boolean approved
    ) {
        return ResponseEntity.ok(workerService.syncAdminApproval(userId, approved));
    }

    // Admin: get all worker profiles
    @GetMapping("/admin/all")
    public ResponseEntity<List<WorkerResponseDto>> getAllWorkerProfiles() {
        return ResponseEntity.ok(workerService.getAllWorkerProfiles());
    }

    // Admin: get only pending worker profiles
    @GetMapping("/admin/pending")
    public ResponseEntity<List<WorkerResponseDto>> getPendingWorkers() {
        return ResponseEntity.ok(workerService.getPendingWorkers());
    }
}