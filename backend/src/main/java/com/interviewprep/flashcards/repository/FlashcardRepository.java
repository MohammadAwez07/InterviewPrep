package com.interviewprep.flashcards.repository;

import com.interviewprep.flashcards.model.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FlashcardRepository extends JpaRepository<Flashcard, UUID> {
    List<Flashcard> findByTopicAndIsActiveTrue(String topic);
    List<Flashcard> findByIsActiveTrue();
    List<String> findDistinctTopicBy();
}
