-- ============================================================================
-- Complete Database Fix for Appointment Type ENUM Issue
-- Date: October 26, 2025
-- Purpose: Add missing appointment types and statuses to ENUMs
-- Run this script BEFORE loading test data
-- ============================================================================

-- STEP 1: Check current state
SELECT 'Current appointment types in use:' as info;
SELECT DISTINCT appointment_type, COUNT(*) as count 
FROM appointment 
GROUP BY appointment_type;

-- STEP 2: Add new appointment_type ENUM values
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

SELECT '✅ Step 1 complete: Added appointment types' as status;

-- STEP 3: Add new status ENUM values (IN_PROGRESS and RESCHEDULED)
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

SELECT '✅ Step 2 complete: Added IN_PROGRESS and RESCHEDULED statuses' as status;

-- STEP 4: Verify the final state
SELECT 'Final verification:' as info;
SHOW COLUMNS FROM appointment LIKE 'appointment_type';
SHOW COLUMNS FROM appointment LIKE 'status';

SELECT 'Final appointment type counts:' as info;
SELECT appointment_type, COUNT(*) as count
FROM appointment
GROUP BY appointment_type
ORDER BY appointment_type;

SELECT 'Final appointment status counts:' as info;
SELECT status, COUNT(*) as count
FROM appointment
GROUP BY status
ORDER BY status;

SELECT '✅ All appointment types and statuses are now available!' as status;

