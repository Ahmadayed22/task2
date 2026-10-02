package com.task2.portal.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

import com.task2.portal.enums.OrderStatus;
import com.task2.portal.enums.Priority;
public record OrderRequest(
        @NotEmpty(message = "items must contain at least one item")
        @Size(max = 50, message = "items must contain at most 50 entries")
        List<@Valid @NotNull @Size(min = 1, max = 200, message = "each item must be 1-200 characters") String> items,
                        
        @NotNull(message = "status is required")
        OrderStatus status,

        @NotNull(message = "priority is required")
        Priority priority,

        @NotNull(message = "total is required")
        @DecimalMin(value = "0.00", inclusive = true, message = "total must be zero or greater")
        BigDecimal total
) {
}
