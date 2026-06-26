package com.interviewprep.problems.repository;

import com.interviewprep.problems.model.UserProblemAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserProblemAttemptRepository extends JpaRepository<UserProblemAttempt, UUID> {

    List<UserProblemAttempt> findByUserIdOrderBySubmittedAtDesc(UUID userId);

    @Query("""
        SELECT a FROM UserProblemAttempt a
        WHERE a.user.id = :userId AND a.problem.id = :problemId
        ORDER BY a.submittedAt DESC
        LIMIT 1
        """)
    Optional<UserProblemAttempt> findLatestByUserAndProblem(
            @Param("userId") UUID userId, @Param("problemId") UUID problemId);

    @Query("""
        SELECT COUNT(DISTINCT a.problem.id) FROM UserProblemAttempt a
        WHERE a.user.id = :userId AND a.status = 'ACCEPTED'
        """)
    long countSolvedByUser(@Param("userId") UUID userId);

    @Query("""
        SELECT a.problem.topic, COUNT(DISTINCT a.problem.id)
        FROM UserProblemAttempt a
        WHERE a.user.id = :userId AND a.status = 'ACCEPTED'
        GROUP BY a.problem.topic
        """)
    List<Object[]> countSolvedByTopicForUser(@Param("userId") UUID userId);

    @Query("""
        SELECT COUNT(a) FROM UserProblemAttempt a
        WHERE a.user.id = :userId
          AND a.submittedAt >= :since
        """)
    long countAttemptsAfter(@Param("userId") UUID userId, @Param("since") Instant since);

    @Query("""
        SELECT DISTINCT a.problem.id FROM UserProblemAttempt a
        WHERE a.user.id = :userId AND a.status = 'ACCEPTED'
        """)
    List<UUID> findSolvedProblemIds(@Param("userId") UUID userId);
}
