package com.interviewprep.analysis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtractedSkill {
    private String skill;
    private String category;
    private Boolean required;
    private Integer userScore;
    private SkillStatus status;

    public enum SkillStatus {
        STRONG, GAP, MISSING
    }
}
