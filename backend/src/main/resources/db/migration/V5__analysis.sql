-- Job Analysis table for Interview Readiness Analyser (MySQL version)

CREATE TABLE IF NOT EXISTS job_analyses (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id CHAR(36) NOT NULL,
    job_title VARCHAR(255),
    company VARCHAR(255),
    jd_text TEXT NOT NULL,
    resume_text TEXT,
    
    -- Analysis results
    extracted_skills JSON,
    readiness_score INTEGER,
    strong_areas JSON,
    gap_areas JSON,
    recommendations JSON,
    suggested_topics JSON,
    
    -- Resume tailoring
    tailored_resume_sections JSON,
    resume_changes JSON,
    
    -- Caching
    cache_key_hash VARCHAR(64),
    cached BOOLEAN DEFAULT FALSE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_job_analyses_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_job_analyses_user_id ON job_analyses(user_id);
CREATE INDEX idx_job_analyses_cache_hash ON job_analyses(cache_key_hash, cached);
CREATE INDEX idx_job_analyses_created_at ON job_analyses(user_id, created_at DESC);
