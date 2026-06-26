package com.interviewprep.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Email(message = "Invalid email address")
        String email,

        @NotBlank @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @NotBlank @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
        String fullName
) {}
