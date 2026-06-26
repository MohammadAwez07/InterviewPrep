package com.interviewprep.problems.dto;

import java.util.List;
import java.util.Map;

public record A2ZStatsDto(
        int totalProblems,
        int easyCount,
        int mediumCount,
        int hardCount,
        int solvedCount,
        double completionPercentage,
        List<StepStats> stepStats,
        List<PatternStats> patternStats
) {
    public record StepStats(
            int stepNumber,
            String sectionName,
            int totalProblems,
            int solvedCount,
            double completionPercentage
    ) {}

    public record PatternStats(
            String patternName,
            int totalProblems,
            int solvedCount,
            double completionPercentage
    ) {}
}
