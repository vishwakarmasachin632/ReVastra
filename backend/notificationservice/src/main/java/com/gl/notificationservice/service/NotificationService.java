package com.gl.notificationservice.service;

import com.gl.notificationservice.dto.NotificationCreateRequestDto;
import com.gl.notificationservice.dto.NotificationResponseDto;
import com.gl.notificationservice.dto.NotificationStatusUpdateDto;

import java.util.List;

public interface NotificationService {

    NotificationResponseDto createNotification(NotificationCreateRequestDto request);

    List<NotificationResponseDto> getMyNotifications();

    NotificationResponseDto getNotificationById(Long notificationId);

    NotificationResponseDto updateStatus(Long notificationId, NotificationStatusUpdateDto request);
}