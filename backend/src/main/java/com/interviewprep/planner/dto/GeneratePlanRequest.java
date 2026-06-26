package com.interviewprep.planner.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record GeneratePlanRequest(
        @NotNull @Future LocalDate targetDate,
        List<String> weakTopics
) {}
