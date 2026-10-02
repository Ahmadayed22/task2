package com.task2.portal.webhook;

import java.time.Instant;

public record WebhookSubscription(Long id, String url, String eventType, Instant createdAt) {
}
