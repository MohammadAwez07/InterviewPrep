package com.interviewprep.mock.service;

import com.interviewprep.auth.model.User;
import com.interviewprep.auth.repository.UserRepository;
import com.interviewprep.mock.dto.StartSessionRequest;
import com.interviewprep.mock.dto.SubmitCodeRequest;
import com.interviewprep.mock.model.MockSession;
import com.interviewprep.mock.repository.MockSessionRepository;
import com.interviewprep.problems.model.Problem;
import com.interviewprep.problems.repository.ProblemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MockService {

    private final MockSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final OpenAIService openAIService;

    @Transactional
    public MockSession startSession(StartSessionRequest req, String email) {
        User user = findUser(email);
        Problem problem = null;
        if (req.problemId() != null) {
            problem = problemRepository.findById(req.problemId())
                    .orElseThrow(() -> new EntityNotFoundException("Problem not found"));
        } else {
            // Pick a random medium problem
            List<Problem> problems = problemRepository.findFiltered(null, Problem.Difficulty.MEDIUM,
                    org.springframework.data.domain.PageRequest.of(0, 50)).getContent();
            if (!problems.isEmpty()) {
                problem = problems.get((int) (Math.random() * problems.size()));
            }
        }
        MockSession session = MockSession.builder()
                .user(user)
                .problem(problem)
                .build();
        return sessionRepository.save(session);
    }

    @Transactional
    public MockSession submitCode(UUID sessionId, SubmitCodeRequest req, String email) {
        MockSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));
        if (!session.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("Not authorised");
        }
        if (!"IN_PROGRESS".equals(session.getStatus())) {
            throw new IllegalArgumentException("Session already submitted");
        }

        Instant now = Instant.now();
        int durationSec = (int) (now.getEpochSecond() - session.getStartedAt().getEpochSecond());

        String lang = req.language() != null ? req.language() : "JAVA";
        Map<String, Object> feedback = Map.of(
                "score", 0, "feedback", "AI evaluation pending",
                "suggestions", List.of());

        if (session.getProblem() != null) {
            feedback = openAIService.evaluateCode(session.getProblem(), req.submittedCode(), lang);
        }

        int score = feedback.get("score") instanceof Number n ? n.intValue() : 0;

        session.setSubmittedCode(req.submittedCode());
        session.setLanguage(lang);
        session.setEndedAt(now);
        session.setDurationSec(durationSec);
        session.setAiFeedback(feedback);
        session.setScore(score);
        session.setStatus("SUBMITTED");
        return sessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public List<MockSession> getSessions(String email) {
        UUID userId = findUser(email).getId();
        return sessionRepository.findByUserIdOrderByStartedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public MockSession getSession(UUID id, String email) {
        MockSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));
        if (!session.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("Not authorised");
        }
        return session;
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
