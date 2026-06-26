package com.interviewprep.resources.dto;

import com.interviewprep.resources.model.LearningResource;

import java.util.UUID;

public record ResourceDto(
        UUID id,
        String topic,
        String title,
        String provider,
        LearningResource.ResourceType type,
        String url,
        String description,
        String duration,
        boolean isFree
) {
    public static ResourceDto from(LearningResource r) {
        return new ResourceDto(
                r.getId(), r.getTopic(), r.getTitle(), r.getProvider(),
                r.getType(), r.getUrl(), r.getDescription(), r.getDuration(), r.getIsFree());
    }
}
