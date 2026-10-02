package com.task2.portal.websocket;


import java.time.Instant;

import com.task2.portal.enums.Priority;
import com.task2.portal.enums.TicketStatus;

public record TicketUpdateMessage(
        Long ticketId,
        Long customerId,
        String subject,
        TicketStatus status,
        Priority priority,
        Instant updatedAt
) {
}
