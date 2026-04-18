package com.gl.notificationservice.dto;

import com.gl.notificationservice.enums.NotificationChannel;
import com.gl.notificationservice.enums.NotificationStatus;
import com.gl.notificationservice.enums.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDto {
    private Long id;
    private Long userId;
    private NotificationType type;
    private NotificationChannel channel;
    private String title;
    private String message;
    private NotificationStatus status;
    private LocalDateTime createdAt;
}