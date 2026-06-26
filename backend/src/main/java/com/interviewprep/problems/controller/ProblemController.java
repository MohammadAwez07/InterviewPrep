package com.interviewprep.problems.controller;

import com.interviewprep.common.ApiResponse;
import com.interviewprep.problems.dto.*;
import com.interviewprep.problems.service.ProblemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProblemSummaryDto>>> listProblems(
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String difficulty,
            @AuthenticationPrincipal UserDetails user) {

        String email = user != null ? user.getUsername() : null;
        List<ProblemSummaryDto> result = problemService.listAllProblems(topic, difficulty, email);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/topics")
    public ResponseEntity<ApiResponse<List<String>>> getTopics() {
        return ResponseEntity.ok(ApiResponse.ok(problemService.getTopics()));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<ProblemDetailDto>> getProblem(
            @PathVariable String slug,
            @AuthenticationPrincipal UserDetails user) {

        String email = user != null ? user.getUsername() : null;
        return ResponseEntity.ok(ApiResponse.ok(problemService.getProblem(slug, email)));
    }

    @PostMapping("/{slug}/submit")
    public ResponseEntity<ApiResponse<String>> submitAttempt(
            @PathVariable String slug,
            @Valid @RequestBody SubmitAttemptRequest req,
            @AuthenticationPrincipal UserDetails user) {

        problemService.submitAttempt(slug, req, user.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Attempt recorded", "OK"));
    }

    // A2Z Sheet Endpoints

    @GetMapping("/a2z")
    public ResponseEntity<ApiResponse<List<ProblemSummaryDto>>> listA2ZProblems(
            @RequestParam(required = false) Integer step,
            @RequestParam(required = false) String section,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String pattern,
            @AuthenticationPrincipal UserDetails user) {

        String email = user != null ? user.getUsername() : null;
        List<ProblemSummaryDto> result = problemService.listA2ZProblems(step, section, difficulty, pattern, email);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/a2z/steps")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getA2ZSteps() {
        return ResponseEntity.ok(ApiResponse.ok(problemService.getA2ZSteps()));
    }

    @GetMapping("/a2z/sections")
    public ResponseEntity<ApiResponse<List<String>>> getA2ZSections() {
        return ResponseEntity.ok(ApiResponse.ok(problemService.getA2ZSections()));
    }

    @GetMapping("/a2z/patterns")
    public ResponseEntity<ApiResponse<List<String>>> getA2ZPatterns() {
        return ResponseEntity.ok(ApiResponse.ok(problemService.getA2ZPatterns()));
    }

    @GetMapping("/a2z/stats")
    public ResponseEntity<ApiResponse<A2ZStatsDto>> getA2ZStats(
            @AuthenticationPrincipal UserDetails user) {

        String email = user != null ? user.getUsername() : null;
        return ResponseEntity.ok(ApiResponse.ok(problemService.getA2ZStats(email)));
    }
}
