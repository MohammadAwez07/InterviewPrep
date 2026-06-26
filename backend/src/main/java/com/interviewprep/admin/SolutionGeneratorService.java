package com.interviewprep.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewprep.problems.model.Problem;
import com.interviewprep.problems.model.SolutionApproach;
import com.interviewprep.problems.repository.ProblemRepository;
import com.interviewprep.problems.repository.SolutionApproachRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolutionGeneratorService {

    private final ProblemRepository problemRepository;
    private final SolutionApproachRepository approachRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.openai.api-key}")
    private String apiKey;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON_MEDIA = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient http = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build();

    private final AtomicInteger progress = new AtomicInteger(0);
    private final AtomicInteger total    = new AtomicInteger(0);
    private volatile boolean running = false;
    private volatile String  lastError = null;

    // ── Status accessors ────────────────────────────────────────────────────────

    public int getProgress()  { return progress.get(); }
    public int getTotal()     { return total.get(); }
    public boolean isRunning(){ return running; }
    public String getLastError(){ return lastError; }

    // ── Stats query ─────────────────────────────────────────────────────────────

    public Map<String, Object> getStats() {
        long totalProblems  = problemRepository.countByIsActiveTrue();
        long withSolutions  = approachRepository.count();   // rough indicator
        long withApproaches = problemRepository.findByIsActiveTrueOrderByStepNumberAscStepOrderAsc()
                .stream()
                .filter(p -> !approachRepository.findByProblemIdOrderByOrderIndexAsc(p.getId()).isEmpty())
                .count();

        return Map.of(
                "totalProblems",   totalProblems,
                "withApproaches",  withApproaches,
                "withoutApproaches", totalProblems - withApproaches,
                "generatorRunning", running,
                "generatorProgress", progress.get(),
                "generatorTotal",    total.get(),
                "lastError",         lastError != null ? lastError : ""
        );
    }

    // ── Main generation entry point ──────────────────────────────────────────────

    @Async
    public void generateForAllProblems() {
        if (running) {
            log.warn("Generation already in progress, skipping");
            return;
        }
        running  = true;
        lastError = null;
        progress.set(0);

        try {
            List<Problem> all = problemRepository
                    .findByIsActiveTrueOrderByStepNumberAscStepOrderAsc();

            List<Problem> unsolved = all.stream()
                    .filter(p -> approachRepository
                            .findByProblemIdOrderByOrderIndexAsc(p.getId()).isEmpty())
                    .toList();

            total.set(unsolved.size());
            log.info("Starting solution generation for {} problems", unsolved.size());

            for (Problem problem : unsolved) {
                try {
                    generateForProblem(problem);
                } catch (Exception ex) {
                    log.error("Failed to generate for {}: {}", problem.getSlug(), ex.getMessage());
                    lastError = problem.getSlug() + ": " + ex.getMessage();
                }
                progress.incrementAndGet();

                // ~1 req/sec to stay within OpenAI rate limits
                Thread.sleep(1100);
            }

            log.info("Solution generation complete ({} processed)", unsolved.size());
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.warn("Generation interrupted");
        } catch (Exception ex) {
            log.error("Generation loop failed", ex);
            lastError = ex.getMessage();
        } finally {
            running = false;
        }
    }

    // ── Per-problem generation ───────────────────────────────────────────────────

    @Transactional
    public void generateForProblem(Problem problem) throws Exception {
        if (!approachRepository.findByProblemIdOrderByOrderIndexAsc(problem.getId()).isEmpty()) {
            return; // already has approaches
        }

        String prompt = buildSystemPrompt();
        String user   = buildUserPrompt(problem);

        String raw = callOpenAI(prompt, user);

        // Strip markdown code fences if present
        raw = raw.trim();
        if (raw.startsWith("```")) {
            raw = raw.replaceAll("^```[a-zA-Z]*\\n?", "").replaceAll("```$", "").trim();
        }

        List<Map<String, Object>> approaches = objectMapper.readValue(
                raw, new TypeReference<>() {});

        for (int i = 0; i < approaches.size(); i++) {
            Map<String, Object> a = approaches.get(i);

            String typeStr = (String) a.get("approachType");
            SolutionApproach.ApproachType type;
            try {
                type = SolutionApproach.ApproachType.valueOf(typeStr);
            } catch (Exception ex) {
                type = SolutionApproach.ApproachType.OPTIMAL; // fallback
            }

            SolutionApproach approach = SolutionApproach.builder()
                    .problem(problem)
                    .approachType(type)
                    .approachName(getString(a, "approachName"))
                    .timeComplexity(getString(a, "timeComplexity"))
                    .spaceComplexity(getString(a, "spaceComplexity"))
                    .intuition(getString(a, "intuition"))
                    .explanation(getString(a, "explanation"))
                    .code(getString(a, "code"))
                    .isOptimal(Boolean.TRUE.equals(a.get("isOptimal")))
                    .orderIndex(i + 1)
                    .build();

            approachRepository.save(approach);
        }

        log.info("Generated {} approaches for {}", approaches.size(), problem.getSlug());
    }

    // ── OpenAI call ─────────────────────────────────────────────────────────────

    private String callOpenAI(String systemPrompt, String userPrompt) throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "model", "gpt-4o",
                "max_tokens", 2500,
                "temperature", 0.2,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user",   "content", userPrompt)
                )
        ));

        Request req = new Request.Builder()
                .url(OPENAI_URL)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(body, JSON_MEDIA))
                .build();

        try (Response resp = http.newCall(req).execute()) {
            if (!resp.isSuccessful() || resp.body() == null) {
                throw new RuntimeException("OpenAI returned HTTP " + resp.code());
            }
            Map<String, Object> raw = objectMapper.readValue(
                    resp.body().string(), new TypeReference<>() {});
            return ((Map<?, ?>) ((Map<?, ?>) ((List<?>) raw.get("choices"))
                    .get(0)).get("message")).get("content").toString().trim();
        }
    }

    // ── Prompt builders ──────────────────────────────────────────────────────────

    private String buildSystemPrompt() {
        return """
                You are an expert competitive programmer and DSA instructor.
                Generate solution approaches for coding problems.
                Return ONLY a valid JSON array — no markdown fences, no extra text.
                Each element must have these fields:
                  approachType  : "BRUTE_FORCE" | "BETTER" | "OPTIMAL"
                  approachName  : short descriptive name (e.g. "HashSet Lookup")
                  timeComplexity: e.g. "O(n)"
                  spaceComplexity: e.g. "O(1)"
                  intuition     : 1-2 sentence key insight
                  explanation   : clear step-by-step algorithm (plain text, no code)
                  code          : complete, compilable Java 17 solution inside a Solution class
                  isOptimal     : true for exactly ONE approach (the best one), false for others
                Only one approach may have isOptimal = true.
                Always include at least a BRUTE_FORCE and an OPTIMAL approach.
                Include a BETTER approach only when it is genuinely distinct from both.
                """;
    }

    private String buildUserPrompt(Problem problem) {
        return String.format("""
                Problem: %s
                Difficulty: %s
                Topic: %s
                Pattern Tags: %s
                Description: %s
                Constraints: %s
                
                Generate the solution approaches as a JSON array.
                """,
                problem.getTitle(),
                problem.getDifficulty(),
                problem.getTopic(),
                problem.getPatternTags() != null ? problem.getPatternTags() : List.of(),
                problem.getDescription() != null ? problem.getDescription() : "",
                problem.getConstraintsText() != null ? problem.getConstraintsText() : "");
    }

    // ── Helpers ──────────────────────────────────────────────────────────────────

    private String getString(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : "";
    }
}
