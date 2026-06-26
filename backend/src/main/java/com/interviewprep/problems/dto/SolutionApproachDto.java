package com.interviewprep.problems.dto;

import com.interviewprep.problems.model.SolutionApproach;

import java.util.UUID;

public record SolutionApproachDto(
        UUID id,
        String approachType,
        String approachName,
        String timeComplexity,
        String spaceComplexity,
        String code,
        String explanation,
        String intuition,
        Boolean optimal,          // named 'optimal' (not 'isOptimal') to avoid Jackson stripping the 'is' prefix
        Integer orderIndex
) {
    public static SolutionApproachDto from(SolutionApproach approach) {
        if (approach == null) return null;
        return new SolutionApproachDto(
                approach.getId(),
                approach.getApproachType() != null ? approach.getApproachType().name() : null,
                approach.getApproachName(),
                approach.getTimeComplexity(),
                approach.getSpaceComplexity(),
                approach.getCode(),
                approach.getExplanation(),
                approach.getIntuition(),
                approach.getIsOptimal(),
                approach.getOrderIndex()
        );
    }
}
