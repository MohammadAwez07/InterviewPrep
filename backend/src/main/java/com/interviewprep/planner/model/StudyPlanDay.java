package com.interviewprep.planner.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "study_plan_days")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class StudyPlanDay {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private StudyPlan plan;

    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Column(nullable = false)
    private String topic;

    private String subtopic;

    @Column(name = "problems_count", nullable = false)
    @Builder.Default
    private Integer problemsCount = 2;

    @Column(name = "flashcards_count", nullable = false)
    @Builder.Default
    private Integer flashcardsCount = 10;

    private String notes;

    @Column(name = "is_completed", nullable = false)
    @Builder.Default
    private Boolean isCompleted = false;
}
