package com.interviewprep.problems.dto;

import com.interviewprep.problems.model.Problem;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ProblemDetailDto(
        UUID id,
        String title,
        String slug,
        Problem.Difficulty difficulty,
        String topic,

        // A2Z Fields
        Integer stepNumber,
        String sectionName,
        String subTopic,
        List<String> patternTags,
        Integer stepOrder,
        String videoSolutionUrl,
        String articleSolutionUrl,

        String description,
        String constraintsText,
        List<Map<String, String>> examples,
        List<String> hints,

        // Legacy solution fields (for backward compatibility)
        String solutionCode,
        String solutionExplanation,
        String timeComplexity,
        String spaceComplexity,

        // New solution approaches
        List<SolutionApproachDto> solutionApproaches,

        boolean solved,
        String lastSubmittedCode
) {
    public static ProblemDetailDto from(Problem p, boolean solved, String lastCode,
                                        List<SolutionApproachDto> solutionApproaches) {
        return new ProblemDetailDto(
                p.getId(), p.getTitle(), p.getSlug(),
                p.getDifficulty(), p.getTopic(),

                // A2Z Fields
                p.getStepNumber(),
                p.getSectionName(),
                p.getSubTopic(),
                p.getPatternTags(),
                p.getStepOrder(),
                p.getVideoSolutionUrl(),
                p.getArticleSolutionUrl(),

                p.getDescription(), p.getConstraintsText(),
                p.getExamples(), p.getHints(),

                // Legacy fields
                p.getSolutionCode(), p.getSolutionExplanation(),
                p.getTimeComplexity(), p.getSpaceComplexity(),

                // New solution approaches
                solutionApproaches,

                solved, lastCode);
    }
}
