package com.interviewprep.problems.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "solution_approaches")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class SolutionApproach {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(name = "approach_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ApproachType approachType;

    @Column(name = "approach_name", nullable = false)
    private String approachName;

    @Column(name = "time_complexity")
    private String timeComplexity;

    @Column(name = "space_complexity")
    private String spaceComplexity;

    @Column(name = "code", columnDefinition = "TEXT")
    private String code;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "intuition", columnDefinition = "TEXT")
    private String intuition;

    @Column(name = "is_optimal")
    private Boolean isOptimal;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    public enum ApproachType {
        BRUTE_FORCE,
        BETTER,
        OPTIMAL
    }
}
