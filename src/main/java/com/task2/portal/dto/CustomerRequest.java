package com.task2.portal.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
        @NotBlank(message = "name is required")
        @Size(max = 150, message = "name must be at most 150 characters")
        String name,

        @NotBlank(message = "email is required")
        @Email(message = "email must be valid")
        @Size(max = 180, message = "email must be at most 180 characters")
        String email,

        @NotBlank(message = "phone is required")
        @Pattern(regexp = "^[0-9+()\\- .]{7,40}$", message = "phone must be a valid phone number")
        String phone
) {
}
