package com.task2.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record WebhookSubscriptionRequest(
        @NotBlank (message = "url is required")
        @Pattern (regexp = "^https?://.+", message = "url must start with http:// or https://")
        String url,

        @NotBlank(message = "eventType is required")
        @Size (max = 100, message = "eventType must be at most 100 characters")
        String eventType
) {
}
