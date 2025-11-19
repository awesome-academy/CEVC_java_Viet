-- V11: Add additional fields to tours table
-- Description: Add duration, max_participants, and location columns to tours table
ALTER TABLE tours
ADD COLUMN duration INT NULL COMMENT 'Tour duration in days',
    ADD COLUMN max_participants INT NULL COMMENT 'Maximum number of participants',
    ADD COLUMN location VARCHAR(255) NULL COMMENT 'Tour location';
-- Add constraint for duration (must be >= 1 if provided)
ALTER TABLE tours
ADD CONSTRAINT chk_tour_duration CHECK (
        duration IS NULL
        OR duration >= 1
    );
-- Add constraint for max_participants (must be >= 1 if provided)
ALTER TABLE tours
ADD CONSTRAINT chk_tour_max_participants CHECK (
        max_participants IS NULL
        OR max_participants >= 1
    );
-- Create index for location search
CREATE INDEX idx_tours_location ON tours(location);