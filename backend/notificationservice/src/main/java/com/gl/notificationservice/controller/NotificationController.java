package com.gl.notificationservice.controller;

import com.gl.notificationservice.dto.NotificationCreateRequestDto;
import com.gl.notificationservice.dto.NotificationResponseDto;
import com.gl.notificationservice.dto.NotificationStatusUpdateDto;
import com.gl.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponseDto> createNotification(
            @Valid @RequestBody NotificationCreateRequestDto request
    ) {
        return ResponseEntity.ok(notificationService.createNotification(request));
    }

    @GetMapping("/me")
    public ResponseEntity<List<NotificationResponseDto>> getMyNotifications() {
        return ResponseEntity.ok(notificationService.getMyNotifications());
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResponseDto> getNotificationById(@PathVariable Long notificationId) {
        return ResponseEntity.ok(notificationService.getNotificationById(notificationId));
    }

    @PutMapping("/{notificationId}/status")
    public ResponseEntity<NotificationResponseDto> updateStatus(
            @PathVariable Long notificationId,
            @Valid @RequestBody NotificationStatusUpdateDto request
    ) {
        return ResponseEntity.ok(notificationService.updateStatus(notificationId, request));
    }
}