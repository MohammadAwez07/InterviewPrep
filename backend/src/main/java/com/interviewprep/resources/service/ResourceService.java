package com.interviewprep.resources.service;

import com.interviewprep.resources.dto.ResourceDto;
import com.interviewprep.resources.model.LearningResource;
import com.interviewprep.resources.repository.LearningResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final LearningResourceRepository repository;

    @Transactional(readOnly = true)
    public List<ResourceDto> getByTopic(String topic, String type) {
        List<LearningResource> resources;
        if (type != null && !type.isBlank()) {
            resources = repository.findByTopicAndTypeOrderByDisplayOrderAsc(
                    topic, LearningResource.ResourceType.valueOf(type.toUpperCase()));
        } else {
            resources = repository.findByTopicOrderByDisplayOrderAsc(topic);
        }
        return resources.stream().map(ResourceDto::from).toList();
    }

    @Transactional(readOnly = true)
    public Map<String, List<ResourceDto>> getAllGroupedByTopic() {
        return repository.findAllByOrderByTopicAscDisplayOrderAsc()
                .stream()
                .map(ResourceDto::from)
                .collect(Collectors.groupingBy(ResourceDto::topic,
                        java.util.LinkedHashMap::new, Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public List<String> getTopics() {
        return repository.findDistinctTopics();
    }
}
