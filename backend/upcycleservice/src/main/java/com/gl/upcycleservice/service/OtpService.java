package com.gl.upcycleservice.service;

public interface OtpService {

    String sendOtp(Long workerProfileId);

    String verifyOtp(Long workerProfileId, String otp);
}