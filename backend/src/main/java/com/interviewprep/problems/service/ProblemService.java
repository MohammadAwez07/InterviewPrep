package com.interviewprep.problems.service;

import com.interviewprep.auth.model.User;
import com.interviewprep.auth.repository.UserRepository;
import com.interviewprep.problems.dto.*;
import com.interviewprep.problems.model.Problem;
import com.interviewprep.problems.model.SolutionApproach;
import com.interviewprep.problems.model.UserProblemAttempt;
import com.interviewprep.problems.repository.ProblemRepository;
import com.interviewprep.problems.repository.SolutionApproachRepository;
import com.interviewprep.problems.repository.UserProblemAttemptRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final SolutionApproachRepository solutionApproachRepository;
    private final UserProblemAttemptRepository attemptRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<ProblemSummaryDto> listProblems(String topic, String difficulty,
                                                int page, int size, String userEmail) {
        Problem.Difficulty diff = (difficulty != null && !difficulty.isBlank())
                ? Problem.Difficulty.valueOf(difficulty.toUpperCase()) : null;
        String topicFilter = (topic != null && !topic.isBlank()) ? topic : null;

        PageRequest pageable = PageRequest.of(page, size, Sort.by("difficulty").ascending().and(Sort.by("title")));
        Page<Problem> problems = problemRepository.findFiltered(topicFilter, diff, pageable);

        Set<UUID> solved = getSolvedIds(userEmail);
        return problems.map(p -> ProblemSummaryDto.from(p, solved.contains(p.getId())));
    }

    @Transactional(readOnly = true)
    public List<ProblemSummaryDto> listAllProblems(String topic, String difficulty, String userEmail) {
        Problem.Difficulty diff = (difficulty != null && !difficulty.isBlank())
                ? Problem.Difficulty.valueOf(difficulty.toUpperCase()) : null;
        String topicFilter = (topic != null && !topic.isBlank()) ? topic : null;

        List<Problem> problems;
        if (topicFilter != null && diff != null) {
            problems = problemRepository.findByTopicAndDifficultyAndIsActiveTrueOrderByTitleAsc(topicFilter, diff);
        } else if (topicFilter != null) {
            problems = problemRepository.findByTopicAndIsActiveTrueOrderByTitleAsc(topicFilter);
        } else if (diff != null) {
            problems = problemRepository.findByDifficultyAndIsActiveTrueOrderByTitleAsc(diff);
        } else {
            problems = problemRepository.findByIsActiveTrueOrderByDifficultyAscTitleAsc();
        }

        Set<UUID> solved = getSolvedIds(userEmail);
        return problems.stream()
                .map(p -> ProblemSummaryDto.from(p, solved.contains(p.getId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProblemDetailDto getProblem(String slug, String userEmail) {
        Problem p = problemRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new EntityNotFoundException("Problem not found: " + slug));
        Set<UUID> solved = getSolvedIds(userEmail);
        String lastCode = attemptRepository
                .findLatestByUserAndProblem(getUserId(userEmail), p.getId())
                .map(UserProblemAttempt::getSubmittedCode)
                .orElse(null);

        // Load solution approaches
        List<SolutionApproach> approaches = solutionApproachRepository.findByProblemIdOrderByOrderIndexAsc(p.getId());
        List<SolutionApproachDto> approachDtos = approaches.stream()
                .map(SolutionApproachDto::from)
                .collect(Collectors.toList());

        return ProblemDetailDto.from(p, solved.contains(p.getId()), lastCode, approachDtos);
    }

    @Transactional
    public UserProblemAttempt submitAttempt(String slug, SubmitAttemptRequest req, String userEmail) {
        Problem problem = problemRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new EntityNotFoundException("Problem not found: " + slug));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        UserProblemAttempt attempt = UserProblemAttempt.builder()
                .user(user)
                .problem(problem)
                .submittedCode(req.submittedCode())
                .status(UserProblemAttempt.Status.valueOf(req.status().toUpperCase()))
                .timeTakenSec(req.timeTakenSec())
                .language(req.language() != null ? req.language() : "JAVA")
                .build();
        return attemptRepository.save(attempt);
    }

    @Transactional(readOnly = true)
    public List<String> getTopics() {
        return problemRepository.findDistinctTopics();
    }

    // A2Z Sheet Methods

    @Transactional(readOnly = true)
    public List<ProblemSummaryDto> listA2ZProblems(Integer stepNumber, String section, String difficulty, String pattern, String userEmail) {
        String diffFilter = (difficulty != null && !difficulty.isBlank()) ? difficulty.toUpperCase() : null;
        String sectionFilter = (section != null && !section.isBlank()) ? section : null;
        String patternFilter = (pattern != null && !pattern.isBlank()) ? pattern : null;

        List<Problem> problems = problemRepository.findA2ZFiltered(stepNumber, sectionFilter, diffFilter, patternFilter);

        Set<UUID> solved = getSolvedIds(userEmail);
        return problems.stream()
                .map(p -> ProblemSummaryDto.from(p, solved.contains(p.getId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getA2ZSteps() {
        List<Problem> all = problemRepository.findByIsActiveTrueOrderByStepNumberAscStepOrderAsc();

        Map<Integer, List<Problem>> byStep = all.stream()
                .filter(p -> p.getStepNumber() != null)
                .collect(Collectors.groupingBy(Problem::getStepNumber));

        return byStep.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("stepNumber", entry.getKey());
                    map.put("sectionName", entry.getValue().get(0).getSectionName());
                    map.put("problemCount", entry.getValue().size());
                    map.put("solvedCount", 0);
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getA2ZSections() {
        return problemRepository.findDistinctSections();
    }

    @Transactional(readOnly = true)
    public List<String> getA2ZPatterns() {
        return problemRepository.findDistinctPatternTags();
    }

    @Transactional(readOnly = true)
    public A2ZStatsDto getA2ZStats(String userEmail) {
        List<Problem> allProblems = problemRepository.findByIsActiveTrueOrderByStepNumberAscStepOrderAsc();
        Set<UUID> solvedIds = getSolvedIds(userEmail);

        int total = allProblems.size();
        long easy = allProblems.stream().filter(p -> p.getDifficulty() == Problem.Difficulty.EASY).count();
        long medium = allProblems.stream().filter(p -> p.getDifficulty() == Problem.Difficulty.MEDIUM).count();
        long hard = allProblems.stream().filter(p -> p.getDifficulty() == Problem.Difficulty.HARD).count();
        int solved = (int) allProblems.stream().filter(p -> solvedIds.contains(p.getId())).count();

        // Stats by step
        Map<Integer, List<Problem>> byStep = allProblems.stream()
                .filter(p -> p.getStepNumber() != null)
                .collect(Collectors.groupingBy(Problem::getStepNumber));

        List<A2ZStatsDto.StepStats> stepStats = byStep.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    int step = entry.getKey();
                    List<Problem> problems = entry.getValue();
                    String sectionName = problems.get(0).getSectionName();
                    int stepTotal = problems.size();
                    int stepSolved = (int) problems.stream().filter(p -> solvedIds.contains(p.getId())).count();
                    return new A2ZStatsDto.StepStats(step, sectionName, stepTotal, stepSolved,
                            stepTotal > 0 ? (double) stepSolved / stepTotal * 100 : 0);
                })
                .collect(Collectors.toList());

        return new A2ZStatsDto(total, (int) easy, (int) medium, (int) hard, solved,
                total > 0 ? (double) solved / total * 100 : 0, stepStats, List.of());
    }

    private Set<UUID> getSolvedIds(String email) {
        if (email == null) return Set.of();
        UUID userId = getUserId(email);
        return attemptRepository.findSolvedProblemIds(userId).stream().collect(Collectors.toSet());
    }

    private UUID getUserId(String email) {
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElse(UUID.randomUUID());
    }
}
