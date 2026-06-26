package com.interviewprep.flashcards.service;

import com.interviewprep.auth.model.User;
import com.interviewprep.auth.repository.UserRepository;
import com.interviewprep.flashcards.dto.FlashcardDto;
import com.interviewprep.flashcards.dto.ReviewRequest;
import com.interviewprep.flashcards.model.Flashcard;
import com.interviewprep.flashcards.model.UserFlashcardProgress;
import com.interviewprep.flashcards.repository.FlashcardRepository;
import com.interviewprep.flashcards.repository.UserFlashcardProgressRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FlashcardService {

    private final FlashcardRepository flashcardRepository;
    private final UserFlashcardProgressRepository progressRepository;
    private final SpacedRepetitionService sm2;
    private final UserRepository userRepository;

    /** Returns all cards due today for the given user (initialising new cards on demand). */
    @Transactional
    public List<FlashcardDto> getDueCards(String email) {
        User user = findUser(email);
        ensureProgressInitialised(user);
        List<UserFlashcardProgress> due = progressRepository.findDueCards(user.getId(), Instant.now());
        return due.stream()
                .map(p -> FlashcardDto.from(p.getFlashcard(), p))
                .toList();
    }

    /** Returns all flashcards for browsing, optionally filtered by topic. */
    @Transactional(readOnly = true)
    public List<FlashcardDto> browse(String topic, String email) {
        User user = findUser(email);
        List<Flashcard> cards = (topic != null && !topic.isBlank())
                ? flashcardRepository.findByTopicAndIsActiveTrue(topic)
                : flashcardRepository.findByIsActiveTrue();

        return cards.stream().map(f -> {
            UserFlashcardProgress p = progressRepository
                    .findByUserIdAndFlashcardId(user.getId(), f.getId()).orElse(null);
            return FlashcardDto.from(f, p);
        }).toList();
    }

    /** Apply an SM-2 review rating to a card. */
    @Transactional
    public FlashcardDto reviewCard(UUID cardId, ReviewRequest req, String email) {
        User user = findUser(email);
        Flashcard card = flashcardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Flashcard not found: " + cardId));
        UserFlashcardProgress progress = progressRepository
                .findByUserIdAndFlashcardId(user.getId(), cardId)
                .orElseGet(() -> initProgress(user, card));
        sm2.applyReview(progress, req.rating());
        progressRepository.save(progress);
        return FlashcardDto.from(card, progress);
    }

    @Transactional(readOnly = true)
    public List<String> getTopics() {
        return flashcardRepository.findDistinctTopicBy();
    }

    // ── Helpers ───────────────────────────────────────────────
    private void ensureProgressInitialised(User user) {
        List<Flashcard> all = flashcardRepository.findByIsActiveTrue();
        all.forEach(card -> progressRepository
                .findByUserIdAndFlashcardId(user.getId(), card.getId())
                .orElseGet(() -> progressRepository.save(initProgress(user, card))));
    }

    private UserFlashcardProgress initProgress(User user, Flashcard card) {
        return UserFlashcardProgress.builder()
                .user(user)
                .flashcard(card)
                .build();
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
