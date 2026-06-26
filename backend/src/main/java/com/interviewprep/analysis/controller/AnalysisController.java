package com.interviewprep.analysis.controller;

import com.interviewprep.analysis.dto.AnalyseRequest;
import com.interviewprep.analysis.dto.AnalysisHistoryDto;
import com.interviewprep.analysis.dto.GapAnalysisDto;
import com.interviewprep.analysis.service.AnalysisService;
import com.interviewprep.common.ApiResponse;
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
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @PostMapping("/analyse")
    public ResponseEntity<ApiResponse<GapAnalysisDto>> analyse(
            @Valid @RequestBody AnalyseRequest request,
            @AuthenticationPrincipal UserDetails user) {
        GapAnalysisDto result = analysisService.analyse(user.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Analysis complete", result));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<AnalysisHistoryDto>>> getHistory(
            @AuthenticationPrincipal UserDetails user) {
        List<AnalysisHistoryDto> history = analysisService.getHistory(user.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(history));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GapAnalysisDto>> getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails user) {
        GapAnalysisDto result = analysisService.getById(user.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
