package com.interviewprep.problems.model;

import com.interviewprep.auth.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_problem_attempts")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class UserProblemAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(name = "submitted_code", columnDefinition = "TEXT")
    private String submittedCode;

    @Column(nullable = false)
    @Builder.Default
    private String language = "JAVA";

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "time_taken_sec")
    private Integer timeTakenSec;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant submittedAt = Instant.now();

    public enum Status { ACCEPTED, PARTIAL, FAILED }
}
