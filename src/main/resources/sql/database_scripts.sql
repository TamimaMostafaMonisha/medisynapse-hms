-- ============================================================================
-- Medisynapse HMS Database changes Script
-- ============================================================================

-- [2024-09-30] Purpose: Add phone number support for users
-- Description: Adding phone column to user table to support hospital admin creation
-- and user contact information management
ALTER TABLE user
    ADD COLUMN phone VARCHAR(20) NULL
        COMMENT 'Phone number for the user';

-- Add index for phone number for better query performance
CREATE INDEX idx_user_phone ON user (phone);

-- [2024-09-30] Purpose: Add password reset tracking
-- Description: Adding password reset timestamp to track when passwords were last reset
-- for security and audit purposes
ALTER TABLE user
    ADD COLUMN password_reset_dt DATETIME NULL
        COMMENT 'Timestamp when password was last reset';

-- Add index for password reset tracking
CREATE INDEX idx_user_password_reset ON user (password_reset_dt);

-- [2024-09-30] Purpose: Add user login tracking
-- Description: Adding last login timestamp to track user activity and login patterns
-- for security monitoring and user engagement analytics
ALTER TABLE user
    ADD COLUMN last_login_dt DATETIME NULL
        COMMENT 'Timestamp when user last logged in';

-- Add index for last login tracking
CREATE INDEX idx_user_last_login ON user (last_login_dt);

