-- ============================================================================
-- Database Migration: Update Appointment ENUMs
-- Date: October 26, 2025
-- Purpose: Add missing values to appointment_type and status ENUMs
-- ============================================================================

-- Step 1: Add new appointment types
ALTER TABLE appointment
MODIFY COLUMN appointment_type ENUM(
    'CONSULTATION',
    'FOLLOW_UP',
    'EMERGENCY',
    'SURGERY',
    'ROUTINE_CHECKUP',
    'SURGICAL_CONSULTATION',
    'DIAGNOSTIC'
) DEFAULT 'CONSULTATION';

-- Step 2: Add new status values
ALTER TABLE appointment
MODIFY COLUMN status ENUM(
    'SCHEDULED',
    'CONFIRMED',
    'CANCELLED',
    'COMPLETED',
    'NO_SHOW',
    'IN_PROGRESS',
    'RESCHEDULED'
) DEFAULT 'SCHEDULED';

SELECT 'Migration complete!' as status;

-- Verify the change
SHOW COLUMNS FROM appointment LIKE 'appointment_type';

-- Optional: Display count of appointments by type to verify no data was lost
SELECT appointment_type, COUNT(*) as count
FROM appointment
GROUP BY appointment_type;

