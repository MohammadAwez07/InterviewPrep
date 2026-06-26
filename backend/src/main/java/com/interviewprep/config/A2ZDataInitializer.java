package com.interviewprep.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewprep.problems.model.Problem;
import com.interviewprep.problems.model.SolutionApproach;
import com.interviewprep.problems.repository.ProblemRepository;
import com.interviewprep.problems.repository.SolutionApproachRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class A2ZDataInitializer implements ApplicationRunner {

    private final ProblemRepository problemRepository;
    private final SolutionApproachRepository solutionApproachRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        loadProblems();
    }

    private void loadProblems() {
        try {
            ClassPathResource resource = new ClassPathResource("data/a2z_problems.json");
            if (!resource.exists()) {
                log.warn("a2z_problems.json not found, skipping A2Z data initialization");
                return;
            }

            try (InputStream is = resource.getInputStream()) {
                List<Map<String, Object>> defs = objectMapper.readValue(is, new TypeReference<>() {});

                int inserted = 0, skipped = 0;

                for (Map<String, Object> def : defs) {
                    String slug = (String) def.get("slug");
                    if (slug == null || slug.isBlank()) continue;

                    Problem problem = problemRepository.findBySlugAndIsActiveTrue(slug).orElse(null);

                    if (problem == null) {
                        problem = Problem.builder()
                                .title((String) def.get("title"))
                                .slug(slug)
                                .difficulty(Problem.Difficulty.valueOf((String) def.get("difficulty")))
                                .topic((String) def.get("topic"))
                                .stepNumber((Integer) def.get("stepNumber"))
                                .sectionName((String) def.get("sectionName"))
                                .subTopic((String) def.get("subTopic"))
                                .patternTags(castStringList(def.get("patternTags")))
                                .stepOrder((Integer) def.get("stepOrder"))
                                .videoSolutionUrl((String) def.get("videoSolutionUrl"))
                                .articleSolutionUrl((String) def.get("articleSolutionUrl"))
                                .description((String) def.get("description"))
                                .constraintsText((String) def.get("constraintsText"))
                                .examples(parseExamples(def.get("examples")))
                                .hints(castStringList(def.get("hints")))
                                .isActive(true)
                                .build();

                        problem = problemRepository.save(problem);
                        inserted++;
                    } else {
                        skipped++;
                    }

                    // Seed solution approaches if defined and none exist yet
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> approaches = (List<Map<String, Object>>) def.get("approaches");
                    if (approaches != null && !approaches.isEmpty()
                            && solutionApproachRepository.findByProblemIdOrderByOrderIndexAsc(problem.getId()).isEmpty()) {
                        seedApproaches(problem, approaches);
                    }
                }

                log.info("A2Z DataInitializer: {} problems inserted, {} already existed", inserted, skipped);
            }
        } catch (Exception e) {
            log.error("Failed to load A2Z problems from JSON: {}", e.getMessage(), e);
        }
    }

    private void seedApproaches(Problem problem, List<Map<String, Object>> approaches) {
        for (int i = 0; i < approaches.size(); i++) {
            Map<String, Object> a = approaches.get(i);
            try {
                SolutionApproach approach = SolutionApproach.builder()
                        .problem(problem)
                        .approachType(SolutionApproach.ApproachType.valueOf((String) a.get("approachType")))
                        .approachName((String) a.get("approachName"))
                        .timeComplexity((String) a.get("timeComplexity"))
                        .spaceComplexity((String) a.get("spaceComplexity"))
                        .code((String) a.get("code"))
                        .explanation((String) a.get("explanation"))
                        .intuition((String) a.get("intuition"))
                        .isOptimal(Boolean.TRUE.equals(a.get("isOptimal")))
                        .orderIndex(i + 1)
                        .build();
                solutionApproachRepository.save(approach);
            } catch (Exception ex) {
                log.warn("Failed to seed approach for {}: {}", problem.getSlug(), ex.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> castStringList(Object obj) {
        if (obj instanceof List<?> list) return (List<String>) list;
        return List.of();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, String>> parseExamples(Object obj) {
        if (obj instanceof List<?> list) return (List<Map<String, String>>) list;
        return List.of();
    }
}
