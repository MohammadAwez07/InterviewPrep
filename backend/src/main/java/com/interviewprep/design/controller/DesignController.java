package com.interviewprep.design.controller;

import com.interviewprep.common.ApiResponse;
import com.interviewprep.design.dto.SaveDesignRequest;
import com.interviewprep.design.model.DesignSession;
import com.interviewprep.design.service.DesignService;
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
@RequestMapping("/api/design/sessions")
@RequiredArgsConstructor
public class DesignController {

    private final DesignService designService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DesignSession>>> getSessions(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(designService.getSessions(user.getUsername())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DesignSession>> createSession(
            @Valid @RequestBody SaveDesignRequest req,
            @AuthenticationPrincipal UserDetails user) {
        DesignSession session = designService.createSession(req, user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Session created", session));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DesignSession>> getSession(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(designService.getSession(id, user.getUsername())));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DesignSession>> updateSession(
            @PathVariable UUID id,
            @Valid @RequestBody SaveDesignRequest req,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok("Session updated", designService.updateSession(id, req, user.getUsername())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSession(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails user) {
        designService.deleteSession(id, user.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Session deleted", null));
    }
}
