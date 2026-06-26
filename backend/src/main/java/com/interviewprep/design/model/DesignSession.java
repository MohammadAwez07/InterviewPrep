package com.interviewprep.design.model;

import com.interviewprep.auth.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "design_sessions")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class DesignSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @Builder.Default
    private String title = "Untitled Design";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "canvas_data", columnDefinition = "JSON")
    @Builder.Default
    private Map<String, Object> canvasData = Map.of("nodes", java.util.List.of(), "edges", java.util.List.of());

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void onUpdate() { this.updatedAt = Instant.now(); }
}
