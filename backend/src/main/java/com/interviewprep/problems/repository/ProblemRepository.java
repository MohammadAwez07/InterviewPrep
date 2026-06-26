package com.interviewprep.problems.repository;

import com.interviewprep.problems.model.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProblemRepository extends JpaRepository<Problem, UUID> {

    Optional<Problem> findBySlugAndIsActiveTrue(String slug);

    long countByIsActiveTrue();

    Page<Problem> findByIsActiveTrue(Pageable pageable);

    List<Problem> findByIsActiveTrueOrderByDifficultyAscTitleAsc();

    List<Problem> findByTopicAndIsActiveTrueOrderByTitleAsc(String topic);

    List<Problem> findByDifficultyAndIsActiveTrueOrderByTitleAsc(Problem.Difficulty difficulty);

    List<Problem> findByTopicAndDifficultyAndIsActiveTrueOrderByTitleAsc(String topic, Problem.Difficulty difficulty);

    @Query("""
        SELECT p FROM Problem p
        WHERE p.isActive = true
          AND (:topic IS NULL OR p.topic = :topic)
          AND (:difficulty IS NULL OR p.difficulty = :difficulty)
        """)
    Page<Problem> findFiltered(
            @Param("topic") String topic,
            @Param("difficulty") Problem.Difficulty difficulty,
            Pageable pageable);

    @Query("SELECT DISTINCT p.topic FROM Problem p WHERE p.isActive = true ORDER BY p.topic")
    List<String> findDistinctTopics();

    // A2Z Sheet queries
    List<Problem> findByIsActiveTrueOrderByStepNumberAscStepOrderAsc();

    List<Problem> findByStepNumberAndIsActiveTrueOrderByStepOrderAsc(Integer stepNumber);

    List<Problem> findByStepNumberAndDifficultyAndIsActiveTrueOrderByStepOrderAsc(Integer stepNumber, Problem.Difficulty difficulty);

    List<Problem> findBySectionNameAndIsActiveTrueOrderByStepNumberAscStepOrderAsc(String sectionName);

    @Query(value = """
        SELECT * FROM problems p
        WHERE p.is_active = true
          AND JSON_CONTAINS(p.pattern_tags, JSON_QUOTE(:pattern))
        ORDER BY p.step_number ASC, p.step_order ASC
        """, nativeQuery = true)
    List<Problem> findByPatternTag(@Param("pattern") String pattern);

    @Query("""
        SELECT DISTINCT p.stepNumber, p.sectionName 
        FROM Problem p 
        WHERE p.isActive = true AND p.stepNumber IS NOT NULL 
        ORDER BY p.stepNumber
        """)
    List<Object[]> findDistinctSteps();

    @Query("""
        SELECT DISTINCT p.sectionName 
        FROM Problem p 
        WHERE p.isActive = true AND p.sectionName IS NOT NULL 
        ORDER BY p.sectionName
        """)
    List<String> findDistinctSections();

    @Query(value = """
        SELECT DISTINCT jt.tag
        FROM problems p,
        JSON_TABLE(p.pattern_tags, '$[*]' COLUMNS (tag VARCHAR(100) PATH '$')) AS jt
        WHERE p.is_active = true
        ORDER BY jt.tag
        """, nativeQuery = true)
    List<String> findDistinctPatternTags();

    @Query(value = """
        SELECT * FROM problems p
        WHERE p.is_active = true
          AND (:stepNumber IS NULL OR p.step_number = :stepNumber)
          AND (:section IS NULL OR p.section_name = :section)
          AND (:difficulty IS NULL OR p.difficulty = :difficulty)
          AND (:pattern IS NULL OR JSON_CONTAINS(p.pattern_tags, JSON_QUOTE(:pattern)))
        ORDER BY p.step_number ASC, p.step_order ASC
        """, nativeQuery = true)
    List<Problem> findA2ZFiltered(
            @Param("stepNumber") Integer stepNumber,
            @Param("section") String section,
            @Param("difficulty") String difficulty,
            @Param("pattern") String pattern);

    @Query("""
        SELECT COUNT(p) as total,
               SUM(CASE WHEN p.difficulty = 'EASY' THEN 1 ELSE 0 END) as easy,
               SUM(CASE WHEN p.difficulty = 'MEDIUM' THEN 1 ELSE 0 END) as medium,
               SUM(CASE WHEN p.difficulty = 'HARD' THEN 1 ELSE 0 END) as hard
        FROM Problem p
        WHERE p.isActive = true AND (:stepNumber IS NULL OR p.stepNumber = :stepNumber)
        """)
    Object[] getA2ZStatsByStep(@Param("stepNumber") Integer stepNumber);
}
