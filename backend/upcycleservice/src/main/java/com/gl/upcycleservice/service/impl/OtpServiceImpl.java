package com.gl.upcycleservice.service.impl;

import com.gl.upcycleservice.entity.WorkerProfile;
import com.gl.upcycleservice.exception.ResourceNotFoundException;
import com.gl.upcycleservice.repository.WorkerProfileRepository;
import com.gl.upcycleservice.service.OtpService;
import com.gl.upcycleservice.service.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final WorkerProfileRepository workerProfileRepository;
    private final WorkerService workerService;

    @Override
    public String sendOtp(Long workerProfileId) {

        System.out.println("🔥 sendOtp API HIT for workerId: " + workerProfileId);

        WorkerProfile workerProfile = workerProfileRepository.findById(workerProfileId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        // prevent OTP resend spam (optional but recommended)
        if (Boolean.TRUE.equals(workerProfile.getOtpVerified())) {
            throw new RuntimeException("OTP already verified. No need to send again.");
        }

        // generate 6-digit OTP
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        workerProfile.setOtpCode(otp);

        // OPTIONAL: expiry add kar sakta hai (future use)
        // workerProfile.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

        workerProfileRepository.save(workerProfile);

        // 🔥 IMPORTANT LOG (TERA OTP YAHI MILEGA)
        System.out.println("=======================================");
        System.out.println("✅ OTP GENERATED SUCCESSFULLY");
        System.out.println("👉 Worker ID: " + workerProfileId);
        System.out.println("👉 OTP: " + otp);
        System.out.println("=======================================");

        return "OTP sent successfully";
    }

    @Override
    public String verifyOtp(Long workerProfileId, String otp) {

        WorkerProfile workerProfile = workerProfileRepository.findById(workerProfileId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        if (workerProfile.getOtpCode() == null) {
            throw new RuntimeException("OTP not generated yet");
        }

        if (!workerProfile.getOtpCode().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        // mark verified
        workerProfile.setOtpVerified(true);

        // 🔥 clean OTP after success
        workerProfile.setOtpCode(null);

        workerProfileRepository.save(workerProfile);

        System.out.println("✅ OTP VERIFIED for workerId: " + workerProfileId);

        return "OTP verified successfully";
    }

}