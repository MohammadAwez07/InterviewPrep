package com.interviewprep.analysis.repository;

import com.interviewprep.analysis.model.JobAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobAnalysisRepository extends JpaRepository<JobAnalysis, UUID> {

    List<JobAnalysis> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<JobAnalysis> findByCacheKeyHashAndCachedTrue(String cacheKeyHash);
}
