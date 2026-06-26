package com.interviewprep.design.repository;

import com.interviewprep.design.model.DesignSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DesignSessionRepository extends JpaRepository<DesignSession, UUID> {
    List<DesignSession> findByUserIdOrderByUpdatedAtDesc(UUID userId);
}
