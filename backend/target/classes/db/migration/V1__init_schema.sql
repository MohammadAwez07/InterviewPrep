-- ============================================================
-- V1: Core schema — users, problems, flashcards, progress,
--     planner, design, mock sessions (MySQL Version)
-- ============================================================

-- ── Users ────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    id            CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(255) NOT NULL,
    role          VARCHAR(50)  NOT NULL DEFAULT 'USER',
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ── Problems ─────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS problems (
    id               CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    title            VARCHAR(255)  NOT NULL,
    slug             VARCHAR(255)  NOT NULL UNIQUE,
    difficulty       VARCHAR(20)   NOT NULL CHECK (difficulty IN ('EASY','MEDIUM','HARD')),
    topic            VARCHAR(100)  NOT NULL,
    step_number      INTEGER,
    section_name     VARCHAR(100),
    sub_topic        VARCHAR(100),
    pattern_tags     JSON,
    step_order       INTEGER,
    video_solution_url VARCHAR(500),
    article_solution_url VARCHAR(500),
    description      TEXT          NOT NULL,
    constraints_text TEXT,
    examples         JSON,
    hints            JSON,
    solution_code    TEXT,
    solution_explanation TEXT,
    time_complexity  VARCHAR(50),
    space_complexity VARCHAR(50),
    is_active        BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_problems_topic ON problems(topic);
CREATE INDEX idx_problems_difficulty ON problems(difficulty);
CREATE INDEX idx_problems_slug ON problems(slug);
CREATE INDEX idx_problems_step ON problems(step_number);
CREATE INDEX idx_problems_section ON problems(section_name);

-- ── User Problem Attempts ─────────────────────────────────────
CREATE TABLE IF NOT EXISTS user_problem_attempts (
    id              CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id         CHAR(36)     NOT NULL,
    problem_id      CHAR(36)     NOT NULL,
    submitted_code  TEXT,
    language        VARCHAR(20)  NOT NULL DEFAULT 'JAVA',
    status          VARCHAR(20)  NOT NULL CHECK (status IN ('ACCEPTED','PARTIAL','FAILED')),
    time_taken_sec  INTEGER,
    submitted_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_attempts_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_attempts_problem FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE
);

CREATE INDEX idx_attempts_user_id ON user_problem_attempts(user_id);
CREATE INDEX idx_attempts_problem_id ON user_problem_attempts(problem_id);
CREATE INDEX idx_attempts_submitted ON user_problem_attempts(submitted_at);

-- ── Flashcards ───────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS flashcards (
    id             CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    topic          VARCHAR(100) NOT NULL,
    sub_topic      VARCHAR(100),
    question       TEXT         NOT NULL,
    answer         TEXT         NOT NULL,
    difficulty_hint VARCHAR(20) DEFAULT 'MEDIUM',
    is_active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_flashcards_topic ON flashcards(topic);

-- ── User Flashcard Progress (SM-2) ───────────────────────────
CREATE TABLE IF NOT EXISTS user_flashcard_progress (
    id               CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id          CHAR(36)     NOT NULL,
    flashcard_id     CHAR(36)     NOT NULL,
    ease_factor      DECIMAL(4,2) NOT NULL DEFAULT 2.50,
    interval_days    INTEGER      NOT NULL DEFAULT 0,
    repetitions      INTEGER      NOT NULL DEFAULT 0,
    next_review_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_reviewed_at TIMESTAMP,
    UNIQUE (user_id, flashcard_id),
    CONSTRAINT fk_fp_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_fp_flashcard FOREIGN KEY (flashcard_id) REFERENCES flashcards(id) ON DELETE CASCADE
);

CREATE INDEX idx_fp_user_id ON user_flashcard_progress(user_id);
CREATE INDEX idx_fp_next_review ON user_flashcard_progress(user_id, next_review_at);

-- ── Study Plans ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS study_plans (
    id           CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id      CHAR(36)     NOT NULL,
    target_date  DATE         NOT NULL,
    weak_topics  JSON,
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_plans_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_plans_user_id ON study_plans(user_id);

CREATE TABLE IF NOT EXISTS study_plan_days (
    id             CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    plan_id        CHAR(36)     NOT NULL,
    day_number     INTEGER      NOT NULL,
    scheduled_date DATE         NOT NULL,
    topic          VARCHAR(100) NOT NULL,
    subtopic       VARCHAR(200),
    problems_count INTEGER      NOT NULL DEFAULT 2,
    flashcards_count INTEGER    NOT NULL DEFAULT 10,
    notes          TEXT,
    is_completed   BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_days_plan FOREIGN KEY (plan_id) REFERENCES study_plans(id) ON DELETE CASCADE
);

CREATE INDEX idx_plan_days_plan_id ON study_plan_days(plan_id);

-- ── Design Sessions (System Design Whiteboard) ───────────────
CREATE TABLE IF NOT EXISTS design_sessions (
    id           CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id      CHAR(36)     NOT NULL,
    title        VARCHAR(255) NOT NULL DEFAULT 'Untitled Design',
    canvas_data  JSON         NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_design_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_design_user_id ON design_sessions(user_id);

-- ── Mock Interview Sessions ───────────────────────────────────
CREATE TABLE IF NOT EXISTS mock_sessions (
    id              CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id         CHAR(36)     NOT NULL,
    problem_id      CHAR(36),
    started_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ended_at        TIMESTAMP,
    submitted_code  TEXT,
    language        VARCHAR(20)  NOT NULL DEFAULT 'JAVA',
    ai_feedback     JSON,
    score           INTEGER CHECK (score BETWEEN 0 AND 100),
    duration_sec    INTEGER,
    status          VARCHAR(20)  NOT NULL DEFAULT 'IN_PROGRESS'
                        CHECK (status IN ('IN_PROGRESS','SUBMITTED','TIMED_OUT')),
    CONSTRAINT fk_mock_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_mock_problem FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE SET NULL
);

CREATE INDEX idx_mock_user_id ON mock_sessions(user_id);

-- ── User Activity Streak (materialised for fast dashboard) ───
CREATE TABLE IF NOT EXISTS user_activity (
    id          CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id     CHAR(36)     NOT NULL,
    activity_date DATE       NOT NULL,
    problems_solved INTEGER  NOT NULL DEFAULT 0,
    cards_reviewed  INTEGER  NOT NULL DEFAULT 0,
    UNIQUE(user_id, activity_date),
    CONSTRAINT fk_activity_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_activity_user_date ON user_activity(user_id, activity_date);
