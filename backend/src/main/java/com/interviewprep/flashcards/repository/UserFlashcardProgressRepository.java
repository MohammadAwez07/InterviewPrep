package com.interviewprep.flashcards.repository;

import com.interviewprep.flashcards.model.UserFlashcardProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserFlashcardProgressRepository extends JpaRepository<UserFlashcardProgress, UUID> {

    Optional<UserFlashcardProgress> findByUserIdAndFlashcardId(UUID userId, UUID flashcardId);

    @Query("""
        SELECT p FROM UserFlashcardProgress p
        JOIN FETCH p.flashcard f
        WHERE p.user.id = :userId
          AND p.nextReviewAt <= :now
          AND f.isActive = true
        ORDER BY p.nextReviewAt ASC
        """)
    List<UserFlashcardProgress> findDueCards(@Param("userId") UUID userId, @Param("now") Instant now);

    @Query("""
        SELECT COUNT(p) FROM UserFlashcardProgress p
        WHERE p.user.id = :userId AND p.nextReviewAt <= :now
        """)
    long countDueCards(@Param("userId") UUID userId, @Param("now") Instant now);

    @Query("""
        SELECT COUNT(p) FROM UserFlashcardProgress p
        WHERE p.user.id = :userId AND p.repetitions > 0
        """)
    long countReviewedCards(@Param("userId") UUID userId);
}
