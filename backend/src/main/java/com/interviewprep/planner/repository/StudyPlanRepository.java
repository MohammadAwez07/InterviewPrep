package com.interviewprep.planner.repository;

import com.interviewprep.planner.model.StudyPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudyPlanRepository extends JpaRepository<StudyPlan, UUID> {
    Optional<StudyPlan> findByUserIdAndIsActiveTrue(UUID userId);
    List<StudyPlan> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
