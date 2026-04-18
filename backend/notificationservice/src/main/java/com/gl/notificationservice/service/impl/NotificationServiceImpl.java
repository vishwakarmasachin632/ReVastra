package com.gl.notificationservice.service.impl;

import com.gl.notificationservice.client.UserServiceClient;
import com.gl.notificationservice.dto.*;
import com.gl.notificationservice.entity.Notification;
import com.gl.notificationservice.enums.NotificationStatus;
import com.gl.notificationservice.exception.ResourceNotFoundException;
import com.gl.notificationservice.repository.NotificationRepository;
import com.gl.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserServiceClient userServiceClient;

    @Override
    public NotificationResponseDto createNotification(NotificationCreateRequestDto request) {
        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .channel(request.getChannel())
                .title(request.getTitle())
                .message(request.getMessage())
                .status(NotificationStatus.UNREAD)
                .createdAt(LocalDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notification);

        // demo log for email/sms simulation
        System.out.println("Notification sent to user " + request.getUserId() + " via " + request.getChannel());

        return mapToDto(saved);
    }

    @Override
    public List<NotificationResponseDto> getMyNotifications() {
        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();

        return notificationRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public NotificationResponseDto getNotificationById(Long notificationId) {
        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        if (!notification.getUserId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to access this notification");
        }

        return mapToDto(notification);
    }

    @Override
    public NotificationResponseDto updateStatus(Long notificationId, NotificationStatusUpdateDto request) {
        UserResponseDto currentUser = userServiceClient.getCurrentUserProfile();

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        if (!notification.getUserId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to update this notification");
        }

        notification.setStatus(request.getStatus());
        Notification updated = notificationRepository.save(notification);

        return mapToDto(updated);
    }

    private NotificationResponseDto mapToDto(Notification notification) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .type(notification.getType())
                .channel(notification.getChannel())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}