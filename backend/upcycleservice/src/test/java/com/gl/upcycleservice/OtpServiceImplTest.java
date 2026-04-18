package com.gl.upcycleservice;

import com.gl.upcycleservice.entity.WorkerProfile;
import com.gl.upcycleservice.repository.WorkerProfileRepository;
import com.gl.upcycleservice.service.WorkerService;
import com.gl.upcycleservice.service.impl.OtpServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class OtpServiceImplTest {
    @Mock private WorkerProfileRepository workerProfileRepository;
    @Mock private WorkerService workerService;
    @InjectMocks
    private OtpServiceImpl otpService;
    @Test
    void sendOtp_ShouldGenerateOtp() {
        WorkerProfile profile = WorkerProfile.builder().id(1L).build();
        when(workerProfileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(workerProfileRepository.save(any(WorkerProfile.class))).thenAnswer(inv -> inv.getArgument(0));
        String message = otpService.sendOtp(1L);
        assertTrue(message.contains("OTP sent successfully"));
        assertNotNull(profile.getOtpCode());
    }
    @Test
    void verifyOtp_ShouldCallWorkerService() {
        WorkerProfile profile =
                WorkerProfile.builder().id(1L).otpCode("123456").build();
        when(workerProfileRepository.findById(1L)).thenReturn(Optional.of(profile));
        String result = otpService.verifyOtp(1L, "123456");
        assertEquals("OTP verified successfully", result);
        verify(workerService).markOtpVerified(1L);
    }
}

