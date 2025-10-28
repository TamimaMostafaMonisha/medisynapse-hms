-- ============================================================================
-- Doctor API Test Data Enhancement
-- Date: October 26, 2025
-- Purpose: Add current date appointments for testing doctor APIs
-- ============================================================================

-- Add appointments for TODAY (2025-10-26) for testing
INSERT INTO appointment (fk_patient_id, fk_doctor_id, fk_department_id, fk_hospital_id, start_time, end_time,
                         duration_minutes, appointment_type, status, reason, notes, created_by)
VALUES
-- Doctor 7 (Dr. Shahidul Islam - Cardiology) - Today's appointments
(1, 7, 1, 1, '2025-10-26 09:00:00', '2025-10-26 09:30:00', 30, 'CONSULTATION', 'SCHEDULED',
 'Follow-up cardiac evaluation', 'Patient recovering well from previous treatment', 7),
(5, 7, 1, 1, '2025-10-26 10:00:00', '2025-10-26 10:30:00', 30, 'FOLLOW_UP', 'CONFIRMED',
 'Post-angioplasty checkup', '', 7),
(10, 7, 1, 1, '2025-10-26 11:00:00', '2025-10-26 11:30:00', 30, 'CONSULTATION', 'SCHEDULED',
 'Chest pain evaluation', '', 7),
(15, 7, 1, 1, '2025-10-26 14:00:00', '2025-10-26 14:30:00', 30, 'ROUTINE_CHECKUP', 'SCHEDULED',
 'Regular cardiac checkup', '', 7),
(20, 7, 1, 1, '2025-10-26 15:30:00', '2025-10-26 16:00:00', 30, 'CONSULTATION', 'IN_PROGRESS',
 'Hypertension management', 'Patient currently being seen', 7),

-- Additional COMPLETED appointments for doctor 7 (for prescription testing)
(25, 7, 1, 1, '2025-10-26 08:00:00', '2025-10-26 08:30:00', 30, 'CONSULTATION', 'COMPLETED',
 'Morning checkup', 'Patient examined, vitals stable', 7),
(30, 7, 1, 1, '2025-10-26 08:30:00', '2025-10-26 09:00:00', 30, 'FOLLOW_UP', 'COMPLETED',
 'Follow-up visit', 'Progress reviewed', 7),

-- Doctor 8 (Dr. Rashida Begum - Neurology) - Today's appointments
(3, 8, 2, 1, '2025-10-26 09:30:00', '2025-10-26 10:00:00', 30, 'CONSULTATION', 'COMPLETED',
 'Migraine consultation', 'Prescribed new medication', 8),
(8, 8, 2, 1, '2025-10-26 11:00:00', '2025-10-26 11:45:00', 45, 'FOLLOW_UP', 'CONFIRMED',
 'Epilepsy follow-up', '', 8),
(12, 8, 2, 1, '2025-10-26 14:30:00', '2025-10-26 15:00:00', 30, 'CONSULTATION', 'SCHEDULED',
 'Neurological assessment', '', 8),

-- Doctor 11 (Dr. Mizanur Rahman - Orthopedics) - Today's appointments
(2, 11, 4, 2, '2025-10-26 08:00:00', '2025-10-26 08:30:00', 30, 'CONSULTATION', 'COMPLETED',
 'Knee pain evaluation', 'X-ray ordered', 11),
(6, 11, 4, 2, '2025-10-26 10:00:00', '2025-10-26 10:30:00', 30, 'FOLLOW_UP', 'SCHEDULED',
 'Post-surgery follow-up', '', 11),
(11, 11, 4, 2, '2025-10-26 15:00:00', '2025-10-26 15:30:00', 30, 'CONSULTATION', 'SCHEDULED',
 'Back pain consultation', '', 11);

-- Add upcoming appointments for next 7 days for Doctor 7
INSERT INTO appointment (fk_patient_id, fk_doctor_id, fk_department_id, fk_hospital_id, start_time, end_time,
                         duration_minutes, appointment_type, status, reason, notes, created_by)
VALUES
-- Tomorrow (2025-10-27)
(25, 7, 1, 1, '2025-10-27 09:00:00', '2025-10-27 09:30:00', 30, 'CONSULTATION', 'SCHEDULED',
 'New patient consultation', '', 7),
(30, 7, 1, 1, '2025-10-27 14:00:00', '2025-10-27 14:30:00', 30, 'FOLLOW_UP', 'CONFIRMED',
 'Post-treatment follow-up', '', 7),

-- Day after tomorrow (2025-10-28)
(35, 7, 1, 1, '2025-10-28 10:00:00', '2025-10-28 10:30:00', 30, 'ROUTINE_CHECKUP', 'SCHEDULED',
 'Annual heart checkup', '', 7),
(40, 7, 1, 1, '2025-10-28 15:00:00', '2025-10-28 15:30:00', 30, 'CONSULTATION', 'SCHEDULED',
 'Cardiac assessment', '', 7),

-- 3 days ahead (2025-10-29)
(45, 7, 1, 1, '2025-10-29 11:00:00', '2025-10-29 11:30:00', 30, 'FOLLOW_UP', 'CONFIRMED',
 'Blood pressure monitoring', '', 7),

-- 4 days ahead (2025-10-30)
(50, 7, 1, 1, '2025-10-30 09:30:00', '2025-10-30 10:00:00', 30, 'CONSULTATION', 'SCHEDULED',
 'Chest discomfort evaluation', '', 7),
(55, 7, 1, 1, '2025-10-30 14:00:00', '2025-10-30 14:30:00', 30, 'FOLLOW_UP', 'SCHEDULED',
 'Post-procedure checkup', '', 7),

-- 5-6 days ahead (2025-10-31 to 2025-11-01)
(60, 7, 1, 1, '2025-10-31 10:00:00', '2025-10-31 10:30:00', 30, 'ROUTINE_CHECKUP', 'SCHEDULED',
 'Regular cardiac monitoring', '', 7),
(65, 7, 1, 1, '2025-11-01 11:00:00', '2025-11-01 11:30:00', 30, 'CONSULTATION', 'SCHEDULED',
 'Heart health consultation', '', 7);

-- Add some admission data for testing
INSERT INTO admission (fk_patient_id, fk_hospital_id, fk_department_id, fk_attending_doctor_id,
                      admission_date, bed_number, reason_for_admission, status, created_by)
VALUES
(1, 1, 1, 7, '2025-10-26 08:00:00', 'A-301', 'Cardiac monitoring required', 'ADMITTED', 7),
(10, 1, 1, 7, '2025-10-26 07:30:00', 'A-305', 'Post-surgery observation', 'ADMITTED', 7),
(2, 2, 4, 11, '2025-10-25 14:00:00', 'B-201', 'Hip replacement surgery', 'ADMITTED', 11),
(3, 1, 2, 8, '2025-10-26 06:00:00', 'C-102', 'Neurological observation', 'ADMITTED', 8);

-- Add some NO_SHOW and CANCELLED appointments for statistics
INSERT INTO appointment (fk_patient_id, fk_doctor_id, fk_department_id, fk_hospital_id, start_time, end_time,
                         duration_minutes, appointment_type, status, reason, notes, cancelled_at, created_by)
VALUES
(70, 7, 1, 1, '2025-10-20 09:00:00', '2025-10-20 09:30:00', 30, 'CONSULTATION', 'NO_SHOW',
 'Regular checkup', 'Patient did not show up', NULL, 7),
(75, 7, 1, 1, '2025-10-22 10:00:00', '2025-10-22 10:30:00', 30, 'FOLLOW_UP', 'CANCELLED',
 'Follow-up visit', 'Patient requested cancellation', '2025-10-21 14:30:00', 7);

-- Update some appointments to COMPLETED with completion timestamps
UPDATE appointment
SET status = 'COMPLETED', completed_at = start_time
WHERE id IN (SELECT id FROM (SELECT id FROM appointment WHERE fk_doctor_id = 7 AND status = 'COMPLETED' LIMIT 5) as temp);

-- Add prescription test data
INSERT INTO prescription (fk_patient_id, fk_doctor_id, fk_hospital_id, fk_appointment_id,
                         prescription_date, medication_name, dosage, frequency, duration,
                         instructions, notes, status, refills_allowed, created_by)
VALUES
-- Doctor 7 (Dr. Shahidul Islam) prescriptions
(1, 7, 1, NULL, '2025-10-26', 'Amoxicillin', '500mg', 'Three times daily', '7 days',
 'Take with food. Complete the full course even if symptoms improve.',
 'Patient has no known allergies. Monitor for any adverse reactions.', 'ACTIVE', 0, 7),

(5, 7, 1, NULL, '2025-10-15', 'Lisinopril', '10mg', 'Once daily', '90 days',
 'Take in the morning with water',
 'For blood pressure management', 'ACTIVE', 2, 7),

(10, 7, 1, NULL, '2025-09-26', 'Metformin', '500mg', 'Twice daily', '90 days',
 'Take with meals',
 'For diabetes management', 'ACTIVE', 3, 7),

(15, 7, 1, NULL, '2025-10-20', 'Aspirin', '75mg', 'Once daily', '180 days',
 'Take after breakfast with water',
 'For cardiovascular protection', 'ACTIVE', 1, 7),

(20, 7, 1, NULL, '2025-08-15', 'Atorvastatin', '20mg', 'Once daily at bedtime', '90 days',
 'Take at the same time each day',
 'For cholesterol management. Completed course.', 'COMPLETED', 0, 7),

-- Doctor 8 (Dr. Rashida Begum) prescriptions
(3, 8, 1, NULL, '2025-10-26', 'Sumatriptan', '50mg', 'As needed for migraine', '30 days',
 'Take at the first sign of migraine. Maximum 2 doses per day.',
 'Patient responds well to this medication', 'ACTIVE', 1, 8),

(8, 8, 1, NULL, '2025-10-10', 'Levetiracetam', '500mg', 'Twice daily', '90 days',
 'Take at the same time each day. Do not stop suddenly.',
 'For epilepsy management. Regular monitoring required.', 'ACTIVE', 2, 8),

-- Doctor 11 (Dr. Mizanur Rahman) prescriptions
(2, 11, 2, NULL, '2025-10-25', 'Ibuprofen', '400mg', 'Every 6 hours as needed', '5 days',
 'Take with food to avoid stomach upset. Do not exceed 4 doses in 24 hours.',
 'For pain management post-procedure', 'ACTIVE', 0, 11),

(6, 11, 2, NULL, '2025-10-01', 'Celecoxib', '200mg', 'Once daily', '30 days',
 'Take with food',
 'For arthritis pain management', 'ACTIVE', 1, 11),

(11, 11, 2, NULL, '2025-09-15', 'Diclofenac', '50mg', 'Twice daily', '14 days',
 'Apply gel to affected area',
 'For back pain. Course completed.', 'COMPLETED', 0, 11);

-- ============================================================================
-- Add prescriptions linked to COMPLETED appointments for Doctor 7
-- Note: Get actual appointment IDs first, then link prescriptions
-- ============================================================================

-- Step 1: Find the appointment IDs for the COMPLETED appointments
-- Run this query to get the IDs:
-- SELECT id, fk_patient_id, start_time, status FROM appointment
-- WHERE fk_doctor_id = 7 AND status = 'COMPLETED'
-- ORDER BY start_time DESC LIMIT 2;

-- Step 2: Insert prescriptions linked to those appointments
-- Replace the appointment IDs below with actual IDs from your database

-- Prescription for Patient 25's COMPLETED appointment (08:00 appointment)
INSERT INTO prescription (fk_patient_id, fk_doctor_id, fk_hospital_id, fk_appointment_id,
                         prescription_date, medication_name, dosage, frequency, duration,
                         instructions, notes, status, refills_allowed, created_by)
SELECT 25, 7, 1, a.id, '2025-10-26', 'Amlodipine', '5mg', 'Once daily', '30 days',
       'Take at the same time each day for blood pressure control',
       'Patient responded well to treatment. Monitor BP weekly.', 'ACTIVE', 1, 7
FROM appointment a
WHERE a.fk_patient_id = 25 AND a.fk_doctor_id = 7
  AND a.status = 'COMPLETED'
  AND DATE(a.start_time) = '2025-10-26'
LIMIT 1;

-- Prescription for Patient 30's COMPLETED appointment (08:30 appointment)
INSERT INTO prescription (fk_patient_id, fk_doctor_id, fk_hospital_id, fk_appointment_id,
                         prescription_date, medication_name, dosage, frequency, duration,
                         instructions, notes, status, refills_allowed, created_by)
SELECT 30, 7, 1, a.id, '2025-10-26', 'Carvedilol', '6.25mg', 'Twice daily', '60 days',
       'Take with food. May cause dizziness initially.',
       'Good progress on current treatment. Continue monitoring.', 'ACTIVE', 2, 7
FROM appointment a
WHERE a.fk_patient_id = 30 AND a.fk_doctor_id = 7
  AND a.status = 'COMPLETED'
  AND DATE(a.start_time) = '2025-10-26'
LIMIT 1;

-- Prescription for Patient 1's SCHEDULED appointment (can be used for CREATE prescription API testing)
-- This shows how appointment IDs can be found for SCHEDULED appointments
-- Run: SELECT id FROM appointment WHERE fk_patient_id = 1 AND fk_doctor_id = 7 AND status = 'SCHEDULED' LIMIT 1;

COMMIT;

