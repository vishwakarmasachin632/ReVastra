package com.gl.upcycleservice.service;

import com.gl.upcycleservice.dto.WorkerRegistrationRequestDto;
import com.gl.upcycleservice.dto.WorkerResponseDto;

import java.util.List;

public interface WorkerService {

    WorkerResponseDto registerWorkerProfile(WorkerRegistrationRequestDto request);

    List<WorkerResponseDto> getVerifiedWorkers();

    WorkerResponseDto getWorkerById(Long workerProfileId);

    WorkerResponseDto markOtpVerified(Long workerProfileId);

    WorkerResponseDto getMyWorkerProfile();

    WorkerResponseDto syncAdminApproval(Long userId, Boolean approved);

    List<WorkerResponseDto> getAllWorkerProfiles();

    List<WorkerResponseDto> getPendingWorkers();
}