package com.interviewprep.analysis.dto;

import com.interviewprep.analysis.model.ExtractedSkill;

public record SkillReadiness(
        String skill,
        String category,
        Boolean required,
        Integer userScore,
        ExtractedSkill.SkillStatus status
) {
    public static SkillReadiness from(ExtractedSkill skill) {
        return new SkillReadiness(
                skill.getSkill(),
                skill.getCategory(),
                skill.getRequired(),
                skill.getUserScore(),
                skill.getStatus()
        );
    }
}
