package com.interviewprep.flashcards.controller;

import com.interviewprep.common.ApiResponse;
import com.interviewprep.flashcards.dto.FlashcardDto;
import com.interviewprep.flashcards.dto.ReviewRequest;
import com.interviewprep.flashcards.service.FlashcardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/flashcards")
@RequiredArgsConstructor
public class FlashcardController {

    private final FlashcardService flashcardService;

    @GetMapping("/due")
    public ResponseEntity<ApiResponse<List<FlashcardDto>>> getDueCards(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(flashcardService.getDueCards(user.getUsername())));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FlashcardDto>>> browseCards(
            @RequestParam(required = false) String topic,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(flashcardService.browse(topic, user.getUsername())));
    }

    @GetMapping("/topics")
    public ResponseEntity<ApiResponse<List<String>>> getTopics() {
        return ResponseEntity.ok(ApiResponse.ok(flashcardService.getTopics()));
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<ApiResponse<FlashcardDto>> reviewCard(
            @PathVariable UUID id,
            @Valid @RequestBody ReviewRequest req,
            @AuthenticationPrincipal UserDetails user) {
        FlashcardDto result = flashcardService.reviewCard(id, req, user.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Review recorded", result));
    }
}
