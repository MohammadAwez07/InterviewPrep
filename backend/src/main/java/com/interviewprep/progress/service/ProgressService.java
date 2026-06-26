package com.interviewprep.progress.service;

import com.interviewprep.auth.model.User;
import com.interviewprep.auth.repository.UserRepository;
import com.interviewprep.flashcards.repository.UserFlashcardProgressRepository;
import com.interviewprep.problems.repository.UserProblemAttemptRepository;
import com.interviewprep.progress.dto.DashboardDto;
import com.interviewprep.progress.model.UserActivity;
import com.interviewprep.progress.repository.UserActivityRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final UserRepository userRepository;
    private final UserActivityRepository activityRepository;
    private final UserProblemAttemptRepository attemptRepository;
    private final UserFlashcardProgressRepository flashcardProgressRepository;

    @Transactional(readOnly = true)
    public DashboardDto getDashboard(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        UUID userId = user.getId();

        // Heatmap — last 365 days
        List<UserActivity> yearActivity = activityRepository.findYearActivity(userId);
        List<DashboardDto.HeatmapEntry> heatmap = yearActivity.stream()
                .map(a -> new DashboardDto.HeatmapEntry(a.getActivityDate(), a.getProblemsSolved(), a.getCardsReviewed()))
                .toList();

        // Streak calculation
        int[] streaks = calculateStreaks(yearActivity);

        // Solved by topic
        List<Object[]> topicRows = attemptRepository.countSolvedByTopicForUser(userId);
        Map<String, Long> solvedByTopic = new LinkedHashMap<>();
        topicRows.forEach(row -> solvedByTopic.put((String) row[0], (Long) row[1]));

        long totalSolved = attemptRepository.countSolvedByUser(userId);
        long reviewed = flashcardProgressRepository.countReviewedCards(userId);
        long due = flashcardProgressRepository.countDueCards(userId, Instant.now());

        return new DashboardDto(streaks[0], streaks[1], totalSolved, reviewed, due, solvedByTopic, heatmap);
    }

    /** Record daily activity (called after problem submit / card review). */
    @Transactional
    public void recordProblemSolved(UUID userId) {
        bumpActivity(userId, 1, 0);
    }

    @Transactional
    public void recordCardReviewed(UUID userId) {
        bumpActivity(userId, 0, 1);
    }

    private void bumpActivity(UUID userId, int problems, int cards) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        UserActivity activity = activityRepository
                .findByUserIdAndActivityDate(userId, today)
                .orElseGet(() -> activityRepository.save(
                        UserActivity.builder().user(user).activityDate(today).build()));
        activity.setProblemsSolved(activity.getProblemsSolved() + problems);
        activity.setCardsReviewed(activity.getCardsReviewed() + cards);
        activityRepository.save(activity);
    }

    private int[] calculateStreaks(List<UserActivity> activities) {
        if (activities.isEmpty()) return new int[]{0, 0};
        // Sort descending
        List<LocalDate> activeDays = activities.stream()
                .filter(a -> a.getProblemsSolved() > 0 || a.getCardsReviewed() > 0)
                .map(UserActivity::getActivityDate)
                .sorted(Comparator.reverseOrder())
                .toList();
        if (activeDays.isEmpty()) return new int[]{0, 0};

        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        int current = 0;
        LocalDate expected = today;
        for (LocalDate d : activeDays) {
            if (d.equals(expected) || d.equals(expected.minusDays(1))) {
                current++;
                expected = d.minusDays(1);
            } else {
                break;
            }
        }

        int longest = 0, running = 1;
        for (int i = 1; i < activeDays.size(); i++) {
            if (activeDays.get(i - 1).minusDays(1).equals(activeDays.get(i))) {
                running++;
            } else {
                longest = Math.max(longest, running);
                running = 1;
            }
        }
        longest = Math.max(longest, running);
        return new int[]{current, longest};
    }
}
