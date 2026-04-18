package com.gl.notificationservice.dto;

import com.gl.notificationservice.enums.NotificationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationStatusUpdateDto {

    @NotNull(message = "Status is required")
    private NotificationStatus status;
}