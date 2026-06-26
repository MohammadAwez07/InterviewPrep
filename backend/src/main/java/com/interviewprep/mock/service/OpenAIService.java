package com.interviewprep.mock.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewprep.problems.model.Problem;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Sends code + problem to OpenAI GPT-4o and returns structured feedback JSON.
 * Uses OkHttp directly (bundled with Spring Boot) to avoid SDK version conflicts.
 */
@Slf4j
@Service
public class OpenAIService {

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");

    private final String apiKey;
    private final String model;
    private final int maxTokens;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OpenAIService(
            @Value("${app.openai.api-key}") String apiKey,
            @Value("${app.openai.model:gpt-4o}") String model,
            @Value("${app.openai.max-tokens:1000}") int maxTokens,
            ObjectMapper objectMapper) {
        this.apiKey = apiKey;
        this.model = model;
        this.maxTokens = maxTokens;
        this.objectMapper = objectMapper;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public Map<String, Object> evaluateCode(Problem problem, String code, String language) {
        String systemPrompt = """
                You are a senior software engineer conducting a technical interview.
                Evaluate the candidate's submitted solution strictly and fairly.
                Return ONLY valid JSON with these exact keys:
                {
                  "score": <integer 0-100>,
                  "correctness": "<Perfect|Good|Partial|Wrong>",
                  "timeComplexity": "<e.g. O(n)>",
                  "spaceComplexity": "<e.g. O(1)>",
                  "codeQuality": "<Excellent|Good|Fair|Poor>",
                  "edgeCasesHandled": <true|false>,
                  "feedback": "<2-3 sentence overall feedback>",
                  "suggestions": ["<suggestion 1>", "<suggestion 2>", "<suggestion 3>"]
                }
                """;

        String userPrompt = String.format("""
                Problem: %s
                Difficulty: %s
                Description: %s
                Constraints: %s
                Expected Time Complexity: %s
                Language: %s
                
                Submitted Code:
                ```%s
                %s
                ```
                
                Evaluate strictly on: correctness, time and space complexity, code quality,
                edge case handling. Return only the JSON object, no markdown.
                """,
                problem.getTitle(), problem.getDifficulty(),
                problem.getDescription(), problem.getConstraintsText(),
                problem.getTimeComplexity(), language,
                language.toLowerCase(), code);

        try {
            String requestBody = objectMapper.writeValueAsString(Map.of(
                    "model", model,
                    "max_tokens", maxTokens,
                    "messages", java.util.List.of(
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", userPrompt)
                    )
            ));

            Request request = new Request.Builder()
                    .url(OPENAI_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, JSON_TYPE))
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    log.error("OpenAI API error: {}", response.code());
                    return fallbackFeedback("OpenAI API returned " + response.code());
                }
                Map<String, Object> raw = objectMapper.readValue(
                        response.body().string(), new TypeReference<>() {});
                String content = ((Map<?, ?>) ((Map<?, ?>) ((java.util.List<?>) raw.get("choices"))
                        .get(0)).get("message")).get("content").toString().trim();
                return objectMapper.readValue(content, new TypeReference<>() {});
            }
        } catch (IOException ex) {
            log.error("OpenAI call failed", ex);
            return fallbackFeedback(ex.getMessage());
        }
    }

    private Map<String, Object> fallbackFeedback(String reason) {
        return Map.of(
                "score", 0,
                "correctness", "Unknown",
                "timeComplexity", "Unknown",
                "spaceComplexity", "Unknown",
                "codeQuality", "Unknown",
                "edgeCasesHandled", false,
                "feedback", "AI evaluation unavailable: " + reason,
                "suggestions", java.util.List.of("Review your solution manually")
        );
    }
}
