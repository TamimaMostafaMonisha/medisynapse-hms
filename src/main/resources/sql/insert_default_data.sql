-- ============================================================================
-- Multi-Hospital Management System - Default Data Insert Script
-- ============================================================================

-- Insert default addresses
INSERT INTO address (id, line1, line2, city, state, postal_code, country, type, created_by, is_active)
VALUES 
(1, '123 Main Street', 'Suite 100', 'New York', 'NY', '10001', 'USA', 'WORK', 1, true),
(2, '456 Oak Avenue', 'Building B', 'Los Angeles', 'CA', '90210', 'USA', 'WORK', 1, true),
(3, '789 Pine Road', 'Floor 3', 'Chicago', 'IL', '60601', 'USA', 'WORK', 1, true),
(4, '321 Maple Drive', '', 'Houston', 'TX', '77001', 'USA', 'WORK', 1, true),
(5, '654 Cedar Lane', 'Unit A', 'Phoenix', 'AZ', '85001', 'USA', 'WORK', 1, true);

-- Insert default hospitals
INSERT INTO hospital (id, name, fk_address_id, contact, created_by, is_active)
VALUES 
(1, 'City General Hospital', 1, '+1-555-1000', 1, true),
(2, 'Green Valley Medical Center', 2, '+1-555-2000', 1, true),
(3, 'Metropolitan Health System', 3, '+1-555-3000', 1, true),
(4, 'Sunrise Community Hospital', 4, '+1-555-4000', 1, true),
(5, 'Desert Medical Complex', 5, '+1-555-5000', 1, true);

-- Insert default departments
INSERT INTO department (id, fk_hospital_id, name, description, created_by, is_active)
VALUES 
-- City General Hospital departments
(1, 1, 'Emergency Medicine', 'Emergency and trauma care services', 1, true),
(2, 1, 'Cardiology', 'Heart and cardiovascular diseases treatment', 1, true),
(3, 1, 'Neurology', 'Brain and nervous system disorders', 1, true),
(4, 1, 'Orthopedics', 'Bone, joint, and muscle treatments', 1, true),
(5, 1, 'Pediatrics', 'Healthcare for infants, children, and adolescents', 1, true),
(6, 1, 'Obstetrics and Gynecology', 'Women''s health and childbirth', 1, true),
(7, 1, 'Internal Medicine', 'General internal medicine and adult care', 1, true),
(8, 1, 'Surgery', 'General and specialized surgical procedures', 1, true),
(9, 1, 'Radiology', 'Medical imaging and diagnostic services', 1, true),
(10, 1, 'Laboratory', 'Clinical laboratory and pathology services', 1, true),
(11, 1, 'Pharmacy', 'Medication management and dispensing', 1, true),
(12, 1, 'ICU', 'Intensive Care Unit', 1, true),

-- Green Valley Medical Center departments
(13, 2, 'Emergency Medicine', 'Emergency and trauma care services', 1, true),
(14, 2, 'Cardiology', 'Heart and cardiovascular diseases treatment', 1, true),
(15, 2, 'Pediatrics', 'Healthcare for infants, children, and adolescents', 1, true),
(16, 2, 'Internal Medicine', 'General internal medicine and adult care', 1, true),
(17, 2, 'Surgery', 'General and specialized surgical procedures', 1, true),
(18, 2, 'Radiology', 'Medical imaging and diagnostic services', 1, true),
(19, 2, 'Laboratory', 'Clinical laboratory and pathology services', 1, true),
(20, 2, 'ICU', 'Intensive Care Unit', 1, true),

-- Metropolitan Health System departments
(21, 3, 'Emergency Medicine', 'Emergency and trauma care services', 1, true),
(22, 3, 'Cardiology', 'Heart and cardiovascular diseases treatment', 1, true),
(23, 3, 'Neurology', 'Brain and nervous system disorders', 1, true),
(24, 3, 'Orthopedics', 'Bone, joint, and muscle treatments', 1, true),
(25, 3, 'Pediatrics', 'Healthcare for infants, children, and adolescents', 1, true),
(26, 3, 'Internal Medicine', 'General internal medicine and adult care', 1, true),
(27, 3, 'Surgery', 'General and specialized surgical procedures', 1, true),
(28, 3, 'Radiology', 'Medical imaging and diagnostic services', 1, true),

-- Sunrise Community Hospital departments
(29, 4, 'Emergency Medicine', 'Emergency and trauma care services', 1, true),
(30, 4, 'Internal Medicine', 'General internal medicine and adult care', 1, true),
(31, 4, 'Pediatrics', 'Healthcare for infants, children, and adolescents', 1, true),
(32, 4, 'Surgery', 'General and specialized surgical procedures', 1, true),

-- Desert Medical Complex departments
(33, 5, 'Emergency Medicine', 'Emergency and trauma care services', 1, true),
(34, 5, 'Cardiology', 'Heart and cardiovascular diseases treatment', 1, true),
(35, 5, 'Internal Medicine', 'General internal medicine and adult care', 1, true),
(36, 5, 'Surgery', 'General and specialized surgical procedures', 1, true);

-- Insert Super Admin user
INSERT INTO user (id, name, email, password_hash, role, status, created_by, is_active)
VALUES 
(1, 'Super Admin', 'superadmin@medcare.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM4JzjRemy.ODVCmDyli', 'SUPER_ADMIN', 'ACTIVE', 1, true);

-- Insert Hospital Admins
INSERT INTO user (id, name, email, password_hash, role, fk_hospital_id, status, created_by, is_active)
VALUES 
(2, 'Dr. Sarah Johnson', 'admin@cityhospital.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM4JzjRemy.ODVCmDyli', 'HOSPITAL_ADMIN', 1, 'ACTIVE', 1, true),
(3, 'Dr. Michael Chen', 'admin@greenvallhospital.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM4JzjRemy.ODVCmDyli', 'HOSPITAL_ADMIN', 2, 'ACTIVE', 1, true),
(4, 'Dr. Patricia Williams', 'admin@metrohealthsystem.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM4JzjRemy.ODVCmDyli', 'HOSPITAL_ADMIN', 3, 'ACTIVE', 1, true),
(5, 'Dr. Robert Garcia', 'admin@sunrisehospital.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM4JzjRemy.ODVCmDyli', 'HOSPITAL_ADMIN', 4, 'ACTIVE', 1, true),
(6, 'Dr. Jennifer Martinez', 'admin@desertmedical.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM4JzjRemy.ODVCmDyli', 'HOSPITAL_ADMIN', 5, 'ACTIVE', 1, true);

-- Insert sample doctors
INSERT INTO user (id, name, email, password_hash, role, fk_hospital_id, fk_department_id, status, created_by, is_active)
VALUES 
-- City General Hospital doctors
(7, 'Dr. Emily Rodriguez', 'emily.rodriguez@cityhospital.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM4JzjRemy.ODVCmDyli', 'DOCTOR', 1, 2, 'ACTIVE', 1, true),
(8, 'Dr. James Wilson', 'james.wilson@cityhospital.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM4JzjRemy.ODVCmDyli', 'DOCTOR', 1, 3, 'ACTIVE', 1, true),
(9, 'Dr. Maria Garcia', 'maria.garcia@cityhospital.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM4JzjRemy.ODVCmDyli', 'DOCTOR', 1, 5, 'ACTIVE', 1, true),
(10, 'Dr. David Brown', 'david.brown@cityhospital.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM4JzjRemy.ODVCmDyli', 'DOCTOR', 1, 8, 'ACTIVE', 1, true),

-- Green Valley Medical Center doctors
(11, 'Dr. Lisa Davis', 'lisa.davis@greenvallhospital.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM4JzjRemy.ODVCmDyli', 'DOCTOR', 2, 15, 'ACTIVE', 1, true),
(12, 'Dr. Thomas Anderson', 'thomas.anderson@greenvallhospital.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM4JzjRemy.ODVCmDyli', 'DOCTOR', 2, 14, 'ACTIVE', 1, true),

-- Metropolitan Health System doctors
(13, 'Dr. Amanda Smith', 'amanda.smith@metrohealthsystem.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM4JzjRemy.ODVCmDyli', 'DOCTOR', 3, 22, 'ACTIVE', 1, true),
(14, 'Dr. Kevin Lee', 'kevin.lee@metrohealthsystem.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM4JzjRemy.ODVCmDyli', 'DOCTOR', 3, 23, 'ACTIVE', 1, true);

-- Insert sample nurses
INSERT INTO user (id, name, email, password_hash, role, fk_hospital_id, fk_department_id, status, created_by, is_active)
VALUES 
(15, 'Nurse Jennifer White', 'jennifer.white@cityhospital.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM4JzjRemy.ODVCmDyli', 'NURSE', 1, 1, 'ACTIVE', 1, true),
(16, 'Nurse Michael Taylor', 'michael.taylor@cityhospital.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM4JzjRemy.ODVCmDyli', 'NURSE', 1, 12, 'ACTIVE', 1, true),
(17, 'Nurse Sandra Johnson', 'sandra.johnson@greenvallhospital.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM4JzjRemy.ODVCmDyli', 'NURSE', 2, 13, 'ACTIVE', 1, true);

-- Insert sample patients
INSERT INTO patient (id, first_name, last_name, national_id, dob, gender, contact, status, created_by, is_active)
VALUES 
(1, 'John', 'Doe', 'NID001234567', '1980-05-15', 'MALE', '+1-555-0001', 'ACTIVE', 1, true),
(2, 'Jane', 'Smith', 'NID001234568', '1992-08-22', 'FEMALE', '+1-555-0002', 'ACTIVE', 1, true),
(3, 'Robert', 'Johnson', 'NID001234569', '1975-12-10', 'MALE', '+1-555-0003', 'ACTIVE', 1, true),
(4, 'Mary', 'Williams', 'NID001234570', '1988-03-18', 'FEMALE', '+1-555-0004', 'ACTIVE', 1, true),
(5, 'James', 'Brown', 'NID001234571', '1965-11-28', 'MALE', '+1-555-0005', 'ACTIVE', 1, true);

-- Register patients with hospitals
INSERT INTO patient_hospital (fk_patient_id, fk_hospital_id, patient_id_number, status, created_by, is_active)
VALUES 
(1, 1, 'CGH-001', 'ACTIVE', 1, true),
(2, 1, 'CGH-002', 'ACTIVE', 1, true),
(3, 2, 'GV-001', 'ACTIVE', 1, true),
(4, 2, 'GV-002', 'ACTIVE', 1, true),
(5, 3, 'MHS-001', 'ACTIVE', 1, true);

-- Insert sample appointments
INSERT INTO appointment (fk_patient_id, fk_doctor_id, fk_department_id, fk_hospital_id, date_time, status, created_by, is_active)
VALUES 
(1, 7, 2, 1, '2024-12-01 10:00:00', 'SCHEDULED', 1, true),
(2, 8, 3, 1, '2024-12-01 14:30:00', 'SCHEDULED', 1, true),
(3, 11, 15, 2, '2024-12-02 09:00:00', 'SCHEDULED', 1, true),
(4, 12, 14, 2, '2024-12-02 11:30:00', 'SCHEDULED', 1, true),
(5, 13, 22, 3, '2024-12-03 15:00:00', 'SCHEDULED', 1, true);

-- Set the auto-increment values to continue from the inserted data
ALTER TABLE address AUTO_INCREMENT = 6;
ALTER TABLE hospital AUTO_INCREMENT = 6;
ALTER TABLE department AUTO_INCREMENT = 37;
ALTER TABLE user AUTO_INCREMENT = 18;
ALTER TABLE patient AUTO_INCREMENT = 6;
ALTER TABLE patient_hospital AUTO_INCREMENT = 6;
ALTER TABLE appointment AUTO_INCREMENT = 6;

-- Commit the transaction
COMMIT;

-- Display summary of inserted data
SELECT 'Data insertion completed successfully!' as Status;
SELECT COUNT(*) as TotalAddresses FROM address WHERE is_active = true;
SELECT COUNT(*) as TotalHospitals FROM hospital WHERE is_active = true;
SELECT COUNT(*) as TotalDepartments FROM department WHERE is_active = true;
SELECT COUNT(*) as TotalUsers FROM user WHERE is_active = true;
SELECT COUNT(*) as TotalPatients FROM patient WHERE is_active = true;
SELECT COUNT(*) as TotalAppointments FROM appointment WHERE is_active = true;




-- Add missing columns to hospital table for frontend application mapping
ALTER TABLE hospital
    ADD COLUMN IF NOT EXISTS type VARCHAR(100),
    ADD COLUMN IF NOT EXISTS address TEXT,
    ADD COLUMN IF NOT EXISTS phone VARCHAR(20),
    ADD COLUMN IF NOT EXISTS email VARCHAR(100),
    ADD COLUMN IF NOT EXISTS total_beds INT,
    ADD COLUMN IF NOT EXISTS available_beds INT,
    ADD COLUMN IF NOT EXISTS total_departments INT,
    ADD COLUMN IF NOT EXISTS total_staff INT,
    ADD COLUMN IF NOT EXISTS established VARCHAR(4),
    ADD COLUMN IF NOT EXISTS accreditation VARCHAR(255),
    ADD COLUMN IF NOT EXISTS status VARCHAR(50) DEFAULT 'Active',
    ADD COLUMN IF NOT EXISTS admin_id BIGINT;

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
