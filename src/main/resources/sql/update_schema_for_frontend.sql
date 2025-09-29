-- Add missing columns to hospital table for frontend application mapping
-- Using individual ALTER TABLE statements for better MySQL version compatibility

ALTER TABLE hospital ADD COLUMN type VARCHAR(100);
ALTER TABLE hospital ADD COLUMN address TEXT;
ALTER TABLE hospital ADD COLUMN phone VARCHAR(20);
ALTER TABLE hospital ADD COLUMN email VARCHAR(100);
ALTER TABLE hospital ADD COLUMN total_beds INT;
ALTER TABLE hospital ADD COLUMN available_beds INT;
ALTER TABLE hospital ADD COLUMN total_departments INT;
ALTER TABLE hospital ADD COLUMN total_staff INT;
ALTER TABLE hospital ADD COLUMN established VARCHAR(4);
ALTER TABLE hospital ADD COLUMN accreditation VARCHAR(255);
ALTER TABLE hospital ADD COLUMN status VARCHAR(50) DEFAULT 'Active';
ALTER TABLE hospital ADD COLUMN admin_id BIGINT;

-- Update existing records with default values if needed
UPDATE hospital SET
    type = 'General Hospital',
    address = 'Address Not Specified',
    phone = '+1-000-0000',
    email = 'info@hospital.com',
    total_beds = 100,
    available_beds = 50,
    total_departments = 5,
    total_staff = 100,
    established = '2000',
    accreditation = 'Standard Accredited',
    status = 'Active'
WHERE type IS NULL OR type = '';

-- Create department_type table
CREATE TABLE IF NOT EXISTS department_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    code VARCHAR(10) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_dt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert default department types
INSERT INTO department_type (name, code, description) VALUES
('Emergency Medicine', 'EM', 'Emergency and trauma care services'),
('Cardiology', 'CARD', 'Heart and cardiovascular diseases treatment'),
('Neurology', 'NEURO', 'Brain and nervous system disorders'),
('Orthopedics', 'ORTHO', 'Bone and joint treatment'),
('Pediatrics', 'PED', 'Medical care for children'),
('Internal Medicine', 'IM', 'General internal medicine'),
('Surgery', 'SURG', 'General surgery department'),
('Radiology', 'RAD', 'Medical imaging and radiology'),
('Laboratory', 'LAB', 'Clinical laboratory services'),
('Pharmacy', 'PHARM', 'Pharmaceutical services')
ON DUPLICATE KEY UPDATE
    description = VALUES(description),
    last_updated_dt = CURRENT_TIMESTAMP;
