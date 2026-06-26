package com.interviewprep.design.service;

import com.interviewprep.auth.model.User;
import com.interviewprep.auth.repository.UserRepository;
import com.interviewprep.design.dto.SaveDesignRequest;
import com.interviewprep.design.model.DesignSession;
import com.interviewprep.design.repository.DesignSessionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DesignService {

    private final DesignSessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Transactional
    public DesignSession createSession(SaveDesignRequest req, String email) {
        User user = findUser(email);
        DesignSession session = DesignSession.builder()
                .user(user)
                .title(req.title() != null ? req.title() : "Untitled Design")
                .canvasData(req.canvasData())
                .build();
        return sessionRepository.save(session);
    }

    @Transactional
    public DesignSession updateSession(UUID id, SaveDesignRequest req, String email) {
        DesignSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Design session not found"));
        if (!session.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("Not authorised to modify this session");
        }
        if (req.title() != null) session.setTitle(req.title());
        session.setCanvasData(req.canvasData());
        return sessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public List<DesignSession> getSessions(String email) {
        UUID userId = findUser(email).getId();
        return sessionRepository.findByUserIdOrderByUpdatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public DesignSession getSession(UUID id, String email) {
        DesignSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Design session not found"));
        if (!session.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("Not authorised");
        }
        return session;
    }

    @Transactional
    public void deleteSession(UUID id, String email) {
        DesignSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Design session not found"));
        if (!session.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("Not authorised");
        }
        sessionRepository.delete(session);
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
