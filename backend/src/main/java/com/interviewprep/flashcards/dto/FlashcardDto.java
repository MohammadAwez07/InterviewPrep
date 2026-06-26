package com.interviewprep.flashcards.dto;

import com.interviewprep.flashcards.model.Flashcard;
import com.interviewprep.flashcards.model.UserFlashcardProgress;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record FlashcardDto(
        UUID id,
        String topic,
        String subTopic,
        String question,
        String answer,
        String difficultyHint,
        // SM-2 progress (null if card not yet initialised for user)
        BigDecimal easeFactor,
        Integer intervalDays,
        Integer repetitions,
        Instant nextReviewAt
) {
    public static FlashcardDto from(Flashcard f, UserFlashcardProgress p) {
        return new FlashcardDto(
                f.getId(), f.getTopic(), f.getSubTopic(),
                f.getQuestion(), f.getAnswer(), f.getDifficultyHint(),
                p != null ? p.getEaseFactor() : null,
                p != null ? p.getIntervalDays() : null,
                p != null ? p.getRepetitions() : null,
                p != null ? p.getNextReviewAt() : null
        );
    }
}
