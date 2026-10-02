package com.task2.portal.webhook;


import java.net.URI;
import java.net.http.*;
import java.time.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.task2.portal.enums.OrderStatus;

import tools.jackson.databind.ObjectMapper;
@Service 
public class WebhookService {
    public static final String ORDER_STATUS_CHANGED = "order.status_changed";

    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    private final List<WebhookSubscription> subscriptions = new CopyOnWriteArrayList<>();
    private final AtomicLong idSequence = new AtomicLong(0);
    private final ObjectMapper objectMapper;
    private final HmacSigner hmacSigner;
    private final HttpClient httpClient;

    public WebhookService(ObjectMapper objectMapper, HmacSigner hmacSigner) {
        this.objectMapper = objectMapper;
        this.hmacSigner = hmacSigner;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
    }

    public WebhookSubscription register(String url, String eventType) {
        WebhookSubscription subscription = new WebhookSubscription(
                idSequence.incrementAndGet(), url.trim(), eventType.trim(), Instant.now());
        subscriptions.add(subscription);
        return subscription;
    }

    public List<WebhookSubscription> subscriptions() {
        return List.copyOf(subscriptions);
    }

    @Async ("webhookTaskExecutor")
    public void publishStatusChanged(OrderStatusChangedEvent event) {
        for (WebhookSubscription subscription : subscriptions) {
            if (!ORDER_STATUS_CHANGED.equals(subscription.eventType())) continue;
            deliver(subscription, event);
        }
    }

    private void deliver(WebhookSubscription subscription, OrderStatusChangedEvent event) {
        try {
            WebhookPayload payload = new WebhookPayload(
                    ORDER_STATUS_CHANGED,
                    event.orderId(),
                    event.previousStatus(),
                    event.newStatus(),
                    event.occurredAt()
            );
            String body = objectMapper.writeValueAsString(payload);
            String signature = hmacSigner.sign(body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(subscription.url()))
                    .timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "application/json")
                    .header("X-Digitinary-Signature", signature)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Webhook delivery to {} completed with HTTP {}", subscription.url(), response.statusCode());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.warn("Webhook delivery interrupted for {}", subscription.url());
        } catch (Exception ex) {
            log.error("Webhook delivery failed for {}", subscription.url(), ex);
        }
    }

    public record WebhookPayload(
            String event,
            Long orderId,
            OrderStatus previousStatus,
            OrderStatus newStatus,
            Instant occurredAt
    ) {
    }
}
