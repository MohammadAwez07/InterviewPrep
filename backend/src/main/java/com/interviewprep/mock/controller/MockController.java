package com.interviewprep.mock.controller;

import com.interviewprep.common.ApiResponse;
import com.interviewprep.mock.dto.StartSessionRequest;
import com.interviewprep.mock.dto.SubmitCodeRequest;
import com.interviewprep.mock.model.MockSession;
import com.interviewprep.mock.service.MockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/mock/sessions")
@RequiredArgsConstructor
public class MockController {

    private final MockService mockService;

    @PostMapping
    public ResponseEntity<ApiResponse<MockSession>> startSession(
            @RequestBody(required = false) StartSessionRequest req,
            @AuthenticationPrincipal UserDetails user) {
        StartSessionRequest r = req != null ? req : new StartSessionRequest(null);
        MockSession session = mockService.startSession(r, user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Session started", session));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MockSession>>> getSessions(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(mockService.getSessions(user.getUsername())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MockSession>> getSession(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(mockService.getSession(id, user.getUsername())));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<ApiResponse<MockSession>> submitCode(
            @PathVariable UUID id,
            @Valid @RequestBody SubmitCodeRequest req,
            @AuthenticationPrincipal UserDetails user) {
        MockSession session = mockService.submitCode(id, req, user.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Code submitted and evaluated", session));
    }
}
