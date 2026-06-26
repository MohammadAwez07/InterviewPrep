package com.interviewprep.problems.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "problems")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Column(nullable = false)
    private String topic;

    // A2Z Sheet Fields
    @Column(name = "step_number")
    private Integer stepNumber;

    @Column(name = "section_name")
    private String sectionName;

    @Column(name = "sub_topic")
    private String subTopic;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "pattern_tags", columnDefinition = "JSON")
    @Builder.Default
    private List<String> patternTags = List.of();

    @Column(name = "step_order")
    private Integer stepOrder;

    @Column(name = "video_solution_url")
    private String videoSolutionUrl;

    @Column(name = "article_solution_url")
    private String articleSolutionUrl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "constraints_text", columnDefinition = "TEXT")
    private String constraintsText;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    private List<Map<String, String>> examples;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    @Builder.Default
    private List<String> hints = List.of();

    // Legacy fields - will be migrated to solution_approaches table
    @Column(name = "solution_code", columnDefinition = "TEXT")
    private String solutionCode;

    @Column(name = "solution_explanation", columnDefinition = "TEXT")
    private String solutionExplanation;

    @Column(name = "time_complexity")
    private String timeComplexity;

    @Column(name = "space_complexity")
    private String spaceComplexity;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("orderIndex ASC")
    private List<SolutionApproach> solutionApproaches;

    public enum Difficulty { EASY, MEDIUM, HARD }
}
