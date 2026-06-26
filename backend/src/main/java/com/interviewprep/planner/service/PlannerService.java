package com.interviewprep.planner.service;

import com.interviewprep.auth.model.User;
import com.interviewprep.auth.repository.UserRepository;
import com.interviewprep.planner.dto.GeneratePlanRequest;
import com.interviewprep.planner.model.StudyPlan;
import com.interviewprep.planner.model.StudyPlanDay;
import com.interviewprep.planner.repository.StudyPlanRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlannerService {

    private final StudyPlanRepository planRepository;
    private final UserRepository userRepository;

    // Ordered curriculum (Must-Know first, then Good-to-Have)
    private static final List<String[]> CURRICULUM = List.of(
        new String[]{"Arrays", "Sliding window, prefix sum, two pointers"},
        new String[]{"Strings", "Palindrome, anagram, KMP"},
        new String[]{"HashMaps & Sets", "Frequency count, two-sum patterns"},
        new String[]{"Two Pointers", "Sorted arrays, Dutch national flag"},
        new String[]{"Sliding Window", "Fixed and variable window"},
        new String[]{"Linked Lists", "Fast/slow pointers, reversal, cycle detection"},
        new String[]{"Stacks & Queues", "Monotonic stack, BFS"},
        new String[]{"Binary Search", "On value space, rotated arrays"},
        new String[]{"Trees - BFS", "Level-order, zigzag, right-side view"},
        new String[]{"Trees - DFS", "Path sum, LCA, diameter"},
        new String[]{"Recursion", "Base cases, call stack visualisation"},
        new String[]{"Backtracking", "Subsets, permutations, N-Queens"},
        new String[]{"Graphs - BFS/DFS", "Islands, shortest path"},
        new String[]{"Graphs - Advanced", "Dijkstra, topological sort, Union-Find"},
        new String[]{"Heaps", "Top-K, median stream, merge K lists"},
        new String[]{"Greedy", "Interval scheduling, jump game"},
        new String[]{"Dynamic Programming - 1D", "Fibonacci, house robber, climb stairs"},
        new String[]{"Dynamic Programming - 2D", "LCS, knapsack, edit distance"},
        new String[]{"Dynamic Programming - Advanced", "Palindrome partitioning, word break II"},
        new String[]{"Tries", "Autocomplete, word search II"},
        new String[]{"Mock Interview Week", "Full timed mock interviews — 2 problems per day"}
    );

    @Transactional
    public StudyPlan generatePlan(GeneratePlanRequest req, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Deactivate existing plan
        planRepository.findByUserIdAndIsActiveTrue(user.getId())
                .ifPresent(p -> { p.setIsActive(false); planRepository.save(p); });

        long daysAvailable = LocalDate.now().until(req.targetDate()).toTotalMonths() * 30
                + LocalDate.now().until(req.targetDate()).getDays();
        int totalDays = (int) Math.max(daysAvailable, 7);

        StudyPlan plan = StudyPlan.builder()
                .user(user)
                .targetDate(req.targetDate())
                .weakTopics(req.weakTopics() != null ? req.weakTopics() : List.of())
                .build();

        // Prioritise weak topics first in the schedule
        List<String[]> ordered = buildOrderedCurriculum(req.weakTopics());
        List<StudyPlanDay> days = new ArrayList<>();
        LocalDate date = LocalDate.now();

        for (int dayNum = 1; dayNum <= totalDays; dayNum++) {
            String[] topic = ordered.get((dayNum - 1) % ordered.size());
            boolean isMockWeek = topic[0].contains("Mock");
            StudyPlanDay day = StudyPlanDay.builder()
                    .plan(plan)
                    .dayNumber(dayNum)
                    .scheduledDate(date)
                    .topic(topic[0])
                    .subtopic(topic[1])
                    .problemsCount(isMockWeek ? 2 : (dayNum <= 14 ? 1 : 2))
                    .flashcardsCount(isMockWeek ? 5 : 10)
                    .notes(buildNotes(topic[0], dayNum))
                    .build();
            days.add(day);
            date = date.plusDays(1);
        }

        plan.getDays().addAll(days);
        return planRepository.save(plan);
    }

    @Transactional(readOnly = true)
    public StudyPlan getActivePlan(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return planRepository.findByUserIdAndIsActiveTrue(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("No active study plan found"));
    }

    @Transactional
    public StudyPlanDay markDayComplete(java.util.UUID dayId, String email) {
        StudyPlan plan = planRepository.findByUserIdAndIsActiveTrue(
                userRepository.findByEmail(email).orElseThrow().getId())
                .orElseThrow(() -> new EntityNotFoundException("No active plan"));
        StudyPlanDay day = plan.getDays().stream()
                .filter(d -> d.getId().equals(dayId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Day not found in plan"));
        day.setIsCompleted(true);
        planRepository.save(plan);
        return day;
    }

    private List<String[]> buildOrderedCurriculum(List<String> weakTopics) {
        if (weakTopics == null || weakTopics.isEmpty()) return CURRICULUM;
        List<String[]> prioritised = new ArrayList<>();
        List<String[]> rest = new ArrayList<>();
        for (String[] entry : CURRICULUM) {
            boolean isWeak = weakTopics.stream()
                    .anyMatch(w -> entry[0].toLowerCase().contains(w.toLowerCase()));
            if (isWeak) prioritised.add(entry);
            else rest.add(entry);
        }
        prioritised.addAll(rest);
        return prioritised;
    }

    private String buildNotes(String topic, int day) {
        return String.format("Day %d: Focus on %s. Watch Udemy video, then solve problems on NeetCode/LeetCode.", day, topic);
    }
}
