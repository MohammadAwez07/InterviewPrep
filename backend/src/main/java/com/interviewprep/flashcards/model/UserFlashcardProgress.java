package com.interviewprep.flashcards.model;

import com.interviewprep.auth.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_flashcard_progress",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "flashcard_id"}))
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class UserFlashcardProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flashcard_id", nullable = false)
    private Flashcard flashcard;

    @Column(name = "ease_factor", nullable = false, precision = 4, scale = 2)
    @Builder.Default
    private BigDecimal easeFactor = new BigDecimal("2.50");

    @Column(name = "interval_days", nullable = false)
    @Builder.Default
    private Integer intervalDays = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer repetitions = 0;

    @Column(name = "next_review_at", nullable = false)
    @Builder.Default
    private Instant nextReviewAt = Instant.now();

    @Column(name = "last_reviewed_at")
    private Instant lastReviewedAt;
}
