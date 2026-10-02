package com.task2.portal.dto;

public record LoginResponse(String token, long expiresIn) {
}
