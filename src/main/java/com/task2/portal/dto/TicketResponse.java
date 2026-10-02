package com.task2.portal.dto;

import java.time.Instant;

import com.task2.portal.enums.Priority;
import com.task2.portal.enums.TicketStatus;

public record TicketResponse(
        Long id,
        Long customerId,
        String subject,
        TicketStatus status,
        Priority priority,
        Instant createdAt,
        Instant updatedAt
) {
}