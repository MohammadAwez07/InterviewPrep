package com.interviewprep.analysis.dto;

import jakarta.validation.constraints.NotBlank;

public record AnalyseRequest(
        @NotBlank String jobTitle,
        String company,
        @NotBlank String jdText,
        String resumeText
) {}
