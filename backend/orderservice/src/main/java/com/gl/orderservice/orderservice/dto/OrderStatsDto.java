package com.gl.orderservice.orderservice.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatsDto {

    private long totalOrders;
    private long pendingOrders;
    private long confirmedOrders;
    private long inProgressOrders;
    private long paidOrders;
    private long completedOrders;
    private long cancelledOrders;
}