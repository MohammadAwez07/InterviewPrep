package com.interviewprep.problems.dto;

import com.interviewprep.problems.model.Problem;

import java.util.List;
import java.util.UUID;

public record ProblemSummaryDto(
        UUID id,
        String title,
        String slug,
        Problem.Difficulty difficulty,
        String topic,
        String timeComplexity,
        boolean solved,

        // A2Z Fields
        Integer stepNumber,
        String sectionName,
        String subTopic,
        List<String> patternTags,
        Integer stepOrder,
        Integer solutionCount
) {
    public static ProblemSummaryDto from(Problem p, boolean solved) {
        return new ProblemSummaryDto(
                p.getId(), p.getTitle(), p.getSlug(),
                p.getDifficulty(), p.getTopic(), p.getTimeComplexity(), solved,

                // A2Z Fields
                p.getStepNumber(),
                p.getSectionName(),
                p.getSubTopic(),
                p.getPatternTags(),
                p.getStepOrder(),
                p.getSolutionApproaches() != null ? p.getSolutionApproaches().size() : 0);
    }
}
