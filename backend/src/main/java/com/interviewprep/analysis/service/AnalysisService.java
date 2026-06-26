package com.interviewprep.analysis.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewprep.analysis.dto.*;
import com.interviewprep.analysis.model.ExtractedSkill;
import com.interviewprep.analysis.model.JobAnalysis;
import com.interviewprep.analysis.model.ResumeSection;
import com.interviewprep.analysis.repository.JobAnalysisRepository;
import com.interviewprep.auth.model.User;
import com.interviewprep.auth.repository.UserRepository;
import com.interviewprep.flashcards.repository.UserFlashcardProgressRepository;
import com.interviewprep.problems.repository.UserProblemAttemptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");
    private static final String CACHE_PREFIX = "analysis:";
    private static final long CACHE_TTL_HOURS = 24;
    private static final int MIN_RESUME_LENGTH = 100;

    private final JobAnalysisRepository jobAnalysisRepository;
    private final UserRepository userRepository;
    private final UserProblemAttemptRepository attemptRepository;
    private final UserFlashcardProgressRepository flashcardProgressRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.openai.api-key:}")
    private String apiKey;

    @Value("${app.openai.model:gpt-4o}")
    private String model;

    @Value("${app.openai.max-tokens:2000}")
    private int maxTokens;

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    @Transactional
    public GapAnalysisDto analyse(String email, AnalyseRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get user progress data
        Map<String, Long> solvedByTopic = getSolvedByTopic(user.getId());
        long totalReviewed = flashcardProgressRepository.countReviewedCards(user.getId());
        Map<String, Long> flashcardReviewsByTopic = getFlashcardReviewsByTopic(user.getId());

        // Build cache key
        String cacheKey = buildCacheKey(request.jdText(), solvedByTopic, flashcardReviewsByTopic);

        // Check Redis cache first
        String cachedResult = redisTemplate.opsForValue().get(CACHE_PREFIX + cacheKey);
        if (cachedResult != null) {
            log.info("Cache hit for analysis");
            try {
                GapAnalysisDto dto = objectMapper.readValue(cachedResult, GapAnalysisDto.class);
                // Still save to DB for history, but mark as cached
                JobAnalysis analysis = saveAnalysis(user, request, dto, cacheKey, true);
                return GapAnalysisDto.from(analysis);
            } catch (IOException e) {
                log.warn("Failed to parse cached result, proceeding with fresh analysis", e);
            }
        }

        // Check database cache
        Optional<JobAnalysis> dbCached = jobAnalysisRepository.findByCacheKeyHashAndCachedTrue(cacheKey);
        if (dbCached.isPresent() && !isExpired(dbCached.get())) {
            log.info("Database cache hit for analysis");
            return GapAnalysisDto.from(dbCached.get());
        }

        // Check for zero-data case
        boolean hasZeroData = solvedByTopic.isEmpty() && totalReviewed == 0;

        // Prepare prompt data
        Map<String, Object> userData = Map.of(
                "topicSolvedCounts", solvedByTopic,
                "totalFlashcardsReviewed", totalReviewed,
                "flashcardReviewsByTopic", flashcardReviewsByTopic,
                "hasPrepData", !hasZeroData
        );

        // Execute parallel GPT calls
        CompletableFuture<Map<String, Object>> gapAnalysisFuture = CompletableFuture.supplyAsync(() ->
                callGapAnalysis(request.jdText(), userData));

        CompletableFuture<Map<String, Object>> resumeTailoringFuture = CompletableFuture.supplyAsync(() -> {
            if (request.resumeText() != null && request.resumeText().length() >= MIN_RESUME_LENGTH) {
                return callResumeTailoring(request.jdText(), request.resumeText());
            }
            return Map.of();
        });

        // Wait for both to complete
        CompletableFuture.allOf(gapAnalysisFuture, resumeTailoringFuture).join();

        Map<String, Object> gapResult;
        Map<String, Object> resumeResult;

        try {
            gapResult = gapAnalysisFuture.get();
            resumeResult = resumeTailoringFuture.get();
        } catch (Exception e) {
            log.error("Failed to get analysis results", e);
            throw new RuntimeException("Analysis failed: " + e.getMessage());
        }

        // Build DTO
        GapAnalysisDto dto = buildGapAnalysisDto(gapResult, resumeResult);

        // Cache in Redis
        try {
            String dtoJson = objectMapper.writeValueAsString(dto);
            redisTemplate.opsForValue().set(
                    CACHE_PREFIX + cacheKey,
                    dtoJson,
                    Duration.ofHours(CACHE_TTL_HOURS)
            );
        } catch (IOException e) {
            log.warn("Failed to cache result", e);
        }

        // Save to database
        JobAnalysis saved = saveAnalysis(user, request, dto, cacheKey, false);
        return GapAnalysisDto.from(saved);
    }

    @Transactional(readOnly = true)
    public List<AnalysisHistoryDto> getHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return jobAnalysisRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(AnalysisHistoryDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GapAnalysisDto getById(String email, UUID id) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        JobAnalysis analysis = jobAnalysisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Analysis not found"));

        if (!analysis.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        return GapAnalysisDto.from(analysis);
    }

    private Map<String, Long> getSolvedByTopic(UUID userId) {
        List<Object[]> rows = attemptRepository.countSolvedByTopicForUser(userId);
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }

    private Map<String, Long> getFlashcardReviewsByTopic(UUID userId) {
        // This would need a custom query - for now return empty
        return Map.of();
    }

    private String buildCacheKey(String jdText, Map<String, Long> solvedByTopic,
                                  Map<String, Long> flashcardReviewsByTopic) {
        String data = jdText + ":" + solvedByTopic.toString() + ":" + flashcardReviewsByTopic.toString();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            return String.valueOf(data.hashCode());
        }
    }

    private boolean isExpired(JobAnalysis analysis) {
        return analysis.getCreatedAt().isBefore(Instant.now().minus(Duration.ofHours(CACHE_TTL_HOURS)));
    }

    private Map<String, Object> callGapAnalysis(String jdText, Map<String, Object> userData) {
        String systemPrompt = """
                You are an expert interview readiness evaluator for backend software engineers.
                Extract required technical skills from the job description.
                For each skill, score the candidate 0-100 based on their preparation data.
                
                Skill categories to map to: Java, Spring, AWS, DSA, SystemDesign, Databases, Other.
                
                Return ONLY valid JSON with this exact structure:
                {
                  "readinessScore": <integer 0-100>,
                  "skillBreakdown": [
                    {"skill": "<name>", "category": "<Java|Spring|AWS|DSA|SystemDesign|Databases|Other>", "required": <true|false>, "userScore": <0-100>, "status": "<STRONG|GAP|MISSING>"}
                  ],
                  "strongAreas": ["<skill1>", "<skill2>"],
                  "gapAreas": ["<skill1>", "<skill2>"],
                  "recommendations": ["<action1>", "<action2>", "<action3>"],
                  "suggestedTopics": ["<topic1>", "<topic2>"]
                }
                
                Guidelines:
                - readinessScore is a weighted average based on importance of skills
                - strongAreas: skills with userScore >= 70
                - gapAreas: required skills with userScore < 70, ordered by priority
                - suggestedTopics: match the topics used in the study planner
                """;

        String userPrompt = String.format("""
                Job Description:
                %s
                
                Candidate Preparation Data (JSON):
                %s
                
                Return only the JSON object, no markdown or explanation.
                """, jdText, toJson(userData));

        return callOpenAI(systemPrompt, userPrompt);
    }

    private Map<String, Object> callResumeTailoring(String jdText, String resumeText) {
        String systemPrompt = """
                You are an expert technical resume writer specializing in backend engineering roles.
                Rewrite the candidate's resume to maximize alignment with the job description.
                Use keywords from the JD naturally. Do NOT fabricate experience.
                
                Return ONLY valid JSON with this exact structure:
                {
                  "sections": [
                    {
                      "type": "SUMMARY",
                      "title": "Summary",
                      "content": {"text": "< rewritten summary >"}
                    },
                    {
                      "type": "EXPERIENCE",
                      "title": "Experience",
                      "content": {
                        "company": "<company name>",
                        "role": "<role title>",
                        "dates": "<dates>",
                        "bullets": ["<achievement 1>", "<achievement 2>"]
                      }
                    },
                    {
                      "type": "SKILLS",
                      "title": "Skills",
                      "content": {
                        "categories": [
                          {"name": "Languages", "items": ["Java", "..."]},
                          {"name": "Frameworks", "items": ["Spring Boot", "..."]}
                        ]
                      }
                    }
                  ],
                  "resumeChanges": [
                    "<description of change 1: what was modified and why>",
                    "<description of change 2>"
                  ]
                }
                
                Available section types: SUMMARY, EXPERIENCE, SKILLS, EDUCATION, CERTIFICATIONS, PROJECTS.
                Include only sections present in the original resume.
                """;

        String userPrompt = String.format("""
                Job Description:
                %s
                
                Original Resume:
                %s
                
                Return only the JSON object, no markdown or explanation.
                """, jdText, resumeText);

        return callOpenAI(systemPrompt, userPrompt);
    }

    private Map<String, Object> callOpenAI(String systemPrompt, String userPrompt) {
        if (apiKey == null || apiKey.isBlank()) {
            log.error("OpenAI API key not configured");
            return fallbackResult("API key not configured");
        }

        try {
            String requestBody = objectMapper.writeValueAsString(Map.of(
                    "model", model,
                    "max_tokens", maxTokens,
                    "temperature", 0.1,
                    "messages", List.of(
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
                    return fallbackResult("API returned " + response.code());
                }

                Map<String, Object> raw = objectMapper.readValue(
                        response.body().string(), new TypeReference<>() {});

                String content = ((Map<?, ?>) ((Map<?, ?>) ((List<?>) raw.get("choices"))
                        .get(0)).get("message")).get("content").toString().trim();

                // Clean up markdown code blocks if present
                if (content.startsWith("```json")) {
                    content = content.substring(7);
                }
                if (content.startsWith("```")) {
                    content = content.substring(3);
                }
                if (content.endsWith("```")) {
                    content = content.substring(0, content.length() - 3);
                }
                content = content.trim();

                return objectMapper.readValue(content, new TypeReference<>() {});
            }
        } catch (IOException e) {
            log.error("OpenAI call failed", e);
            return fallbackResult(e.getMessage());
        }
    }

    private Map<String, Object> fallbackResult(String reason) {
        return Map.of(
                "readinessScore", 0,
                "skillBreakdown", List.of(),
                "strongAreas", List.of(),
                "gapAreas", List.of(),
                "recommendations", List.of("Analysis unavailable: " + reason),
                "suggestedTopics", List.of()
        );
    }

    private GapAnalysisDto buildGapAnalysisDto(Map<String, Object> gapResult,
                                               Map<String, Object> resumeResult) {
        List<ResumeSectionDto> resumeSections = new ArrayList<>();
        List<String> resumeChanges = new ArrayList<>();

        if (!resumeResult.isEmpty()) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> sections = (List<Map<String, Object>>) resumeResult.get("sections");
            if (sections != null) {
                resumeSections = sections.stream()
                        .map(this::convertToResumeSectionDto)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }

            @SuppressWarnings("unchecked")
            List<String> changes = (List<String>) resumeResult.get("resumeChanges");
            if (changes != null) {
                resumeChanges = changes;
            }
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> skillBreakdown = (List<Map<String, Object>>) gapResult.getOrDefault("skillBreakdown", List.of());
        List<SkillReadiness> skills = skillBreakdown.stream()
                .map(this::convertToSkillReadiness)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new GapAnalysisDto(
                null,
                (Integer) gapResult.getOrDefault("readinessScore", 0),
                skills,
                (List<String>) gapResult.getOrDefault("strongAreas", List.of()),
                (List<String>) gapResult.getOrDefault("gapAreas", List.of()),
                (List<String>) gapResult.getOrDefault("recommendations", List.of()),
                (List<String>) gapResult.getOrDefault("suggestedTopics", List.of()),
                resumeSections,
                resumeChanges,
                false,
                null,
                null
        );
    }

    private SkillReadiness convertToSkillReadiness(Map<String, Object> map) {
        try {
            return new SkillReadiness(
                    (String) map.get("skill"),
                    (String) map.get("category"),
                    (Boolean) map.get("required"),
                    (Integer) map.get("userScore"),
                    ExtractedSkill.SkillStatus.valueOf((String) map.get("status"))
            );
        } catch (Exception e) {
            log.warn("Failed to convert skill: {}", map, e);
            return null;
        }
    }

    private ResumeSectionDto convertToResumeSectionDto(Map<String, Object> map) {
        String type = (String) map.get("type");
        String title = (String) map.get("title");
        @SuppressWarnings("unchecked")
        Map<String, Object> content = (Map<String, Object>) map.get("content");

        return switch (type != null ? type : "") {
            case "SUMMARY" -> {
                ResumeSectionDto.SummaryDto dto = new ResumeSectionDto.SummaryDto();
                dto.setTitle(title);
                if (content != null && content.get("text") != null) {
                    ResumeSectionDto.SummaryContent sc = new ResumeSectionDto.SummaryContent();
                    sc.setText((String) content.get("text"));
                    dto.setContent(sc);
                }
                yield dto;
            }
            case "EXPERIENCE" -> {
                ResumeSectionDto.ExperienceDto dto = new ResumeSectionDto.ExperienceDto();
                dto.setTitle(title);
                if (content != null) {
                    ResumeSectionDto.ExperienceContent ec = new ResumeSectionDto.ExperienceContent();
                    ec.setCompany((String) content.get("company"));
                    ec.setRole((String) content.get("role"));
                    ec.setDates((String) content.get("dates"));
                    @SuppressWarnings("unchecked")
                    List<String> bullets = (List<String>) content.get("bullets");
                    ec.setBullets(bullets != null ? bullets : List.of());
                    dto.setContent(ec);
                }
                yield dto;
            }
            case "SKILLS" -> {
                ResumeSectionDto.SkillsDto dto = new ResumeSectionDto.SkillsDto();
                dto.setTitle(title);
                if (content != null) {
                    ResumeSectionDto.SkillsContent sc = new ResumeSectionDto.SkillsContent();
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> categories = (List<Map<String, Object>>) content.get("categories");
                    if (categories != null) {
                        sc.setCategories(categories.stream().map(cat -> {
                            ResumeSectionDto.SkillCategory category = new ResumeSectionDto.SkillCategory();
                            category.setName((String) cat.get("name"));
                            @SuppressWarnings("unchecked")
                            List<String> items = (List<String>) cat.get("items");
                            category.setItems(items != null ? items : List.of());
                            return category;
                        }).collect(Collectors.toList()));
                    }
                    dto.setContent(sc);
                }
                yield dto;
            }
            case "EDUCATION" -> {
                ResumeSectionDto.EducationDto dto = new ResumeSectionDto.EducationDto();
                dto.setTitle(title);
                if (content != null) {
                    ResumeSectionDto.EducationContent ec = new ResumeSectionDto.EducationContent();
                    ec.setInstitution((String) content.get("institution"));
                    ec.setDegree((String) content.get("degree"));
                    ec.setDates((String) content.get("dates"));
                    ec.setDetails((String) content.get("details"));
                    dto.setContent(ec);
                }
                yield dto;
            }
            case "CERTIFICATIONS" -> {
                ResumeSectionDto.CertificationsDto dto = new ResumeSectionDto.CertificationsDto();
                dto.setTitle(title);
                if (content != null) {
                    ResumeSectionDto.CertificationsContent cc = new ResumeSectionDto.CertificationsContent();
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> certs = (List<Map<String, Object>>) content.get("certifications");
                    if (certs != null) {
                        cc.setCertifications(certs.stream().map(cert -> {
                            ResumeSectionDto.Certification c = new ResumeSectionDto.Certification();
                            c.setName((String) cert.get("name"));
                            c.setIssuer((String) cert.get("issuer"));
                            c.setDate((String) cert.get("date"));
                            return c;
                        }).collect(Collectors.toList()));
                    }
                    dto.setContent(cc);
                }
                yield dto;
            }
            case "PROJECTS" -> {
                ResumeSectionDto.ProjectsDto dto = new ResumeSectionDto.ProjectsDto();
                dto.setTitle(title);
                if (content != null) {
                    ResumeSectionDto.ProjectsContent pc = new ResumeSectionDto.ProjectsContent();
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> projects = (List<Map<String, Object>>) content.get("projects");
                    if (projects != null) {
                        pc.setProjects(projects.stream().map(proj -> {
                            ResumeSectionDto.Project p = new ResumeSectionDto.Project();
                            p.setName((String) proj.get("name"));
                            p.setDescription((String) proj.get("description"));
                            @SuppressWarnings("unchecked")
                            List<String> tech = (List<String>) proj.get("technologies");
                            p.setTechnologies(tech != null ? tech : List.of());
                            return p;
                        }).collect(Collectors.toList()));
                    }
                    dto.setContent(pc);
                }
                yield dto;
            }
            default -> null;
        };
    }

    private JobAnalysis saveAnalysis(User user, AnalyseRequest request,
                                     GapAnalysisDto dto, String cacheKey, boolean cached) {
        List<ExtractedSkill> skills = dto.skillBreakdown().stream()
                .map(s -> new ExtractedSkill(
                        s.skill(), s.category(), s.required(), s.userScore(), s.status()
                ))
                .collect(Collectors.toList());

        List<ResumeSection> sections = dto.tailoredResumeSections() != null
                ? dto.tailoredResumeSections().stream()
                        .map(this::convertToResumeSection)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
                : List.of();

        JobAnalysis analysis = JobAnalysis.builder()
                .user(user)
                .jobTitle(request.jobTitle())
                .company(request.company())
                .jdText(request.jdText())
                .resumeText(request.resumeText())
                .extractedSkills(skills)
                .readinessScore(dto.readinessScore())
                .strongAreas(dto.strongAreas())
                .gapAreas(dto.gapAreas())
                .recommendations(dto.recommendations())
                .suggestedTopics(dto.suggestedTopics())
                .tailoredResumeSections(sections)
                .resumeChanges(dto.resumeChanges() != null ? dto.resumeChanges() : List.of())
                .cacheKeyHash(cacheKey)
                .cached(cached)
                .build();

        return jobAnalysisRepository.save(analysis);
    }

    private ResumeSection convertToResumeSection(ResumeSectionDto dto) {
        if (dto == null) return null;

        return switch (dto.getType()) {
            case "SUMMARY" -> {
                ResumeSection.SummarySection section = new ResumeSection.SummarySection();
                section.setType("SUMMARY");
                section.setTitle(dto.getTitle());
                if (dto instanceof ResumeSectionDto.SummaryDto s && s.getContent() != null) {
                    section.setContent(new ResumeSection.SummaryContent(s.getContent().getText()));
                }
                yield section;
            }
            case "EXPERIENCE" -> {
                ResumeSection.ExperienceSection section = new ResumeSection.ExperienceSection();
                section.setType("EXPERIENCE");
                section.setTitle(dto.getTitle());
                if (dto instanceof ResumeSectionDto.ExperienceDto e && e.getContent() != null) {
                    section.setContent(new ResumeSection.ExperienceContent(
                        e.getContent().getCompany(),
                        e.getContent().getRole(),
                        e.getContent().getDates(),
                        e.getContent().getBullets()
                    ));
                }
                yield section;
            }
            case "SKILLS" -> {
                ResumeSection.SkillsSection section = new ResumeSection.SkillsSection();
                section.setType("SKILLS");
                section.setTitle(dto.getTitle());
                if (dto instanceof ResumeSectionDto.SkillsDto s && s.getContent() != null) {
                    List<ResumeSection.SkillCategory> categories = s.getContent().getCategories() != null
                        ? s.getContent().getCategories().stream()
                            .map(cat -> new ResumeSection.SkillCategory(cat.getName(), cat.getItems()))
                            .collect(Collectors.toList())
                        : List.of();
                    section.setContent(new ResumeSection.SkillsContent(categories));
                }
                yield section;
            }
            case "EDUCATION" -> {
                ResumeSection.EducationSection section = new ResumeSection.EducationSection();
                section.setType("EDUCATION");
                section.setTitle(dto.getTitle());
                if (dto instanceof ResumeSectionDto.EducationDto e && e.getContent() != null) {
                    section.setContent(new ResumeSection.EducationContent(
                        e.getContent().getInstitution(),
                        e.getContent().getDegree(),
                        e.getContent().getDates(),
                        e.getContent().getDetails()
                    ));
                }
                yield section;
            }
            case "CERTIFICATIONS" -> {
                ResumeSection.CertificationsSection section = new ResumeSection.CertificationsSection();
                section.setType("CERTIFICATIONS");
                section.setTitle(dto.getTitle());
                if (dto instanceof ResumeSectionDto.CertificationsDto c && c.getContent() != null) {
                    List<ResumeSection.Certification> certs = c.getContent().getCertifications() != null
                        ? c.getContent().getCertifications().stream()
                            .map(cert -> new ResumeSection.Certification(
                                cert.getName(), cert.getIssuer(), cert.getDate()
                            ))
                            .collect(Collectors.toList())
                        : List.of();
                    section.setContent(new ResumeSection.CertificationsContent(certs));
                }
                yield section;
            }
            case "PROJECTS" -> {
                ResumeSection.ProjectsSection section = new ResumeSection.ProjectsSection();
                section.setType("PROJECTS");
                section.setTitle(dto.getTitle());
                if (dto instanceof ResumeSectionDto.ProjectsDto p && p.getContent() != null) {
                    List<ResumeSection.Project> projects = p.getContent().getProjects() != null
                        ? p.getContent().getProjects().stream()
                            .map(proj -> new ResumeSection.Project(
                                proj.getName(),
                                proj.getDescription(),
                                proj.getTechnologies()
                            ))
                            .collect(Collectors.toList())
                        : List.of();
                    section.setContent(new ResumeSection.ProjectsContent(projects));
                }
                yield section;
            }
            default -> null;
        };
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            return obj.toString();
        }
    }
}
