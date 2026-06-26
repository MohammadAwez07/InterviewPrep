package com.interviewprep.flashcards.service;

import com.interviewprep.flashcards.model.UserFlashcardProgress;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * SM-2 spaced repetition algorithm.
 *
 * Rating scale (1–5):
 *   5 = Perfect recall, no hesitation
 *   4 = Correct after slight hesitation
 *   3 = Correct with serious difficulty
 *   2 = Incorrect but answer seemed easy to recall
 *   1 = Incorrect with major difficulty
 *
 * Formula:
 *   new_EF = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
 *   EF minimum: 1.3
 *   Interval:
 *     repetitions == 0 → 1 day
 *     repetitions == 1 → 6 days
 *     repetitions >= 2 → interval * EF (rounded)
 *   If rating < 3: reset repetitions to 0, interval to 1
 */
@Service
public class SpacedRepetitionService {

    private static final BigDecimal MIN_EF = new BigDecimal("1.30");

    public UserFlashcardProgress applyReview(UserFlashcardProgress progress, int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        BigDecimal ef = progress.getEaseFactor();
        int reps = progress.getRepetitions();
        int interval = progress.getIntervalDays();

        // Update ease factor
        double delta = 0.1 - (5 - rating) * (0.08 + (5 - rating) * 0.02);
        ef = ef.add(BigDecimal.valueOf(delta)).max(MIN_EF)
               .setScale(2, RoundingMode.HALF_UP);

        // Compute new interval
        int newInterval;
        if (rating < 3) {
            // Forgot — restart
            reps = 0;
            newInterval = 1;
        } else {
            reps++;
            newInterval = switch (reps) {
                case 1 -> 1;
                case 2 -> 6;
                default -> (int) Math.round(interval * ef.doubleValue());
            };
        }

        progress.setEaseFactor(ef);
        progress.setRepetitions(reps);
        progress.setIntervalDays(newInterval);
        progress.setLastReviewedAt(Instant.now());
        progress.setNextReviewAt(Instant.now().plus(newInterval, ChronoUnit.DAYS));
        return progress;
    }
}
