package com.interviewprep.resources.repository;

import com.interviewprep.resources.model.LearningResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface LearningResourceRepository extends JpaRepository<LearningResource, UUID> {

    List<LearningResource> findByTopicOrderByDisplayOrderAsc(String topic);

    List<LearningResource> findByTopicAndTypeOrderByDisplayOrderAsc(
            String topic, LearningResource.ResourceType type);

    @Query("SELECT DISTINCT r.topic FROM LearningResource r ORDER BY r.topic")
    List<String> findDistinctTopics();

    List<LearningResource> findAllByOrderByTopicAscDisplayOrderAsc();
}
