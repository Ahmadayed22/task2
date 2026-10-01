package com.task2.portal.dto;

import java.time.Instant;

public record CustomerResponse(
        Long id,
        String name,
        String email,
        String phone,
        Instant createdAt
) {
}
