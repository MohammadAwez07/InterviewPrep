package com.interviewprep.resources.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "learning_resources")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class LearningResource {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Matches the topic names used in StudyPlan curriculum */
    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String title;

    /** YouTube / Udemy / NeetCode / LeetCode / GeeksForGeeks / Article */
    @Column(nullable = false)
    private String provider;

    /** VIDEO | COURSE | ARTICLE | PRACTICE | PLAYLIST */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ResourceType type;

    @Column(nullable = false, length = 1000)
    private String url;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** Human-readable: "2h 30m", "45 min", "10 problems" */
    private String duration;

    /** Whether this is a premium paid resource */
    @Column(name = "is_free", nullable = false)
    @Builder.Default
    private Boolean isFree = true;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 100;

    public enum ResourceType { VIDEO, COURSE, ARTICLE, PRACTICE, PLAYLIST }
}
