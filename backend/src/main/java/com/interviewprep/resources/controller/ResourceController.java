package com.interviewprep.resources.controller;

import com.interviewprep.common.ApiResponse;
import com.interviewprep.resources.dto.ResourceDto;
import com.interviewprep.resources.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    /** All resources grouped by topic — used for the full Resources page */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, List<ResourceDto>>>> getAllGrouped() {
        return ResponseEntity.ok(ApiResponse.ok(resourceService.getAllGroupedByTopic()));
    }

    /** Resources for a specific topic — used by the planner day panel */
    @GetMapping("/topic/{topic}")
    public ResponseEntity<ApiResponse<List<ResourceDto>>> getByTopic(
            @PathVariable String topic,
            @RequestParam(required = false) String type) {
        return ResponseEntity.ok(ApiResponse.ok(resourceService.getByTopic(topic, type)));
    }

    @GetMapping("/topics")
    public ResponseEntity<ApiResponse<List<String>>> getTopics() {
        return ResponseEntity.ok(ApiResponse.ok(resourceService.getTopics()));
    }
}
