package com.task2.portal.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.task2.portal.dto.WebhookIncomingResponse;
import com.task2.portal.dto.WebhookSubscriptionRequest;
import com.task2.portal.dto.WebhookSubscriptionResponse;
import com.task2.portal.webhook.HmacSigner;
import com.task2.portal.webhook.WebhookService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {
    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);
    private final WebhookService webhookService;
    private final HmacSigner hmacSigner;

    public WebhookController(WebhookService webhookService, HmacSigner hmacSigner) {
        this.webhookService = webhookService;
        this.hmacSigner = hmacSigner;
    }

    @PostMapping("/subscriptions")
    public ResponseEntity<WebhookSubscriptionResponse> subscribe(@Valid @RequestBody WebhookSubscriptionRequest request) {
        var subscription = webhookService.register(request.url(), request.eventType());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(subscription.id())
                .toUri();
        return ResponseEntity.created(location)
                .body(new WebhookSubscriptionResponse(subscription.id(), subscription.url(), subscription.eventType(), subscription.createdAt()));
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<List<WebhookSubscriptionResponse>> listSubscriptions() {
        return ResponseEntity.ok(webhookService.subscriptions().stream()
                .map(s -> new WebhookSubscriptionResponse(s.id(), s.url(), s.eventType(), s.createdAt()))
                .toList());
    }

    @PostMapping("/incoming/orders")
    public ResponseEntity<WebhookIncomingResponse> receiveOrderEvent(
            @RequestHeader(value = "X-Digitinary-Signature", required = false) String signature,
            @RequestBody String body) {
        boolean valid = hmacSigner.verify(body, signature);
        log.info("Received simulated order webhook. signatureValid={}, payload={}", valid, body);
        if (!valid) {
            return ResponseEntity.status(401).body(new WebhookIncomingResponse(false, "Invalid webhook signature"));
        }
        return ResponseEntity.ok(new WebhookIncomingResponse(true, "Webhook received and signature verified"));
    }
}
