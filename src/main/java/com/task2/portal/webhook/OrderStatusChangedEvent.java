package com.task2.portal.webhook;

import java.time.Instant;

import com.task2.portal.enums.OrderStatus;

public record OrderStatusChangedEvent(
        Long orderId,
        OrderStatus previousStatus,
        OrderStatus newStatus,
        Instant occurredAt
) {
}
