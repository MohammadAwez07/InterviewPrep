-- ============================================================
-- V6: A2Z Schema Changes (MySQL Version)
-- Note: A2Z columns (step_number, section_name, etc.) already added in V1
-- This migration creates the solution_approaches table only
-- ============================================================

-- Create solution_approaches table for storing multiple solution approaches per problem
CREATE TABLE IF NOT EXISTS solution_approaches (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    problem_id CHAR(36) NOT NULL,
    approach_type VARCHAR(20) NOT NULL CHECK (approach_type IN ('BRUTE_FORCE', 'BETTER', 'OPTIMAL')),
    approach_name VARCHAR(200) NOT NULL,
    time_complexity VARCHAR(50),
    space_complexity VARCHAR(50),
    code TEXT,
    explanation TEXT,
    intuition TEXT,
    is_optimal BOOLEAN DEFAULT FALSE,
    order_index INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_solutions_problem FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE
);

CREATE INDEX idx_solutions_problem ON solution_approaches(problem_id);
CREATE INDEX idx_solutions_type ON solution_approaches(approach_type);
