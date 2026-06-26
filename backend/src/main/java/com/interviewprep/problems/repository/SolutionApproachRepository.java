package com.interviewprep.problems.repository;

import com.interviewprep.problems.model.SolutionApproach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SolutionApproachRepository extends JpaRepository<SolutionApproach, UUID> {

    List<SolutionApproach> findByProblemIdOrderByOrderIndexAsc(UUID problemId);

    @Query("SELECT sa FROM SolutionApproach sa WHERE sa.problem.id = :problemId AND sa.approachType = :type")
    SolutionApproach findByProblemIdAndApproachType(@Param("problemId") UUID problemId, 
                                                      @Param("type") SolutionApproach.ApproachType type);
}
