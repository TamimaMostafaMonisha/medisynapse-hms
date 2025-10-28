-- ============================================================================
-- Verify Appointment Status Update
-- Purpose: Check if appointment status is being updated correctly
-- ============================================================================

-- Check current status of all appointments for doctor 7
SELECT
    id,
    fk_patient_id,
    DATE(start_time) as date,
    TIME(start_time) as time,
    status,
    completed_at,
    last_updated_dt
FROM appointment
WHERE fk_doctor_id = 7
  AND DATE(start_time) = CURDATE()
ORDER BY start_time;

-- To manually mark an appointment as completed (for testing):
-- UPDATE appointment
-- SET status = 'COMPLETED',
--     completed_at = NOW(),
--     last_updated_dt = NOW()
-- WHERE id = YOUR_APPOINTMENT_ID
--   AND fk_doctor_id = 7;

