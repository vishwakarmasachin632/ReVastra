package com.gl.upcycleservice;

import com.gl.upcycleservice.client.UserServiceClient;
import com.gl.upcycleservice.dto.UserResponseDto;
import com.gl.upcycleservice.dto.WorkerRegistrationRequestDto;
import com.gl.upcycleservice.dto.WorkerResponseDto;
import com.gl.upcycleservice.entity.WorkerProfile;
import com.gl.upcycleservice.repository.WorkerProfileRepository;
import com.gl.upcycleservice.service.impl.WorkerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkerServiceImplTest {

    @Mock
    private WorkerProfileRepository workerProfileRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private WorkerServiceImpl workerService;

    @Test
    void registerWorkerProfile_ShouldCreateProfile() {
        // 1. Mock the user service to return a worker user
        when(userServiceClient.getCurrentUserProfile()).thenReturn(
                UserResponseDto.builder()
                        .id(2L)
                        .name("Worker One")
                        .role("WORKER")
                        .verified(true)
                        .build()
        );

        // 2. Mock that no existing profile exists for this user
        when(workerProfileRepository.findByUserId(2L)).thenReturn(Optional.empty());

        // 3. Mock the save operation to assign an ID (Fixed lambda syntax)
        when(workerProfileRepository.save(any(WorkerProfile.class))).thenAnswer(invocation -> {
            WorkerProfile p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        // 4. Execute
        WorkerResponseDto response = workerService.registerWorkerProfile(
                WorkerRegistrationRequestDto.builder()
                        .skills("Stitching")
                        .bio("Tailor")
                        .build()
        );

        // 5. Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Worker One", response.getWorkerName());
        verify(workerProfileRepository).save(any(WorkerProfile.class));
    }

    @Test
    void getVerifiedWorkers_ShouldReturnList() {
        // 1. Mock the repository to return a list of verified workers
        when(workerProfileRepository.findByVerifiedBadgeTrue()).thenReturn(List.of(
                WorkerProfile.builder()
                        .id(1L)
                        .userId(2L)
                        .workerName("Worker One")
                        .skills("Stitching")
                        .bio("Bio")
                        .verifiedBadge(true)
                        .build()
        ));

        // 2. Execute
        List<WorkerResponseDto> result = workerService.getVerifiedWorkers();

        // 3. Assert
        assertEquals(1, result.size());
        assertEquals("Worker One", result.get(0).getWorkerName());
    }
}