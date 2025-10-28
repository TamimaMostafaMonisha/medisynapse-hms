-- ============================================================================
-- Database Migration: Add Prescription Management Columns
-- Date: October 26, 2025
-- Purpose: Add missing columns for prescription management APIs
-- ============================================================================

-- Add missing columns to prescription table
ALTER TABLE prescription
ADD COLUMN fk_hospital_id BIGINT AFTER fk_doctor_id,
ADD COLUMN notes TEXT AFTER instructions,
ADD COLUMN status ENUM('ACTIVE', 'COMPLETED', 'CANCELLED', 'EXPIRED') DEFAULT 'ACTIVE' AFTER notes;

-- Add foreign key constraint
ALTER TABLE prescription
ADD CONSTRAINT fk_prescription_hospital FOREIGN KEY (fk_hospital_id) REFERENCES hospital (id);

-- Add indexes for better performance
CREATE INDEX idx_prescription_hospital ON prescription(fk_hospital_id);
CREATE INDEX idx_prescription_status ON prescription(status);

-- Update existing prescriptions to have hospital_id from doctor's hospital
UPDATE prescription p
JOIN user d ON p.fk_doctor_id = d.id
SET p.fk_hospital_id = d.fk_hospital_id
WHERE p.fk_hospital_id IS NULL;

-- Make hospital_id NOT NULL after updating existing data
ALTER TABLE prescription
MODIFY COLUMN fk_hospital_id BIGINT NOT NULL;

SELECT '✅ Migration complete: Added hospital_id, notes, and status columns' as status;

