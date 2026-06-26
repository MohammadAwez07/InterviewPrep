package com.interviewprep.problems.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubmitAttemptRequest(
        @NotBlank String submittedCode,
        @NotNull String status,        // ACCEPTED | PARTIAL | FAILED
        Integer timeTakenSec,
        String language
) {}
