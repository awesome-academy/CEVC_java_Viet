-- Create persistent_logins table for remember-me functionality
-- This table stores persistent login tokens for the remember-me feature
CREATE TABLE persistent_logins (
    username VARCHAR(64) NOT NULL,
    series VARCHAR(64) PRIMARY KEY,
    token VARCHAR(64) NOT NULL,
    last_used TIMESTAMP NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
-- Add index on username for faster lookups
CREATE INDEX idx_persistent_logins_username ON persistent_logins(username);
-- Add index on last_used for cleanup of old tokens
CREATE INDEX idx_persistent_logins_last_used ON persistent_logins(last_used);