package com.task2.portal.dto;

public record WebhookIncomingResponse(boolean signatureValid, String message) {
}
