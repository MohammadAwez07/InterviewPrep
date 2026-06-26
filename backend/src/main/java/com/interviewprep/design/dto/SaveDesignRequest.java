package com.interviewprep.design.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record SaveDesignRequest(
        String title,
        @NotNull Map<String, Object> canvasData
) {}
