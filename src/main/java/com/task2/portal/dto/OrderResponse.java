package com.task2.portal.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.task2.portal.enums.OrderStatus;
import com.task2.portal.enums.Priority;

public record OrderResponse(
        Long id,
        Long customerId,
        List<String> items,
        OrderStatus status,
        Priority priority,
        BigDecimal total,
        Instant createdAt
    ) {
} 
