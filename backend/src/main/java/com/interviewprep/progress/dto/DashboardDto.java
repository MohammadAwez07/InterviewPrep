package com.interviewprep.progress.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record DashboardDto(
        int currentStreak,
        int longestStreak,
        long totalProblemsSolved,
        long totalCardsReviewed,
        long dueCardsToday,
        Map<String, Long> solvedByTopic,
        List<HeatmapEntry> heatmap
) {
    public record HeatmapEntry(LocalDate date, int problemsSolved, int cardsReviewed) {}
}
