package com.task2.portal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank @Size(max = 80) String username,
        @NotBlank @Size(min = 8) String password,
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Email @Size(max = 180) String email,
        @NotBlank
        @Size(max = 25)
        @Pattern(regexp = "^[0-9+()\\- .]{7,25}$")
        String phone
) {
}