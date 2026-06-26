package com.interviewprep.progress.repository;

import com.interviewprep.progress.model.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserActivityRepository extends JpaRepository<UserActivity, UUID> {

    Optional<UserActivity> findByUserIdAndActivityDate(UUID userId, LocalDate date);

    @Query("""
        SELECT a FROM UserActivity a
        WHERE a.user.id = :userId
          AND a.activityDate >= :from
        ORDER BY a.activityDate ASC
        """)
    List<UserActivity> findRecentActivity(@Param("userId") UUID userId, @Param("from") LocalDate from);

    @Query("""
        SELECT a FROM UserActivity a
        WHERE a.user.id = :userId
        ORDER BY a.activityDate DESC
        LIMIT 365
        """)
    List<UserActivity> findYearActivity(@Param("userId") UUID userId);
}
