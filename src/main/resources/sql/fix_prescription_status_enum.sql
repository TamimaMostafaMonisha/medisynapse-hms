-- ============================================================================
-- Fix Prescription Status Column - Change VARCHAR to ENUM
-- Date: October 26, 2025
-- Purpose: Fix the status column type mismatch error
-- ============================================================================

-- Step 1: Update existing data to use uppercase ENUM values
UPDATE prescription
SET status = 'ACTIVE'
WHERE status = 'Active' OR status = 'active';

UPDATE prescription
SET status = 'COMPLETED'
WHERE status = 'Completed' OR status = 'completed';

UPDATE prescription
SET status = 'CANCELLED'
WHERE status = 'Cancelled' OR status = 'cancelled';

UPDATE prescription
SET status = 'EXPIRED'
WHERE status = 'Expired' OR status = 'expired';

-- Step 2: Change column type from VARCHAR to ENUM
ALTER TABLE prescription
MODIFY COLUMN status ENUM('ACTIVE', 'COMPLETED', 'CANCELLED', 'EXPIRED') DEFAULT 'ACTIVE';

SELECT '✅ Prescription status column fixed - changed to ENUM' as status;

