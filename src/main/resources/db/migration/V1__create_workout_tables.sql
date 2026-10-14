-- V1__create_workout_tables.sql

-- 1. Workout templates / splits
CREATE TABLE splits (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 2. Exercises planned inside a split
CREATE TABLE split_exercises (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    split_id UUID NOT NULL
        REFERENCES splits(id) ON DELETE CASCADE,

    exercise_id UUID NOT NULL,
    exercise_order INTEGER NOT NULL,
    target_sets INTEGER NOT NULL DEFAULT 3,
    target_reps_min INTEGER NOT NULL DEFAULT 8,
    target_reps_max INTEGER NOT NULL DEFAULT 12,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_split_exercise_order
        CHECK (exercise_order >= 0),

    CONSTRAINT chk_target_sets
        CHECK (target_sets > 0),

    CONSTRAINT chk_target_reps
        CHECK (
            target_reps_min > 0
            AND target_reps_max >= target_reps_min
        ),

    CONSTRAINT uq_split_exercise_order
        UNIQUE (split_id, exercise_order)
);

-- 3. Actual workout sessions
CREATE TABLE workout_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id UUID NOT NULL,

    split_id UUID
        REFERENCES splits(id) ON DELETE SET NULL,

    split_name_snapshot VARCHAR(100),

    started_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    finished_at TIMESTAMPTZ,

    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    notes TEXT,

    CONSTRAINT chk_workout_status
        CHECK (
            status IN (
                'IN_PROGRESS',
                'COMPLETED',
                'CANCELLED'
            )
        ),

    CONSTRAINT chk_workout_finish_time
        CHECK (
            finished_at IS NULL
            OR finished_at >= started_at
        )
);

-- 4. Actual sets performed during a workout
CREATE TABLE session_sets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    session_id UUID NOT NULL
        REFERENCES workout_sessions(id) ON DELETE CASCADE,

    split_exercise_id UUID
        REFERENCES split_exercises(id) ON DELETE SET NULL,

    exercise_id UUID NOT NULL,
    exercise_name_snapshot VARCHAR(150) NOT NULL,

    set_number INTEGER NOT NULL,

    target_reps INTEGER,
    actual_reps INTEGER,
    weight_kg NUMERIC(7,2),

    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at TIMESTAMPTZ,

    CONSTRAINT chk_set_number
        CHECK (set_number > 0),

    CONSTRAINT chk_target_reps_positive
        CHECK (
            target_reps IS NULL
            OR target_reps > 0
        ),

    CONSTRAINT chk_actual_reps_nonnegative
        CHECK (
            actual_reps IS NULL
            OR actual_reps >= 0
        ),

    CONSTRAINT chk_weight_nonnegative
        CHECK (
            weight_kg IS NULL
            OR weight_kg >= 0
        ),

    CONSTRAINT uq_session_exercise_set
        UNIQUE (session_id, exercise_id, set_number)
);

-- Indexes for common queries
CREATE INDEX idx_splits_user_id
    ON splits(user_id);

-- Check if index already exists or create
CREATE INDEX idx_sessions_user_started
    ON workout_sessions(user_id, started_at DESC);

CREATE INDEX idx_session_sets_session
    ON session_sets(session_id);
