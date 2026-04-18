package com.gl.notificationservice;

import com.gl.notificationservice.client.UserServiceClient;
import com.gl.notificationservice.dto.*;
import com.gl.notificationservice.entity.Notification;
import com.gl.notificationservice.enums.NotificationChannel;
import com.gl.notificationservice.enums.NotificationStatus;
import com.gl.notificationservice.enums.NotificationType;
import com.gl.notificationservice.exception.ResourceNotFoundException;
import com.gl.notificationservice.repository.NotificationRepository;
import com.gl.notificationservice.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {
    @Mock private NotificationRepository notificationRepository;
    @Mock private UserServiceClient userServiceClient;
    @InjectMocks
    private NotificationServiceImpl notificationService;
    @Test
    void createNotification_ShouldSave() {
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv ->
        {
            Notification n = inv.getArgument(0);
            n.setId(1L);
            return n;
        });
        NotificationResponseDto response =
                notificationService.createNotification(
                        NotificationCreateRequestDto.builder()
                                .userId(1L)
                                .type(NotificationType.BOOKING_CREATED)
                                .channel(NotificationChannel.IN_APP)
                                .title("Title")
                                .message("Message")
                                .build()
                );
        assertEquals(1L, response.getId());
        assertEquals(NotificationStatus.UNREAD, response.getStatus());
    }
    @Test
    void getMyNotifications_ShouldReturnList() {
        // 1. Mock the user profile retrieval
        when(userServiceClient.getCurrentUserProfile())
                .thenReturn(UserResponseDto.builder().id(1L).build());

        // 2. Mock the repository to return a list with one notification
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(
                        Notification.builder()
                                .id(1L)
                                .userId(1L)
                                .type(NotificationType.GENERAL)
                                .channel(NotificationChannel.IN_APP)
                                .title("Test Title")
                                .message("Test Message")
                                .status(NotificationStatus.UNREAD)
                                .createdAt(LocalDateTime.now())
                                .build()
                ));

        // 3. Execute the service call
        List<NotificationResponseDto> result = notificationService.getMyNotifications();

        // 4. Assertions
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Title", result.get(0).getTitle());
        assertEquals(NotificationStatus.UNREAD, result.get(0).getStatus());
    }
    @Test
    void getNotificationById_ShouldThrow_WhenNotFound() {
        // 1. Mock the user profile retrieval
        when(userServiceClient.getCurrentUserProfile())
                .thenReturn(UserResponseDto.builder().id(1L).build());

        // 2. Mock the repository to return an empty Optional (not found)
        when(notificationRepository.findById(1L))
                .thenReturn(Optional.empty());

        // 3. Assert that the specific exception is thrown
        assertThrows(ResourceNotFoundException.class, () ->
                notificationService.getNotificationById(1L));

        // 4. Verify that the repository was indeed queried
        verify(notificationRepository).findById(1L);
    }
}
