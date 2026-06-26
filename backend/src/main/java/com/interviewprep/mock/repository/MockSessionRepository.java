package com.interviewprep.mock.repository;

import com.interviewprep.mock.model.MockSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MockSessionRepository extends JpaRepository<MockSession, UUID> {
    List<MockSession> findByUserIdOrderByStartedAtDesc(UUID userId);
}
