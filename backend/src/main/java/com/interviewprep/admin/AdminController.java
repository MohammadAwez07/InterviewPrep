package com.interviewprep.admin;

import com.interviewprep.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SolutionGeneratorService generatorService;

    /** Kick off background generation for all problems that have no approaches yet. */
    @PostMapping("/generate-solutions")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateSolutions() {
        if (generatorService.isRunning()) {
            return ResponseEntity.ok(ApiResponse.ok(
                    Map.of("message", "Already running",
                           "progress", generatorService.getProgress(),
                           "total",    generatorService.getTotal())));
        }
        generatorService.generateForAllProblems();
        return ResponseEntity.ok(ApiResponse.ok(
                Map.of("message", "Generation started in background")));
    }

    /** Returns current generation progress + overall stats. */
    @GetMapping("/solution-stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> solutionStats() {
        return ResponseEntity.ok(ApiResponse.ok(generatorService.getStats()));
    }
}
