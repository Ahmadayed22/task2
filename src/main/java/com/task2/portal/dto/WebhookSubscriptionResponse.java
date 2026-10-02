package com.task2.portal.dto;

import java.time.Instant;

public record WebhookSubscriptionResponse(Long id, String url, String eventType, Instant createdAt) {
}
