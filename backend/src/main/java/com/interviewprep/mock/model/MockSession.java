package com.interviewprep.mock.model;

import com.interviewprep.auth.model.User;
import com.interviewprep.problems.model.Problem;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "mock_sessions")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class MockSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @Column(name = "started_at", nullable = false)
    @Builder.Default
    private Instant startedAt = Instant.now();

    @Column(name = "ended_at")
    private Instant endedAt;

    @Column(name = "submitted_code", columnDefinition = "TEXT")
    private String submittedCode;

    @Column(nullable = false)
    @Builder.Default
    private String language = "JAVA";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ai_feedback", columnDefinition = "JSON")
    private Map<String, Object> aiFeedback;

    private Integer score;

    @Column(name = "duration_sec")
    private Integer durationSec;

    @Column(nullable = false)
    @Builder.Default
    private String status = "IN_PROGRESS";
}
