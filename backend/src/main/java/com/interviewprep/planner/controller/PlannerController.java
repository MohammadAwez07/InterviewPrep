package com.interviewprep.planner.controller;

import com.interviewprep.common.ApiResponse;
import com.interviewprep.planner.dto.GeneratePlanRequest;
import com.interviewprep.planner.model.StudyPlan;
import com.interviewprep.planner.model.StudyPlanDay;
import com.interviewprep.planner.service.PlannerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/planner")
@RequiredArgsConstructor
public class PlannerController {

    private final PlannerService plannerService;

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<StudyPlan>> generatePlan(
            @Valid @RequestBody GeneratePlanRequest req,
            @AuthenticationPrincipal UserDetails user) {
        StudyPlan plan = plannerService.generatePlan(req, user.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Study plan generated", plan));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<StudyPlan>> getActivePlan(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(plannerService.getActivePlan(user.getUsername())));
    }

    @PostMapping("/days/{dayId}/complete")
    public ResponseEntity<ApiResponse<StudyPlanDay>> markComplete(
            @PathVariable UUID dayId,
            @AuthenticationPrincipal UserDetails user) {
        StudyPlanDay day = plannerService.markDayComplete(dayId, user.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Day marked complete", day));
    }
}
