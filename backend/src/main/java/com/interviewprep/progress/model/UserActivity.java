package com.interviewprep.progress.model;

import com.interviewprep.auth.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "user_activity",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","activity_date"}))
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "activity_date", nullable = false)
    private LocalDate activityDate;

    @Column(name = "problems_solved", nullable = false)
    @Builder.Default
    private Integer problemsSolved = 0;

    @Column(name = "cards_reviewed", nullable = false)
    @Builder.Default
    private Integer cardsReviewed = 0;
}
