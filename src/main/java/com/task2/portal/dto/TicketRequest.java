package com.task2.portal.dto;

import com.task2.portal.enums.Priority;
import com.task2.portal.enums.TicketStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TicketRequest(
        @NotBlank (message = "subject is required")
        @Size (max = 200, message = "subject must be at most 200 characters")
        String subject,

        @NotNull (message = "status is required")
        TicketStatus status,

        @NotNull(message = "priority is required")
        Priority priority
) {
}