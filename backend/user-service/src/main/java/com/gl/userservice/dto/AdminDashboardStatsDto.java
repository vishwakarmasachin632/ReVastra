package com.gl.userservice.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardStatsDto {

    private long totalUsers;
    private long totalConsumers;
    private long totalWorkers;
    private long verifiedWorkers;
    private long pendingWorkers;
}