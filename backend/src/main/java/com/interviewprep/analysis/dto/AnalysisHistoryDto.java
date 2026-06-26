package com.interviewprep.analysis.dto;

import com.interviewprep.analysis.model.JobAnalysis;

import java.time.Instant;
import java.util.UUID;

public record AnalysisHistoryDto(
        UUID id,
        String jobTitle,
        String company,
        Integer readinessScore,
        Boolean hasResumeTailoring,
        Instant createdAt
) {
    public static AnalysisHistoryDto from(JobAnalysis analysis) {
        return new AnalysisHistoryDto(
                analysis.getId(),
                analysis.getJobTitle(),
                analysis.getCompany(),
                analysis.getReadinessScore(),
                analysis.getTailoredResumeSections() != null && !analysis.getTailoredResumeSections().isEmpty(),
                analysis.getCreatedAt()
        );
    }
}
