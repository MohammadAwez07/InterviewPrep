package com.interviewprep.analysis.model;

import com.interviewprep.auth.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "job_analyses")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class JobAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "company")
    private String company;

    @Column(name = "jd_text", nullable = false, columnDefinition = "TEXT")
    private String jdText;

    @Column(name = "resume_text", columnDefinition = "TEXT")
    private String resumeText;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extracted_skills", columnDefinition = "JSON")
    @Builder.Default
    private List<ExtractedSkill> extractedSkills = new ArrayList<>();

    @Column(name = "readiness_score")
    private Integer readinessScore;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "strong_areas", columnDefinition = "JSON")
    @Builder.Default
    private List<String> strongAreas = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "gap_areas", columnDefinition = "JSON")
    @Builder.Default
    private List<String> gapAreas = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "recommendations", columnDefinition = "JSON")
    @Builder.Default
    private List<String> recommendations = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "suggested_topics", columnDefinition = "JSON")
    @Builder.Default
    private List<String> suggestedTopics = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tailored_resume_sections", columnDefinition = "JSON")
    @Builder.Default
    private List<ResumeSection> tailoredResumeSections = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "resume_changes", columnDefinition = "JSON")
    @Builder.Default
    private List<String> resumeChanges = new ArrayList<>();

    @Column(name = "cache_key_hash", length = 64)
    private String cacheKeyHash;

    @Column(name = "cached")
    @Builder.Default
    private Boolean cached = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
