package com.interviewprep.mock.dto;

import jakarta.validation.constraints.NotBlank;

public record SubmitCodeRequest(
        @NotBlank String submittedCode,
        String language
) {}
