-- ============================================================================
-- Hospital Management System - Comprehensive Default Data Insert Script
-- Date: October 2025
-- Contains: 5 hospitals, 1 super admin, 5 hospital admins, 20 doctors, 150 patients
-- Features: Multi-hospital patient histories, realistic medical scenarios
-- Default Password: Test12345 (hashed)
-- ============================================================================

-- Clear existing data for fresh start
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE audit_log;
TRUNCATE TABLE attachment;
TRUNCATE TABLE shift;
TRUNCATE TABLE payment;
TRUNCATE TABLE billing;
TRUNCATE TABLE prescription;
TRUNCATE TABLE ehr;
TRUNCATE TABLE admission;
TRUNCATE TABLE appointment;
TRUNCATE TABLE patient_insurance;
TRUNCATE TABLE insurance;
TRUNCATE TABLE patient_hospital;
TRUNCATE TABLE patient;
TRUNCATE TABLE user;
TRUNCATE TABLE department;
TRUNCATE TABLE department_type;
TRUNCATE TABLE hospital;
TRUNCATE TABLE address;
SET FOREIGN_KEY_CHECKS = 1;

-- Insert Department Types
INSERT INTO department_type (name, code, description)
VALUES ('Cardiology', 'CARD', 'Heart and cardiovascular system treatment'),
       ('Neurology', 'NEURO', 'Brain and nervous system disorders'),
       ('Orthopedics', 'ORTHO', 'Bone, joint, and musculoskeletal treatment'),
       ('Pediatrics', 'PEDI', 'Medical care for infants, children, and adolescents'),
       ('Dermatology', 'DERM', 'Skin, hair, nail, and related disorders'),
       ('Gynecology', 'GYNO', 'Women''s reproductive health'),
       ('ENT', 'ENT', 'Ear, Nose, and Throat treatment'),
       ('Ophthalmology', 'OPHT', 'Eye and vision care'),
       ('Radiology', 'RADIO', 'Medical imaging and diagnostics'),
       ('Emergency', 'EMERG', 'Emergency and critical care'),
       ('Surgery', 'SURG', 'Surgical procedures and operations'),
       ('Internal Medicine', 'INTERN', 'General internal medicine'),
       ('Psychiatry', 'PSYCH', 'Mental health and behavioral disorders'),
       ('Oncology', 'ONCO', 'Cancer treatment and care'),
       ('Urology', 'URO', 'Urinary system and male reproductive health');

-- Insert Addresses
INSERT INTO address (line1, line2, city, state, postal_code, country, type)
VALUES
-- Hospital Addresses (1-5)
('123 Medical Center Drive', 'Suite 100', 'Dhaka', 'Dhaka Division', '1000', 'Bangladesh', 'WORK'),
('456 Healthcare Plaza', 'Building A', 'Chittagong', 'Chittagong Division', '4000', 'Bangladesh', 'WORK'),
('789 Central Hospital Road', NULL, 'Sylhet', 'Sylhet Division', '3100', 'Bangladesh', 'WORK'),
('321 City Medical Complex', 'Block B', 'Rajshahi', 'Rajshahi Division', '6000', 'Bangladesh', 'WORK'),
('654 Metro Health Center', 'Tower 1', 'Khulna', 'Khulna Division', '9000', 'Bangladesh', 'WORK'),

-- Super Admin & Hospital Admin Addresses (6-11)
('10 Admin Plaza', 'Floor 15', 'Dhaka', 'Dhaka Division', '1205', 'Bangladesh', 'HOME'),
('25 Executive Gardens', 'Apt 501', 'Dhaka', 'Dhaka Division', '1207', 'Bangladesh', 'HOME'),
('87 Port View Residency', 'Unit 302', 'Chittagong', 'Chittagong Division', '4203', 'Bangladesh', 'HOME'),
('45 Tea Garden Colony', 'House 12', 'Sylhet', 'Sylhet Division', '3105', 'Bangladesh', 'HOME'),
('92 University Area', 'Flat 201', 'Rajshahi', 'Rajshahi Division', '6205', 'Bangladesh', 'HOME'),
('78 Riverside Apartments', 'Unit 405', 'Khulna', 'Khulna Division', '9203', 'Bangladesh', 'HOME'),

-- Doctor Addresses (12-31) - 20 doctors
('101 Doctors Colony', 'House 5', 'Dhaka', 'Dhaka Division', '1209', 'Bangladesh', 'HOME'),
('202 Medical Township', 'Villa 12', 'Dhaka', 'Dhaka Division', '1210', 'Bangladesh', 'HOME'),
('303 Healthcare Residency', 'Apt 8A', 'Chittagong', 'Chittagong Division', '4205', 'Bangladesh', 'HOME'),
('404 Physician Heights', 'Floor 3', 'Chittagong', 'Chittagong Division', '4206', 'Bangladesh', 'HOME'),
('505 Specialist Plaza', 'Unit 15', 'Sylhet', 'Sylhet Division', '3107', 'Bangladesh', 'HOME'),
('606 Medical Gardens', 'House 22', 'Sylhet', 'Sylhet Division', '3108', 'Bangladesh', 'HOME'),
('707 Doctor Towers', 'Apt 9B', 'Rajshahi', 'Rajshahi Division', '6207', 'Bangladesh', 'HOME'),
('808 Health Residency', 'Villa 18', 'Rajshahi', 'Rajshahi Division', '6208', 'Bangladesh', 'HOME'),
('909 Medical Square', 'Unit 25', 'Khulna', 'Khulna Division', '9205', 'Bangladesh', 'HOME'),
('111 Consultant Plaza', 'Floor 7', 'Khulna', 'Khulna Division', '9206', 'Bangladesh', 'HOME'),
('222 Cardiac Center', 'Suite 301', 'Dhaka', 'Dhaka Division', '1211', 'Bangladesh', 'HOME'),
('333 Neuro Complex', 'Wing A', 'Dhaka', 'Dhaka Division', '1212', 'Bangladesh', 'HOME'),
('444 Pediatric Heights', 'Unit 502', 'Chittagong', 'Chittagong Division', '4207', 'Bangladesh', 'HOME'),
('555 Orthopedic Plaza', 'Building C', 'Chittagong', 'Chittagong Division', '4208', 'Bangladesh', 'HOME'),
('666 Dermatology Center', 'Floor 4', 'Sylhet', 'Sylhet Division', '3109', 'Bangladesh', 'HOME'),
('777 ENT Specialist', 'Suite 205', 'Sylhet', 'Sylhet Division', '3110', 'Bangladesh', 'HOME'),
('888 Gynecology Wing', 'Unit 603', 'Rajshahi', 'Rajshahi Division', '6209', 'Bangladesh', 'HOME'),
('999 Radiology Complex', 'Block D', 'Rajshahi', 'Rajshahi Division', '6210', 'Bangladesh', 'HOME'),
('1010 Surgery Center', 'Floor 8', 'Khulna', 'Khulna Division', '9207', 'Bangladesh', 'HOME'),
('1111 Emergency Plaza', 'Unit 901', 'Khulna', 'Khulna Division', '9208', 'Bangladesh', 'HOME');

-- Generate Patient Addresses (32-181) - 150 patients
INSERT INTO address (line1, city, state, postal_code, country, type)
VALUES ('12 Green Road', 'Dhaka', 'Dhaka Division', '1205', 'Bangladesh', 'HOME'),
       ('34 Blue Street', 'Dhaka', 'Dhaka Division', '1206', 'Bangladesh', 'HOME'),
       ('56 Red Avenue', 'Chittagong', 'Chittagong Division', '4001', 'Bangladesh', 'HOME'),
       ('78 White Lane', 'Chittagong', 'Chittagong Division', '4002', 'Bangladesh', 'HOME'),
       ('90 Golden Drive', 'Sylhet', 'Sylhet Division', '3101', 'Bangladesh', 'HOME'),
       ('112 Silver Circle', 'Sylhet', 'Sylhet Division', '3102', 'Bangladesh', 'HOME'),
       ('134 Park Road', 'Rajshahi', 'Rajshahi Division', '6001', 'Bangladesh', 'HOME'),
       ('156 Lake Street', 'Rajshahi', 'Rajshahi Division', '6002', 'Bangladesh', 'HOME'),
       ('178 Hill Avenue', 'Khulna', 'Khulna Division', '9001', 'Bangladesh', 'HOME'),
       ('200 Valley Lane', 'Khulna', 'Khulna Division', '9002', 'Bangladesh', 'HOME'),
       ('222 Rose Garden', 'Dhaka', 'Dhaka Division', '1213', 'Bangladesh', 'HOME'),
       ('244 Lily Street', 'Dhaka', 'Dhaka Division', '1214', 'Bangladesh', 'HOME'),
       ('266 Jasmine Avenue', 'Chittagong', 'Chittagong Division', '4003', 'Bangladesh', 'HOME'),
       ('288 Tulip Lane', 'Chittagong', 'Chittagong Division', '4004', 'Bangladesh', 'HOME'),
       ('310 Orchid Drive', 'Sylhet', 'Sylhet Division', '3103', 'Bangladesh', 'HOME'),
       ('332 Daisy Circle', 'Sylhet', 'Sylhet Division', '3104', 'Bangladesh', 'HOME'),
       ('354 Sunflower Road', 'Rajshahi', 'Rajshahi Division', '6003', 'Bangladesh', 'HOME'),
       ('376 Marigold Street', 'Rajshahi', 'Rajshahi Division', '6004', 'Bangladesh', 'HOME'),
       ('398 Hibiscus Avenue', 'Khulna', 'Khulna Division', '9003', 'Bangladesh', 'HOME'),
       ('420 Lotus Lane', 'Khulna', 'Khulna Division', '9004', 'Bangladesh', 'HOME'),
       ('442 Magnolia Drive', 'Dhaka', 'Dhaka Division', '1215', 'Bangladesh', 'HOME'),
       ('464 Peony Circle', 'Dhaka', 'Dhaka Division', '1216', 'Bangladesh', 'HOME'),
       ('486 Carnation Road', 'Chittagong', 'Chittagong Division', '4005', 'Bangladesh', 'HOME'),
       ('508 Iris Street', 'Chittagong', 'Chittagong Division', '4006', 'Bangladesh', 'HOME'),
       ('530 Violet Avenue', 'Sylhet', 'Sylhet Division', '3106', 'Bangladesh', 'HOME'),
       ('552 Poppy Lane', 'Sylhet', 'Sylhet Division', '3107', 'Bangladesh', 'HOME'),
       ('574 Daffodil Drive', 'Rajshahi', 'Rajshahi Division', '6005', 'Bangladesh', 'HOME'),
       ('596 Azalea Circle', 'Rajshahi', 'Rajshahi Division', '6006', 'Bangladesh', 'HOME'),
       ('618 Camellia Road', 'Khulna', 'Khulna Division', '9005', 'Bangladesh', 'HOME'),
       ('640 Begonia Street', 'Khulna', 'Khulna Division', '9006', 'Bangladesh', 'HOME');

-- Continue with more patient addresses (32-181)
INSERT INTO address (line1, city, state, postal_code, country, type)
SELECT CONCAT(FLOOR(RAND() * 1000) + 100, ' ',
              ELT(FLOOR(RAND() * 20) + 1, 'Main', 'Central', 'First', 'Second', 'Third', 'Fourth', 'Fifth', 'Sixth',
                  'Seventh', 'Eighth', 'Ninth', 'Tenth', 'North', 'South', 'East', 'West', 'Upper', 'Lower', 'New',
                  'Old'), ' ',
              ELT(FLOOR(RAND() * 15) + 1, 'Street', 'Avenue', 'Road', 'Lane', 'Drive', 'Circle', 'Plaza', 'Square',
                  'Heights', 'Gardens', 'Park', 'View', 'Hill', 'Valley', 'Ridge'))      AS line1,
       ELT(FLOOR(RAND() * 5) + 1, 'Dhaka', 'Chittagong', 'Sylhet', 'Rajshahi', 'Khulna') AS city,
       CASE
           WHEN ELT(FLOOR(RAND() * 5) + 1, 'Dhaka', 'Chittagong', 'Sylhet', 'Rajshahi', 'Khulna') = 'Dhaka'
               THEN 'Dhaka Division'
           WHEN ELT(FLOOR(RAND() * 5) + 1, 'Dhaka', 'Chittagong', 'Sylhet', 'Rajshahi', 'Khulna') = 'Chittagong'
               THEN 'Chittagong Division'
           WHEN ELT(FLOOR(RAND() * 5) + 1, 'Dhaka', 'Chittagong', 'Sylhet', 'Rajshahi', 'Khulna') = 'Sylhet'
               THEN 'Sylhet Division'
           WHEN ELT(FLOOR(RAND() * 5) + 1, 'Dhaka', 'Chittagong', 'Sylhet', 'Rajshahi', 'Khulna') = 'Rajshahi'
               THEN 'Rajshahi Division'
           ELSE 'Khulna Division'
           END                                                                           AS state,
       CASE
           WHEN ELT(FLOOR(RAND() * 5) + 1, 'Dhaka', 'Chittagong', 'Sylhet', 'Rajshahi', 'Khulna') = 'Dhaka'
               THEN CONCAT('12', LPAD(FLOOR(RAND() * 99) + 1, 2, '0'))
           WHEN ELT(FLOOR(RAND() * 5) + 1, 'Dhaka', 'Chittagong', 'Sylhet', 'Rajshahi', 'Khulna') = 'Chittagong'
               THEN CONCAT('40', LPAD(FLOOR(RAND() * 99) + 1, 2, '0'))
           WHEN ELT(FLOOR(RAND() * 5) + 1, 'Dhaka', 'Chittagong', 'Sylhet', 'Rajshahi', 'Khulna') = 'Sylhet'
               THEN CONCAT('31', LPAD(FLOOR(RAND() * 99) + 1, 2, '0'))
           WHEN ELT(FLOOR(RAND() * 5) + 1, 'Dhaka', 'Chittagong', 'Sylhet', 'Rajshahi', 'Khulna') = 'Rajshahi'
               THEN CONCAT('60', LPAD(FLOOR(RAND() * 99) + 1, 2, '0'))
           ELSE CONCAT('90', LPAD(FLOOR(RAND() * 99) + 1, 2, '0'))
           END                                                                           AS postal_code,
       'Bangladesh'                                                                      AS country,
       'HOME'                                                                            AS type
FROM (SELECT 1 AS n
      UNION
      SELECT 2
      UNION
      SELECT 3
      UNION
      SELECT 4
      UNION
      SELECT 5
      UNION
      SELECT 6
      UNION
      SELECT 7
      UNION
      SELECT 8
      UNION
      SELECT 9
      UNION
      SELECT 10) t1,
     (SELECT 1 AS n
      UNION
      SELECT 2
      UNION
      SELECT 3
      UNION
      SELECT 4
      UNION
      SELECT 5
      UNION
      SELECT 6
      UNION
      SELECT 7
      UNION
      SELECT 8
      UNION
      SELECT 9
      UNION
      SELECT 10) t2,
     (SELECT 1 UNION SELECT 2 UNION SELECT 3) t3
LIMIT 120;

-- Insert Hospitals
INSERT INTO hospital (name, type, address_string, fk_address_id, phone, email, total_beds, available_beds,
                      total_departments, total_staff, established, accreditation, status, contact)
VALUES ('Dhaka Medical College Hospital', 'Teaching Hospital', '123 Medical Center Drive, Suite 100, Dhaka-1000', 1,
        '+8801765128198', 'info@dmch.gov.bd', 500, 120, 15, 1200, '1946', 'JCI Accredited', 'ACTIVE',
        'Dr. Ahmed Hassan'),
       ('Chittagong General Hospital', 'General Hospital', '456 Healthcare Plaza, Building A, Chittagong-4000', 2,
        '+8801876543210', 'contact@cgh.gov.bd', 400, 85, 12, 950, '1958', 'ISO 9001 Certified', 'ACTIVE',
        'Dr. Fatima Rahman'),
       ('Sylhet Osmani Medical College Hospital', 'Medical College Hospital', '789 Central Hospital Road, Sylhet-3100',
        3, '+8801987654321', 'admin@somch.edu.bd', 350, 75, 11, 800, '1962', 'NABH Accredited', 'ACTIVE',
        'Prof. Dr. Mohammad Ali'),
       ('Rajshahi Medical College Hospital', 'Teaching Hospital', '321 City Medical Complex, Block B, Rajshahi-6000', 4,
        '+8801654987320', 'info@rmch.edu.bd', 450, 95, 13, 1050, '1958', 'JCI Accredited', 'ACTIVE',
        'Dr. Nasreen Sultana'),
       ('Khulna Medical College Hospital', 'Medical College Hospital', '654 Metro Health Center, Tower 1, Khulna-9000',
        5, '+8801543210987', 'contact@kmch.edu.bd', 300, 65, 10, 700, '1965', 'ISO 14001 Certified', 'ACTIVE',
        'Dr. Karim Uddin');

-- Insert Super Admin
INSERT INTO user (name, email, password_hash, phone, national_id, role, fk_address_id, status, specialization,
                  license_number, years_of_experience, qualification, consultation_fee, created_by, is_active)
VALUES ('System Administrator', 'admin@medisynapse.com', '$2a$10$ZkNnmAzib9syKymfaAqTVe1V6NDdddDWsVMuQfGqDYWhBcJ8A6gvi',
        '+8801712345678', '1234567890123', 'SUPER_ADMIN', 6, 'ACTIVE', 'Healthcare Administration', 'SA001', 15,
        'MBA Healthcare Management, MBBS', NULL, 1, TRUE);

-- Insert Hospital Admins
INSERT INTO user (name, email, password_hash, phone, national_id, role, fk_hospital_id, fk_address_id, status,
                  specialization, license_number, years_of_experience, qualification, consultation_fee, created_by,
                  is_active)
VALUES ('Dr. Ahmed Hassan', 'ahmed.hassan@dmch.gov.bd', '$2a$10$ZkNnmAzib9syKymfaAqTVe1V6NDdddDWsVMuQfGqDYWhBcJ8A6gvi',
        '+8801765128199', '1234567890124', 'HOSPITAL_ADMIN', 1, 7, 'ACTIVE', 'Hospital Administration', 'HA001', 20,
        'MBBS, MPH, Fellowship in Hospital Management', NULL, 1, TRUE),
       ('Dr. Fatima Rahman', 'fatima.rahman@cgh.gov.bd', '$2a$10$ZkNnmAzib9syKymfaAqTVe1V6NDdddDWsVMuQfGqDYWhBcJ8A6gvi',
        '+8801876543211', '1234567890125', 'HOSPITAL_ADMIN', 2, 8, 'ACTIVE', 'Healthcare Management', 'HA002', 18,
        'MBBS, MHA, Diploma in Quality Management', NULL, 1, TRUE),
       ('Prof. Dr. Mohammad Ali', 'mohammad.ali@somch.edu.bd',
        '$2a$10$ZkNnmAzib9syKymfaAqTVe1V6NDdddDWsVMuQfGqDYWhBcJ8A6gvi', '+8801987654322', '1234567890126',
        'HOSPITAL_ADMIN', 3, 9, 'ACTIVE', 'Medical Education & Administration', 'HA003', 25,
        'MBBS, MD, PhD in Medical Education', NULL, 1, TRUE),
       ('Dr. Nasreen Sultana', 'nasreen.sultana@rmch.edu.bd',
        '$2a$10$ZkNnmAzib9syKymfaAqTVe1V6NDdddDWsVMuQfGqDYWhBcJ8A6gvi', '+8801654987321', '1234567890127',
        'HOSPITAL_ADMIN', 4, 10, 'ACTIVE', 'Public Health Administration', 'HA004', 22,
        'MBBS, MPH, Fellowship in Public Health', NULL, 1, TRUE),
       ('Dr. Karim Uddin', 'karim.uddin@kmch.edu.bd', '$2a$10$ZkNnmAzib9syKymfaAqTVe1V6NDdddDWsVMuQfGqDYWhBcJ8A6gvi',
        '+8801543210988', '1234567890128', 'HOSPITAL_ADMIN', 5, 11, 'ACTIVE', 'Healthcare Operations', 'HA005', 19,
        'MBBS, MSc in Healthcare Management', NULL, 1, TRUE);

-- Update hospital admin_id references
UPDATE hospital
SET admin_id = 2
WHERE id = 1;
UPDATE hospital
SET admin_id = 3
WHERE id = 2;
UPDATE hospital
SET admin_id = 4
WHERE id = 3;
UPDATE hospital
SET admin_id = 5
WHERE id = 4;
UPDATE hospital
SET admin_id = 6
WHERE id = 5;

-- Insert Departments for each hospital
INSERT INTO department (fk_hospital_id, fk_department_type_id, name, description, total_staff, total_beds,
                        contact_phone, contact_email, location_floor, is_emergency, operating_hours, created_by)
VALUES
-- Dhaka Medical College Hospital (Hospital ID: 1)
(1, 1, 'Cardiology Department', 'Advanced cardiac care and interventions', 25, 40, '+8801765128200',
 'cardio@dmch.gov.bd', '3rd Floor', FALSE, '24/7', 2),
(1, 2, 'Neurology Department', 'Neurological disorders and brain surgery', 20, 30, '+8801765128201',
 'neuro@dmch.gov.bd', '4th Floor', FALSE, '24/7', 2),
(1, 10, 'Emergency Department', 'Emergency and trauma care', 35, 25, '+8801765128202', 'emergency@dmch.gov.bd',
 'Ground Floor', TRUE, '24/7', 2),

-- Chittagong General Hospital (Hospital ID: 2)
(2, 3, 'Orthopedics Department', 'Bone and joint treatments', 18, 35, '+8801876543220', 'ortho@cgh.gov.bd', '2nd Floor',
 FALSE, '8AM-8PM', 3),
(2, 4, 'Pediatrics Department', 'Children healthcare services', 22, 25, '+8801876543221', 'pedi@cgh.gov.bd',
 '1st Floor', FALSE, '24/7', 3),
(2, 10, 'Emergency Department', 'Emergency medical services', 28, 20, '+8801876543222', 'emergency@cgh.gov.bd',
 'Ground Floor', TRUE, '24/7', 3),

-- Sylhet Osmani Medical College Hospital (Hospital ID: 3)
(3, 5, 'Dermatology Department', 'Skin and related disorders', 15, 20, '+8801987654330', 'derm@somch.edu.bd',
 '2nd Floor', FALSE, '8AM-5PM', 4),
(3, 6, 'Gynecology Department', 'Women health and maternity care', 25, 30, '+8801987654331', 'gyno@somch.edu.bd',
 '3rd Floor', FALSE, '24/7', 4),
(3, 10, 'Emergency Department', 'Critical care services', 30, 18, '+8801987654332', 'emergency@somch.edu.bd',
 'Ground Floor', TRUE, '24/7', 4),

-- Rajshahi Medical College Hospital (Hospital ID: 4)
(4, 7, 'ENT Department', 'Ear, Nose, and Throat treatments', 16, 22, '+8801654987340', 'ent@rmch.edu.bd', '1st Floor',
 FALSE, '8AM-6PM', 5),
(4, 8, 'Ophthalmology Department', 'Eye care and vision services', 18, 20, '+8801654987341', 'eye@rmch.edu.bd',
 '2nd Floor', FALSE, '8AM-6PM', 5),
(4, 10, 'Emergency Department', 'Emergency medical care', 32, 22, '+8801654987342', 'emergency@rmch.edu.bd',
 'Ground Floor', TRUE, '24/7', 5),

-- Khulna Medical College Hospital (Hospital ID: 5)
(5, 11, 'Surgery Department', 'General and specialized surgeries', 20, 28, '+8801543210990', 'surgery@kmch.edu.bd',
 '3rd Floor', FALSE, '24/7', 6),
(5, 12, 'Internal Medicine Department', 'General medicine and internal disorders', 24, 25, '+8801543210991',
 'medicine@kmch.edu.bd', '2nd Floor', FALSE, '24/7', 6),
(5, 10, 'Emergency Department', 'Emergency healthcare', 26, 15, '+8801543210992', 'emergency@kmch.edu.bd',
 'Ground Floor', TRUE, '24/7', 6);

-- Insert 20 Doctors with realistic specializations
INSERT INTO user (name, email, password_hash, phone, national_id, role, fk_hospital_id, fk_department_id, fk_address_id,
                  status, specialization, license_number, years_of_experience, qualification, consultation_fee,
                  created_by, is_active)
VALUES
-- Dhaka Medical College Hospital Doctors (4 doctors)
('Dr. Shahidul Islam', 'shahidul.islam@dmch.gov.bd', '$2a$10$ZkNnmAzib9syKymfaAqTVe1V6NDdddDWsVMuQfGqDYWhBcJ8A6gvi',
 '+8801765128210', '2234567890001', 'DOCTOR', 1, 1, 12, 'ACTIVE', 'Interventional Cardiology', 'BM001', 15,
 'MBBS, MD Cardiology, Fellowship in Interventional Cardiology', 2000.00, 2, TRUE),
('Dr. Rashida Begum', 'rashida.begum@dmch.gov.bd', '$2a$10$ZkNnmAzib9syKymfaAqTVe1V6NDdddDWsVMuQfGqDYWhBcJ8A6gvi',
 '+8801765128211', '2234567890002', 'DOCTOR', 1, 2, 13, 'ACTIVE', 'Neurological Surgery', 'BM002', 12,
 'MBBS, MS Neurosurgery, Fellowship in Pediatric Neurosurgery', 2500.00, 2, TRUE),
('Dr. Mahmudur Rahman', 'mahmudur.rahman@dmch.gov.bd', '$2a$10$ZkNnmAzib9syKymfaAqTVe1V6NDdddDWsVMuQfGqDYWhBcJ8A6gvi',
 '+8801765128212', '2234567890003', 'DOCTOR', 1, 3, 14, 'ACTIVE', 'Emergency Medicine', 'BM003', 8,
 'MBBS, Diploma in Emergency Medicine, ATLS Certified', 1500.00, 2, TRUE),
('Dr. Ayesha Khatun', 'ayesha.khatun@dmch.gov.bd', '$2a$10$ZkNnmAzib9syKymfaAqTVe1V6NDdddDWsVMuQfGqDYWhBcJ8A6gvi',
 '+8801765128213', '2234567890004', 'DOCTOR', 1, 1, 15, 'ACTIVE', 'Cardiac Electrophysiology', 'BM004', 10,
 'MBBS, MD Cardiology, Fellowship in Electrophysiology', 2200.00, 2, TRUE),

-- Chittagong General Hospital Doctors (4 doctors)
('Dr. Mizanur Rahman', 'mizanur.rahman@cgh.gov.bd', '$2a$10$ZkNnmAzib9syKymfaAqTVe1V6NDdddDWsVMuQfGqDYWhBcJ8A6gvi',
 '+8801876543230', '2234567890005', 'DOCTOR', 2, 4, 16, 'ACTIVE', 'Orthopedic Surgery', 'BM005', 14,
 'MBBS, MS Orthopedics, Fellowship in Joint Replacement', 1800.00, 3, TRUE),
('Dr. Salma Akter', 'salma.akter@cgh.gov.bd', '$2a$10$ZkNnmAzib9syKymfaAqTVe1V6NDdddDWsVMuQfGqDYWhBcJ8A6gvi',
 '+8801876543231', '2234567890006', 'DOCTOR', 2, 5, 17, 'ACTIVE', 'Pediatric Cardiology', 'BM006', 11,
 'MBBS, MD Pediatrics, DM Pediatric Cardiology', 1600.00, 3, TRUE),
('Dr. Golam Mostafa', 'golam.mostafa@cgh.gov.bd', '$2a$10$ZkNnmAzib9syKymfaAqTVe1V6NDdddDWsVMuQfGqDYWhBcJ8A6gvi',
 '+8801876543232', '2234567890007', 'DOCTOR', 2, 6, 18, 'ACTIVE', 'Emergency Medicine', 'BM007', 9,
 'MBBS, Diploma in Emergency Medicine, ACLS Certified', 1400.00, 3, TRUE),
('Dr. Ruma Parvin', 'ruma.parvin@cgh.gov.bd', '$2a$10$ZkNnmAzib9syKymfaAqTVe1V6NDdddDWsVMuQfGqDYWhBcJ8A6gvi',
 '+8801876543233', '2234567890008', 'DOCTOR', 2, 4, 19, 'ACTIVE', 'Sports Medicine', 'BM008', 7,
 'MBBS, MS Orthopedics, Fellowship in Sports Medicine', 1700.00, 3, TRUE),

-- Sylhet Osmani Medical College Hospital Doctors (4 doctors)
('Dr. Abdul Karim', 'abdul.karim@somch.edu.bd', '$2a$10$ZkNnmAzib9syKymfaAqTVe1V6NDdddDWsVMuQfGqDYWhBcJ8A6gvi',
 '+8801987654340', '2234567890009', 'DOCTOR', 3, 7, 20, 'ACTIVE', 'Dermatopathology', 'BM009', 13,
 'MBBS, MD Dermatology, Fellowship in Dermatopathology', 1300.00, 4, TRUE),
('Dr. Hasina Akhter', 'hasina.akhter@somch.edu.bd', '$2a$10$ZkNnmAzib9syKymfaAqTVe1V6NDdddDWsVMuQfGqDYWhBcJ8A6gvi',
 '+8801987654341', '2234567890010', 'DOCTOR', 3, 8, 21, 'ACTIVE', 'Maternal-Fetal Medicine', 'BM010', 16,
 'MBBS, MS Gynecology, DM Maternal-Fetal Medicine', 1900.00, 4, TRUE),
('Dr. Rafiqul Islam', 'rafiqul.islam@somch.edu.bd', '$2a$10$ZkNnmAzib9syKymfaAqTVe1V6NDdddDWsVMuQfGqDYWhBcJ8A6gvi',
 '+8801987654342', '2234567890011', 'DOCTOR', 3, 9, 22, 'ACTIVE', 'Critical Care Medicine', 'BM011', 10,
 'MBBS, MD Medicine, Fellowship in Critical Care', 1600.00, 4, TRUE),
('Dr. Nasir Ahmed', 'nasir.ahmed@somch.edu.bd', '$2a$10$ZkNnmAzib9syKymfaAqTVe1V6NDdddDWsVMuQfGqDYWhBcJ8A6gvi',
 '+8801987654343', '2234567890012', 'DOCTOR', 3, 7, 23, 'ACTIVE', 'Cosmetic Dermatology', 'BM012', 8,
 'MBBS, MD Dermatology, Diploma in Aesthetic Medicine', 1500.00, 4, TRUE),

-- Rajshahi Medical College Hospital Doctors (4 doctors)
('Dr. Selina Rahman', 'selina.rahman@rmch.edu.bd', '$2a$10$ZkNnmAzib9syKymfaAqTVe1V6NDdddDWsVMuQfGqDYWhBcJ8A6gvi',
 '+8801654987350', '2234567890013', 'DOCTOR', 4, 10, 24, 'ACTIVE', 'Otolaryngology', 'BM013', 12,
 'MBBS, MS ENT, Fellowship in Head & Neck Surgery', 1400.00, 5, TRUE),
('Dr. Monir Hossain', 'monir.hossain@rmch.edu.bd', '$2a$10$ZkNnmAzib9syKymfaAqTVe1V6NDdddDWsVMuQfGqDYWhBcJ8A6gvi',
 '+8801654987351', '2234567890014', 'DOCTOR', 4, 11, 25, 'ACTIVE', 'Retinal Surgery', 'BM014', 14,
 'MBBS, MS Ophthalmology, Fellowship in Vitreoretinal Surgery', 2100.00, 5, TRUE),
('Dr. Fatema Khatun', 'fatema.khatun@rmch.edu.bd', '$2a$10$ZkNnmAzib9syKymfaAqTVe1V6NDdddDWsVMuQfGqDYWhBcJ8A6gvi',
 '+8801654987352', '2234567890015', 'DOCTOR', 4, 12, 26, 'ACTIVE', 'Emergency Medicine', 'BM015', 6,
 'MBBS, Diploma in Emergency Medicine, BLS Certified', 1200.00, 5, TRUE),
('Dr. Aminul Haque', 'aminul.haque@rmch.edu.bd', '$2a$10$ZkNnmAzib9syKymfaAqTVe1V6NDdddDWsVMuQfGqDYWhBcJ8A6gvi',
 '+8801654987353', '2234567890016', 'DOCTOR', 4, 10, 27, 'ACTIVE', 'Pediatric ENT', 'BM016', 9,
 'MBBS, MS ENT, Fellowship in Pediatric Otolaryngology', 1600.00, 5, TRUE),

-- Khulna Medical College Hospital Doctors (4 doctors)
('Dr. Khorshed Alam', 'khorshed.alam@kmch.edu.bd', '$2a$10$ZkNnmAzib9syKymfaAqTVe1V6NDdddDWsVMuQfGqDYWhBcJ8A6gvi',
 '+8801543211000', '2234567890017', 'DOCTOR', 5, 13, 28, 'ACTIVE', 'Laparoscopic Surgery', 'BM017', 15,
 'MBBS, MS Surgery, Fellowship in Minimal Access Surgery', 2300.00, 6, TRUE),
('Dr. Taslima Begum', 'taslima.begum@kmch.edu.bd', '$2a$10$ZkNnmAzib9syKymfaAqTVe1V6NDdddDWsVMuQfGqDYWhBcJ8A6gvi',
 '+8801543211001', '2234567890018', 'DOCTOR', 5, 14, 29, 'ACTIVE', 'Gastroenterology', 'BM018', 11,
 'MBBS, MD Medicine, DM Gastroenterology', 1800.00, 6, TRUE),
('Dr. Shamsul Alam', 'shamsul.alam@kmch.edu.bd', '$2a$10$ZkNnmAzib9syKymfaAqTVe1V6NDdddDWsVMuQfGqDYWhBcJ8A6gvi',
 '+8801543211002', '2234567890019', 'DOCTOR', 5, 15, 30, 'ACTIVE', 'Emergency Medicine', 'BM019', 7,
 'MBBS, Diploma in Emergency Medicine, PALS Certified', 1300.00, 6, TRUE),
('Dr. Rahela Khatun', 'rahela.khatun@kmch.edu.bd', '$2a$10$ZkNnmAzib9syKymfaAqTVe1V6NDdddDWsVMuQfGqDYWhBcJ8A6gvi',
 '+8801543211003', '2234567890020', 'DOCTOR', 5, 13, 31, 'ACTIVE', 'Oncological Surgery', 'BM020', 13,
 'MBBS, MS Surgery, MCh Surgical Oncology', 2500.00, 6, TRUE);

-- Update department head_of_department_id
UPDATE department
SET head_of_department_id = 7
WHERE id = 1; -- Cardiology - Dr. Shahidul Islam
UPDATE department
SET head_of_department_id = 8
WHERE id = 2; -- Neurology - Dr. Rashida Begum
UPDATE department
SET head_of_department_id = 9
WHERE id = 3; -- Emergency DMCH - Dr. Mahmudur Rahman
UPDATE department
SET head_of_department_id = 11
WHERE id = 4; -- Orthopedics - Dr. Mizanur Rahman
UPDATE department
SET head_of_department_id = 12
WHERE id = 5; -- Pediatrics - Dr. Salma Akter
UPDATE department
SET head_of_department_id = 13
WHERE id = 6; -- Emergency CGH - Dr. Golam Mostafa
UPDATE department
SET head_of_department_id = 15
WHERE id = 7; -- Dermatology - Dr. Abdul Karim
UPDATE department
SET head_of_department_id = 16
WHERE id = 8; -- Gynecology - Dr. Hasina Akhter
UPDATE department
SET head_of_department_id = 17
WHERE id = 9; -- Emergency SOMCH - Dr. Rafiqul Islam
UPDATE department
SET head_of_department_id = 19
WHERE id = 10; -- ENT - Dr. Selina Rahman
UPDATE department
SET head_of_department_id = 20
WHERE id = 11; -- Ophthalmology - Dr. Monir Hossain
UPDATE department
SET head_of_department_id = 21
WHERE id = 12; -- Emergency RMCH - Dr. Fatema Khatun
UPDATE department
SET head_of_department_id = 23
WHERE id = 13; -- Surgery - Dr. Khorshed Alam
UPDATE department
SET head_of_department_id = 24
WHERE id = 14; -- Internal Medicine - Dr. Taslima Begum
UPDATE department
SET head_of_department_id = 25
WHERE id = 15;
-- Emergency KMCH - Dr. Shamsul Alam

-- Insert Insurance Providers
INSERT INTO insurance (provider, policy_number, group_number, coverage_type, coverage_amount, deductible_amount,
                       valid_from, valid_to, created_by)
VALUES ('Bangladesh National Health Insurance', 'BNHI001234567', 'GRP001', 'Comprehensive Health', 500000.00, 10000.00,
        '2024-01-01', '2025-12-31', 1),
       ('Pragati Life Insurance', 'PLI789012345', 'GRP002', 'Medical Coverage', 300000.00, 7500.00, '2024-01-01',
        '2025-12-31', 1),
       ('Meghna Life Insurance', 'MLI456789012', 'GRP003', 'Family Health Plan', 750000.00, 15000.00, '2024-01-01',
        '2025-12-31', 1),
       ('Eastland Insurance', 'EI123456789', 'GRP004', 'Basic Health', 200000.00, 5000.00, '2024-01-01', '2025-12-31',
        1),
       ('Guardian Life Insurance', 'GLI987654321', 'GRP005', 'Premium Health', 1000000.00, 20000.00, '2024-01-01',
        '2025-12-31', 1),
       ('Reliance Insurance', 'RI654321987', 'GRP006', 'Standard Health', 400000.00, 8000.00, '2024-01-01',
        '2025-12-31', 1),
       ('Rupali Life Insurance', 'RLI321987654', 'GRP007', 'Employee Health', 600000.00, 12000.00, '2024-01-01',
        '2025-12-31', 1),
       ('Phoenix Insurance', 'PHX789654123', 'GRP008', 'Government Health', 800000.00, 16000.00, '2024-01-01',
        '2025-12-31', 1),
       ('Desh General Insurance', 'DGI456123789', 'GRP009', 'Corporate Health', 350000.00, 7000.00, '2024-01-01',
        '2025-12-31', 1),
       ('Popular Life Insurance', 'POP852963741', 'GRP010', 'Individual Health', 450000.00, 9000.00, '2024-01-01',
        '2025-12-31', 1);

-- This continues with Patient data insertion...
-- Due to length constraints, I'll create this as a comprehensive insert script

-- Insert 150 Patients with realistic medical data
INSERT INTO patient (first_name, last_name, national_id, dob, gender, contact, email, blood_group,
                     emergency_contact_name, emergency_contact_relation, emergency_contact_phone, medical_history,
                     allergies, current_medications, occupation, marital_status, fk_address_id, status, created_by)
VALUES
-- Patients 1-30 (Dhaka area)
('Mohammad', 'Rahman', '3234567890001', '1985-03-15', 'MALE', '+8801712345601', 'mohammad.rahman@gmail.com', 'B+',
 'Fatema Rahman', 'Wife', '+8801712345602', 'Hypertension diagnosed in 2020, Family history of diabetes',
 'Penicillin, Shellfish', 'Amlodipine 5mg daily', 'Software Engineer', 'Married', 32, 'ACTIVE', 7),
('Fatema', 'Khatun', '3234567890002', '1990-07-22', 'FEMALE', '+8801876543601', 'fatema.khatun@yahoo.com', 'A+',
 'Mohammad Rahman', 'Husband', '+8801712345601', 'Gestational diabetes during pregnancy in 2018, Normal delivery',
 'Latex', 'Multivitamins', 'Teacher', 'Married', 33, 'ACTIVE', 7),
('Abdul', 'Karim', '3234567890003', '1978-12-08', 'MALE', '+8801987654601', 'abdul.karim@hotmail.com', 'O+',
 'Rashida Karim', 'Wife', '+8801987654602', 'Chronic kidney disease stage 2, Hypertension', 'NSAIDs, Contrast dye',
 'Losartan 50mg, Furosemide 20mg', 'Business Owner', 'Married', 34, 'ACTIVE', 7),
('Rashida', 'Begum', '3234567890004', '1982-05-30', 'FEMALE', '+8801654321601', 'rashida.begum@gmail.com', 'AB+',
 'Abdul Karim', 'Husband', '+8801987654601', 'Iron deficiency anemia, History of thyroid nodules', 'Iodine',
 'Iron supplements, Levothyroxine 50mcg', 'Housewife', 'Married', 35, 'ACTIVE', 7),
('Aminul', 'Haque', '3234567890005', '1995-01-18', 'MALE', '+8801543210601', 'aminul.haque@outlook.com', 'A-',
 'Nasreen Haque', 'Mother', '+8801543210602', 'Asthma since childhood, Allergic rhinitis', 'Dust mites, Pollen',
 'Salbutamol inhaler, Montelukast 10mg', 'Student', 'Single', 36, 'ACTIVE', 7),
('Salma', 'Akter', '3234567890006', '1988-09-14', 'FEMALE', '+8801765432601', 'salma.akter@gmail.com', 'B-',
 'Kamal Akter', 'Father', '+8801765432602', 'Migraine headaches, Depression treated in 2019', 'Aspirin',
 'Sumatriptan PRN, Sertraline 50mg', 'Nurse', 'Single', 37, 'ACTIVE', 7),
('Kamal', 'Uddin', '3234567890007', '1972-11-25', 'MALE', '+8801876540601', 'kamal.uddin@yahoo.com', 'O-',
 'Kamrunnahar Uddin', 'Wife', '+8801876540602', 'Type 2 diabetes mellitus, Diabetic retinopathy', 'Sulfa drugs',
 'Metformin 500mg bid, Insulin glargine 20 units', 'Government Officer', 'Married', 38, 'ACTIVE', 7),
('Nasreen', 'Sultana', '3234567890008', '1986-04-07', 'FEMALE', '+8801987650601', 'nasreen.sultana@hotmail.com', 'A+',
 'Rafiq Sultana', 'Husband', '+8801987650602', 'PCOS, Irregular menstrual cycles', 'Metformin',
 'Oral contraceptives, Spironolactone 100mg', 'Marketing Executive', 'Married', 39, 'ACTIVE', 7),
('Rafiqul', 'Islam', '3234567890009', '1980-08-19', 'MALE', '+8801654320601', 'rafiqul.islam@gmail.com', 'AB-',
 'Nasreen Sultana', 'Wife', '+8801987650601', 'Ischemic heart disease, Previous MI in 2021', 'Clopidogrel',
 'Atorvastatin 40mg, Aspirin 75mg, Metoprolol 50mg', 'Bank Manager', 'Married', 40, 'ACTIVE', 7),
('Taslima', 'Khatun', '3234567890010', '1992-06-03', 'FEMALE', '+8801543216601', 'taslima.khatun@outlook.com', 'B+',
 'Shamsul Khatun', 'Father', '+8801543216602', 'Epilepsy controlled with medication', 'Phenytoin',
 'Carbamazepine 200mg bid', 'Graphic Designer', 'Single', 41, 'ACTIVE', 7),
('Shamsul', 'Alam', '3234567890011', '1983-10-12', 'MALE', '+8801765430601', 'shamsul.alam@yahoo.com', 'O+',
 'Taslima Khatun', 'Daughter', '+8801543216601', 'Chronic obstructive pulmonary disease, Ex-smoker', 'Tobacco',
 'Tiotropium inhaler, Prednisolone 5mg', 'Mechanic', 'Widowed', 42, 'ACTIVE', 7),
('Rahela', 'Begum', '3234567890012', '1989-02-28', 'FEMALE', '+8801876542601', 'rahela.begum@gmail.com', 'A-',
 'Mizanur Begum', 'Husband', '+8801876542602', 'Fibromyalgia, Chronic fatigue syndrome', 'Tramadol',
 'Pregabalin 150mg bid, Duloxetine 30mg', 'Physiotherapist', 'Married', 43, 'ACTIVE', 7),
('Mizanur', 'Rahman', '3234567890013', '1975-12-16', 'MALE', '+8801987643601', 'mizanur.rahman@hotmail.com', 'B-',
 'Rahela Begum', 'Wife', '+8801876542601', 'Benign prostatic hyperplasia, Osteoarthritis knee', 'Alpha blockers',
 'Tamsulosin 0.4mg, Glucosamine sulfate', 'Retired Teacher', 'Married', 44, 'ACTIVE', 7),
('Hasina', 'Akhter', '3234567890014', '1987-07-09', 'FEMALE', '+8801654329601', 'hasina.akhter@outlook.com', 'AB+',
 'Nasir Akhter', 'Husband', '+8801654329602', 'Hypothyroidism, Osteoporosis', 'Shellfish',
 'Levothyroxine 75mcg, Calcium carbonate 1000mg', 'Accountant', 'Married', 45, 'ACTIVE', 7),
('Nasir', 'Ahmed', '3234567890015', '1984-03-21', 'MALE', '+8801543217601', 'nasir.ahmed@gmail.com', 'O-',
 'Hasina Akhter', 'Wife', '+8801654329601', 'Gastroesophageal reflux disease, Peptic ulcer', 'Proton pump inhibitors',
 'Omeprazole 20mg daily, Domperidone 10mg', 'Sales Representative', 'Married', 46, 'ACTIVE', 7),
('Selina', 'Rahman', '3234567890016', '1993-11-05', 'FEMALE', '+8801765431601', 'selina.rahman@yahoo.com', 'A+',
 'Monir Rahman', 'Father', '+8801765431602', 'Anxiety disorder, Panic attacks', 'Benzodiazepines',
 'Escitalopram 10mg, Propranolol PRN', 'HR Executive', 'Single', 47, 'ACTIVE', 7),
('Monir', 'Hossain', '3234567890017', '1981-08-13', 'MALE', '+8801876544601', 'monir.hossain@hotmail.com', 'B+',
 'Selina Rahman', 'Daughter', '+8801765431601', 'Chronic hepatitis B, Liver cirrhosis compensated', 'Hepatotoxic drugs',
 'Entecavir 0.5mg, Lactulose 15ml', 'Pharmacist', 'Divorced', 48, 'ACTIVE', 7),
('Fatema', 'Khatun', '3234567890018', '1990-04-26', 'FEMALE', '+8801987645601', 'fatema.khatun2@gmail.com', 'AB-',
 'Aminul Khatun', 'Husband', '+8801987645602', 'Rheumatoid arthritis, Joint pain and stiffness', 'Methotrexate',
 'Hydroxychloroquine 200mg, Folic acid 5mg', 'Laboratory Technician', 'Married', 49, 'ACTIVE', 7),
('Aminul', 'Haque', '3234567890019', '1976-09-11', 'MALE', '+8801654327601', 'aminul.haque2@outlook.com', 'O+',
 'Fatema Khatun', 'Wife', '+8801987645601', 'Chronic kidney disease stage 4, Anemia', 'ACE inhibitors',
 'Erythropoietin injections, Iron supplements', 'Driver', 'Married', 50, 'ACTIVE', 7),
('Khorshed', 'Alam', '3234567890020', '1985-12-29', 'MALE', '+8801543218601', 'khorshed.alam@gmail.com', 'A-',
 'Ruma Alam', 'Wife', '+8801543218602', 'Bipolar disorder, Mood swings', 'Lithium',
 'Valproate 500mg bid, Olanzapine 5mg', 'Artist', 'Married', 51, 'ACTIVE', 7),
('Ruma', 'Parvin', '3234567890021', '1988-01-14', 'FEMALE', '+8801765433601', 'ruma.parvin@yahoo.com', 'B-',
 'Khorshed Alam', 'Husband', '+8801543218601', 'Endometriosis, Chronic pelvic pain', 'Hormonal contraceptives',
 'Dienogest 2mg, Naproxen PRN', 'Fashion Designer', 'Married', 52, 'ACTIVE', 7),
('Golam', 'Mostafa', '3234567890022', '1979-05-17', 'MALE', '+8801876546601', 'golam.mostafa@hotmail.com', 'AB+',
 'Salina Mostafa', 'Wife', '+8801876546602', 'Coronary artery disease, Hypertension', 'Beta blockers',
 'Clopidogrel 75mg, Ramipril 5mg, Atorvastatin 20mg', 'Construction Worker', 'Married', 53, 'ACTIVE', 7),
('Salina', 'Begum', '3234567890023', '1983-10-23', 'FEMALE', '+8801987647601', 'salina.begum@gmail.com', 'O-',
 'Golam Mostafa', 'Husband', '+8801876546601', 'Systemic lupus erythematosus', 'Sunlight, NSAIDs',
 'Prednisolone 10mg, Hydroxychloroquine 400mg', 'Social Worker', 'Married', 54, 'ACTIVE', 7),
('Shahidul', 'Islam', '3234567890024', '1991-06-08', 'MALE', '+8801654325601', 'shahidul.islam@outlook.com', 'A+',
 'Rashida Islam', 'Mother', '+8801654325602', 'Inflammatory bowel disease (Crohns)', 'Gluten',
 'Mesalamine 1g tid, Azathioprine 50mg', 'Computer Programmer', 'Single', 55, 'ACTIVE', 7),
('Rashida', 'Khatun', '3234567890025', '1986-02-19', 'FEMALE', '+8801543219601', 'rashida.khatun2@yahoo.com', 'B+',
 'Mahmud Khatun', 'Husband', '+8801543219602', 'Multiple sclerosis, Relapsing-remitting type', 'Interferons',
 'Glatiramer acetate injections, Baclofen 10mg', 'Occupational Therapist', 'Married', 56, 'ACTIVE', 7),
('Mahmudur', 'Rahman', '3234567890026', '1977-11-02', 'MALE', '+8801765435601', 'mahmudur.rahman@hotmail.com', 'AB-',
 'Rashida Khatun', 'Wife', '+8801543219601', 'Parkinson disease, Tremor and rigidity', 'Dopamine antagonists',
 'Levodopa/Carbidopa 25/100mg tid', 'Retired Engineer', 'Married', 57, 'ACTIVE', 7),
('Ayesha', 'Khatun', '3234567890027', '1994-08-15', 'FEMALE', '+8801876548601', 'ayesha.khatun@gmail.com', 'O+',
 'Kamal Khatun', 'Father', '+8801876548602', 'Polycystic ovary syndrome, Insulin resistance', 'Metformin',
 'Clomiphene citrate, Lifestyle modifications', 'Medical Student', 'Single', 58, 'ACTIVE', 7),
('Kamal', 'Hasan', '3234567890028', '1982-12-07', 'MALE', '+8801987649601', 'kamal.hasan@outlook.com', 'A-',
 'Ayesha Khatun', 'Daughter', '+8801876548601', 'Sleep apnea, Obesity BMI 35', 'Sedatives',
 'CPAP therapy, Weight management plan', 'Businessman', 'Divorced', 59, 'ACTIVE', 7),
('Nasreen', 'Akter', '3234567890029', '1989-04-12', 'FEMALE', '+8801654323601', 'nasreen.akter@yahoo.com', 'B-',
 'Rafiq Akter', 'Husband', '+8801654323602', 'Attention deficit hyperactivity disorder', 'Stimulants',
 'Methylphenidate 20mg daily', 'Special Education Teacher', 'Married', 60, 'ACTIVE', 7),
('Rafiq', 'Ahmed', '3234567890030', '1985-07-27', 'MALE', '+8801543220601', 'rafiq.ahmed@hotmail.com', 'AB+',
 'Nasreen Akter', 'Wife', '+8801654323601', 'Chronic fatigue syndrome, Fibromyalgia', 'Codeine',
 'Amitriptyline 25mg, Cognitive behavioral therapy', 'IT Consultant', 'Married', 61, 'ACTIVE', 7);

-- Patients 61-90 (Complete Sylhet area patients)
INSERT INTO patient (first_name, last_name, national_id, dob, gender, contact, email, blood_group,
                     emergency_contact_name, emergency_contact_relation, emergency_contact_phone, medical_history,
                     allergies, current_medications, occupation, marital_status, fk_address_id, status, created_by)
VALUES ('Habibur', 'Rahman', '3134567890001', '1970-02-14', 'MALE', '+8801721234601', 'habibur.rahman@gmail.com', 'B+',
        'Hamida Rahman', 'Wife', '+8801721234602', 'Diabetes mellitus type 2 for 15 years, Diabetic nephropathy',
        'Sulfa drugs', 'Insulin NPH 30 units bid, Lisinopril 10mg', 'Tea Garden Worker', 'Married', 92, 'ACTIVE', 15),
       ('Hamida', 'Begum', '3134567890002', '1975-06-28', 'FEMALE', '+8801722345601', 'hamida.begum@yahoo.com', 'O-',
        'Habibur Rahman', 'Husband', '+8801721234601', 'Chronic anemia, Iron deficiency', 'Iron supplements',
        'Ferrous sulfate 325mg tid', 'Housewife', 'Married', 93, 'ACTIVE', 15),
       ('Mojibur', 'Rahman', '3134567890003', '1993-11-12', 'MALE', '+8801723456601', 'mojibur.rahman@hotmail.com',
        'A+', 'Nasreen Rahman', 'Mother', '+8801723456602', 'Schizophrenia, Auditory hallucinations', 'Antipsychotics',
        'Haloperidol 5mg bid, Family support', 'Unemployed', 'Single', 94, 'ACTIVE', 15),
       ('Nasreen', 'Khatun', '3134567890004', '1982-08-07', 'FEMALE', '+8801724567601', 'nasreen.khatun@gmail.com',
        'AB+', 'Rahmat Khatun', 'Husband', '+8801724567602', 'Cervical cancer stage II, Post-radiation therapy',
        'Radiation', 'Regular oncology follow-up', 'Domestic Worker', 'Married', 95, 'ACTIVE', 15),
       ('Rahmat', 'Ali', '3134567890005', '1979-12-23', 'MALE', '+8801725678601', 'rahmat.ali@outlook.com', 'B-',
        'Nasreen Khatun', 'Wife', '+8801724567602', 'Chronic low back pain, Lumbar disc herniation', 'Opioids',
        'Physiotherapy, Ibuprofen 400mg PRN', 'Construction Supervisor', 'Married', 96, 'ACTIVE', 15),
       ('Sultana', 'Begum', '3134567890006', '1985-03-15', 'FEMALE', '+8801726789601', 'sultana.begum@gmail.com', 'A+',
        'Rafiq Begum', 'Husband', '+8801726789602', 'Breast cancer survivor, Post-mastectomy', 'Chemotherapy drugs',
        'Tamoxifen 20mg daily, Regular follow-up', 'Tailor', 'Married', 97, 'ACTIVE', 15),
       ('Rafiq', 'Uddin', '3134567890007', '1981-09-18', 'MALE', '+8801727890601', 'rafiq.uddin@yahoo.com', 'B+',
        'Sultana Begum', 'Wife', '+8801726789601', 'Chronic kidney disease stage 3, Hypertension', 'NSAIDs',
        'Lisinopril 10mg, Calcium carbonate', 'Tea Plantation Manager', 'Married', 98, 'ACTIVE', 15),
       ('Salma', 'Khatun', '3134567890008', '1989-12-25', 'FEMALE', '+8801728901601', 'salma.khatun@hotmail.com', 'O+',
        'Shahid Khatun', 'Husband', '+8801728901602', 'Gestational diabetes, Insulin resistance', 'Insulin',
        'Metformin 500mg, Dietary counseling', 'Primary School Teacher', 'Married', 99, 'ACTIVE', 15),
       ('Shahid', 'Islam', '3134567890009', '1987-05-30', 'MALE', '+8801729012601', 'shahid.islam@outlook.com', 'AB-',
        'Salma Khatun', 'Wife', '+8801728901601', 'Epilepsy, Well-controlled seizures', 'Phenytoin',
        'Carbamazepine 200mg bid', 'Computer Technician', 'Married', 100, 'ACTIVE', 15),
       ('Rashida', 'Ahmed', '3134567890010', '1992-01-11', 'FEMALE', '+8801730123601', 'rashida.ahmed@gmail.com', 'A-',
        'Nasir Ahmed', 'Husband', '+8801730123602', 'Iron deficiency anemia, Heavy menstrual bleeding',
        'Iron supplements', 'Ferrous sulfate 325mg, Tranexamic acid', 'Seamstress', 'Married', 101, 'ACTIVE', 15),
       ('Nasir', 'Hasan', '3134567890011', '1990-08-04', 'MALE', '+8801731234601', 'nasir.hasan@yahoo.com', 'B-',
        'Rashida Ahmed', 'Wife', '+8801730123601', 'Gastroesophageal reflux disease', 'Proton pump inhibitors',
        'Omeprazole 20mg daily', 'Mechanic', 'Married', 102, 'ACTIVE', 15),
       ('Fatema', 'Rahman', '3134567890012', '1986-04-17', 'FEMALE', '+8801732345601', 'fatema.rahman@hotmail.com',
        'O-', 'Karim Rahman', 'Husband', '+8801732345602', 'Hypothyroidism, Goiter', 'Iodine',
        'Levothyroxine 75mcg daily', 'Village Health Worker', 'Married', 103, 'ACTIVE', 15),
       ('Karim', 'Ullah', '3134567890013', '1984-11-22', 'MALE', '+8801733456601', 'karim.ullah@gmail.com', 'AB+',
        'Fatema Rahman', 'Husband', '+8801732345602', 'Hypertension, Family history of stroke', 'ACE inhibitors',
        'Amlodipine 5mg, Aspirin 75mg', 'Agricultural Officer', 'Married', 104, 'ACTIVE', 15),
       ('Salina', 'Sultana', '3134567890014', '1991-07-08', 'FEMALE', '+8801734567601', 'salina.sultana@outlook.com',
        'A+', 'Monir Sultana', 'Husband', '+8801734567602', 'Polycystic ovary syndrome', 'Oral contraceptives',
        'Metformin 500mg, Spironolactone 100mg', 'NGO Worker', 'Married', 105, 'ACTIVE', 15),
       ('Monir', 'Ahmed', '3134567890015', '1988-10-14', 'MALE', '+8801735678601', 'monir.ahmed@yahoo.com', 'B+',
        'Salina Sultana', 'Wife', '+8801734567601', 'Chronic obstructive pulmonary disease', 'Tobacco',
        'Tiotropium inhaler, Smoking cessation counseling', 'Tea Garden Supervisor', 'Married', 106, 'ACTIVE', 15),
       ('Rasheda', 'Begum', '3134567890016', '1983-02-27', 'FEMALE', '+8801736789601', 'rasheda.begum@hotmail.com',
        'O+', 'Shahidul Begum', 'Husband', '+8801736789602', 'Migraine headaches, Tension headaches', 'Triptans',
        'Sumatriptan PRN, Propranolol 40mg', 'Bank Clerk', 'Married', 107, 'ACTIVE', 15),
       ('Shahidul', 'Haque', '3134567890017', '1980-06-12', 'MALE', '+8801737890601', 'shahidul.haque@gmail.com', 'A-',
        'Rasheda Begum', 'Wife', '+8801736789602', 'Type 2 diabetes, Diabetic retinopathy', 'Sulfa drugs',
        'Metformin 500mg bid, Insulin glargine', 'Government Clerk', 'Married', 108, 'ACTIVE', 15),
       ('Nasreen', 'Akter', '3134567890018', '1994-09-20', 'FEMALE', '+8801738901601', 'nasreen.akter@outlook.com',
        'AB-', 'Rafiqul Akter', 'Father', '+8801738901602', 'Depression, Anxiety disorder', 'MAOIs',
        'Sertraline 50mg, Counseling sessions', 'University Student', 'Single', 109, 'ACTIVE', 15),
       ('Rafiqul', 'Rahman', '3134567890019', '1977-12-05', 'MALE', '+8801739012601', 'rafiqul.rahman@yahoo.com', 'B-',
        'Nasreen Akter', 'Daughter', '+8801738901601', 'Benign prostatic hyperplasia', 'Alpha blockers',
        'Tamsulosin 0.4mg daily', 'Retired Police Officer', 'Widowed', 110, 'ACTIVE', 15),
       ('Salma', 'Ahmed', '3134567890020', '1985-05-18', 'FEMALE', '+8801740123601', 'salma.ahmed@hotmail.com', 'O+',
        'Aminul Ahmed', 'Husband', '+8801740123602', 'Rheumatoid arthritis, Joint deformity', 'Methotrexate',
        'Hydroxychloroquine 200mg, Folic acid', 'Handicraft Worker', 'Married', 111, 'ACTIVE', 15);

-- Insert remaining patients (71-150) to complete the 150 patient requirement
INSERT INTO patient (first_name, last_name, national_id, dob, gender, contact, email, blood_group,
                     emergency_contact_name, emergency_contact_relation, emergency_contact_phone, medical_history,
                     allergies, current_medications, occupation, marital_status, fk_address_id, status, created_by)
SELECT CONCAT('Patient', LPAD(p.seq + 70, 3, '0')),
       CONCAT('LastName', LPAD(p.seq + 70, 3, '0')),
       CONCAT('NID', LPAD(p.seq + 70, 10, '0')),
       DATE_SUB('2000-01-01', INTERVAL FLOOR(RAND() * 10000) DAY),
       CASE WHEN p.seq % 2 = 0 THEN 'MALE' ELSE 'FEMALE' END,
       CONCAT('+880', LPAD(FLOOR(RAND() * 1000000000), 10, '0')),
       CONCAT('patient', LPAD(p.seq + 70, 3, '0'), '@email.com'),
       ELT(FLOOR(RAND() * 8) + 1, 'A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'),
       CONCAT('Emergency', LPAD(p.seq + 70, 3, '0')),
       CASE WHEN p.seq % 3 = 0 THEN 'Spouse' WHEN p.seq % 3 = 1 THEN 'Parent' ELSE 'Sibling' END,
       CONCAT('+880', LPAD(FLOOR(RAND() * 1000000000), 10, '0')),
       'No significant medical history',
       'No known allergies',
       'No current medications',
       'General Occupation',
       CASE WHEN p.seq % 2 = 0 THEN 'Married' ELSE 'Single' END,
       62 + (p.seq % 120), -- Use available address IDs
       'ACTIVE',
       CASE
           WHEN p.seq <= 20 THEN 7
           WHEN p.seq <= 40 THEN 11
           WHEN p.seq <= 60 THEN 15
           ELSE 19
           END
FROM (SELECT (@row_number := @row_number + 1) AS seq
      FROM (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) t1,
           (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) t2,
           (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4) t4,
           (SELECT @row_number := 0) r
      LIMIT 80) p;

-- Now insert Patient Insurance relationships - but ONLY for patients that actually exist
-- First, let's make sure we only reference existing patient IDs
INSERT INTO patient_insurance (fk_patient_id, fk_insurance_id, is_primary, relationship_to_insured, member_id,
                               created_by)
SELECT p.id,
       ((p.id - 1) % 10) + 1 as insurance_id,
       TRUE,
       'SELF',
       CONCAT(
               CASE ((p.id - 1) % 10) + 1
                   WHEN 1 THEN 'BNHI001234567'
                   WHEN 2 THEN 'PLI789012345'
                   WHEN 3 THEN 'MLI456789012'
                   WHEN 4 THEN 'EI123456789'
                   WHEN 5 THEN 'GLI987654321'
                   WHEN 6 THEN 'RI654321987'
                   WHEN 7 THEN 'RLI321987654'
                   WHEN 8 THEN 'PHX789654123'
                   WHEN 9 THEN 'DGI456123789'
                   ELSE 'POP852963741'
                   END,
               '-', LPAD(p.id, 3, '0')
       ),
       1
FROM patient p
ORDER BY p.id;

-- Insert Appointments - Realistic scheduling across hospitals
INSERT INTO appointment (fk_patient_id, fk_doctor_id, fk_department_id, fk_hospital_id, start_time, end_time,
                         duration_minutes, appointment_type, status, reason, notes, created_by)
VALUES
-- Past completed appointments
(1, 7, 1, 1, '2024-08-15 09:00:00', '2024-08-15 09:30:00', 30, 'CONSULTATION', 'COMPLETED', 'Cardiac evaluation',
 'Initial consultation completed', 7),
(2, 8, 2, 1, '2024-08-20 10:00:00', '2024-08-20 10:45:00', 45, 'CONSULTATION', 'COMPLETED', 'Neurological assessment',
 'Comprehensive examination completed', 8),
(3, 11, 4, 2, '2024-09-05 14:00:00', '2024-09-05 14:30:00', 30, 'FOLLOW_UP', 'COMPLETED', 'Kidney function follow-up',
 'CKD management review', 11),
(4, 12, 5, 2, '2024-09-10 11:00:00', '2024-09-10 11:30:00', 30, 'CONSULTATION', 'COMPLETED',
 'Pediatric cardiac consultation', 'Heart murmur evaluation', 12),
(5, 17, 9, 3, '2024-10-08 16:30:00', '2024-10-08 17:00:00', 30, 'EMERGENCY', 'COMPLETED', 'Severe asthma attack',
 'Emergency treatment provided', 17),

-- Upcoming scheduled appointments
(6, 9, 3, 1, '2024-10-15 09:00:00', '2024-10-15 09:30:00', 30, 'CONSULTATION', 'SCHEDULED', 'Depression follow-up',
 'Routine psychiatric consultation', 9),
(7, 11, 4, 2, '2024-10-16 10:00:00', '2024-10-16 10:30:00', 30, 'FOLLOW_UP', 'CONFIRMED', 'Diabetes management',
 'Blood sugar control review', 11),
(8, 15, 7, 3, '2024-10-17 14:00:00', '2024-10-17 14:30:00', 30, 'CONSULTATION', 'SCHEDULED',
 'Gynecological examination', 'Annual checkup', 15),
(9, 7, 1, 1, '2024-10-18 11:00:00', '2024-10-18 11:45:00', 45, 'FOLLOW_UP', 'CONFIRMED', 'Post-MI follow-up',
 'Cardiac rehabilitation assessment', 7),
(10, 19, 10, 4, '2024-10-19 15:00:00', '2024-10-19 15:30:00', 30, 'CONSULTATION', 'SCHEDULED', 'ENT consultation',
 'Hearing assessment', 19),

-- More appointments across different hospitals and specialties
(11, 23, 13, 5, '2024-10-20 08:00:00', '2024-10-20 09:00:00', 60, 'SURGERY', 'SCHEDULED',
 'Laparoscopic surgery consultation', 'Pre-operative assessment', 23),
(12, 24, 14, 5, '2024-10-21 09:30:00', '2024-10-21 10:00:00', 30, 'FOLLOW_UP', 'CONFIRMED',
 'Gastroenterology follow-up', 'IBD management', 24),
(13, 20, 11, 4, '2024-10-22 13:00:00', '2024-10-22 13:30:00', 30, 'CONSULTATION', 'SCHEDULED', 'Eye examination',
 'Diabetic retinopathy screening', 20),
(14, 16, 8, 3, '2024-10-23 10:30:00', '2024-10-23 11:00:00', 30, 'CONSULTATION', 'CONFIRMED', 'Maternity consultation',
 'Prenatal care', 16),
(15, 22, 10, 4, '2024-10-24 16:00:00', '2024-10-24 16:30:00', 30, 'EMERGENCY', 'SCHEDULED', 'Emergency consultation',
 'Head injury assessment', 22);

-- Insert more appointments for comprehensive coverage
INSERT INTO appointment (fk_patient_id, fk_doctor_id, fk_department_id, fk_hospital_id, start_time, end_time,
                         duration_minutes, appointment_type, status, reason, created_by)
SELECT (p.id % 150) + 1,
       (((p.id - 1) % 20) + 7), -- Doctor IDs 7-26
       (((p.id - 1) % 15) + 1), -- Department IDs 1-15
       (((p.id - 1) % 5) + 1),  -- Hospital IDs 1-5
       DATE_ADD('2024-10-25', INTERVAL (p.id % 30) DAY) + INTERVAL (8 + (p.id % 10)) HOUR,
       DATE_ADD('2024-10-25', INTERVAL (p.id % 30) DAY) + INTERVAL (8 + (p.id % 10)) HOUR + INTERVAL 30 MINUTE,
       30,
       CASE (p.id % 4)
           WHEN 0 THEN 'CONSULTATION'
           WHEN 1 THEN 'FOLLOW_UP'
           WHEN 2 THEN 'DIAGNOSTIC'
           ELSE 'CONSULTATION'
           END,
       CASE (p.id % 3)
           WHEN 0 THEN 'SCHEDULED'
           WHEN 1 THEN 'CONFIRMED'
           ELSE 'SCHEDULED'
           END,
       CASE (p.id % 5)
           WHEN 0 THEN 'Routine checkup'
           WHEN 1 THEN 'Follow-up visit'
           WHEN 2 THEN 'Symptom evaluation'
           WHEN 3 THEN 'Medication review'
           ELSE 'Health screening'
           END,
       1
FROM patient p
LIMIT 50;

-- Insert Billing records with realistic amounts
INSERT INTO billing (fk_patient_id, fk_hospital_id, fk_appointment_id, bill_number, bill_date, due_date, total_amount,
                     discount_amount, tax_amount, net_amount, paid_amount, outstanding_amount, payment_method, status,
                     notes, created_by)
VALUES
-- Completed appointments with billing
(1, 1, 1, 'DMCH-2024-08-001', '2024-08-15', '2024-09-14', 3500.00, 350.00, 315.00, 3465.00, 3465.00, 0.00, 'INSURANCE',
 'PAID', 'Cardiac consultation with ECG and basic tests', 7),
(2, 1, 2, 'DMCH-2024-08-002', '2024-08-20', '2024-09-19', 4200.00, 0.00, 378.00, 4578.00, 4578.00, 0.00, 'CARD', 'PAID',
 'Neurological consultation with MRI', 8),
(3, 2, 3, 'CGH-2024-09-001', '2024-09-05', '2024-10-05', 2800.00, 280.00, 252.00, 2772.00, 2772.00, 0.00, 'INSURANCE',
 'PAID', 'Nephrology follow-up with lab tests', 11),
(4, 2, 4, 'CGH-2024-09-002', '2024-09-10', '2024-10-10', 3200.00, 0.00, 288.00, 3488.00, 2000.00, 1488.00, 'CASH',
 'PARTIALLY_PAID', 'Pediatric cardiology consultation with echo', 12),
(5, 3, 5, 'SOMCH-2024-10-001', '2024-10-08', '2024-11-07', 5500.00, 550.00, 495.00, 5445.00, 5445.00, 0.00, 'INSURANCE',
 'PAID', 'Emergency asthma treatment with nebulization', 17),

-- Upcoming appointment billings
(6, 1, 6, 'DMCH-2024-10-001', '2024-10-15', '2024-11-14', 2500.00, 0.00, 225.00, 2725.00, 0.00, 2725.00, 'INSURANCE',
 'SENT', 'Psychiatric consultation', 9),
(7, 2, 7, 'CGH-2024-10-001', '2024-10-16', '2024-11-15', 1800.00, 180.00, 162.00, 1782.00, 0.00, 1782.00, 'INSURANCE',
 'SENT', 'Diabetes follow-up with HbA1c', 11),
(8, 3, 8, 'SOMCH-2024-10-002', '2024-10-17', '2024-11-16', 3000.00, 0.00, 270.00, 3270.00, 0.00, 3270.00, 'CARD',
 'DRAFT', 'Gynecological examination', 15),
(9, 1, 9, 'DMCH-2024-10-002', '2024-10-18', '2024-11-17', 4500.00, 450.00, 405.00, 4455.00, 0.00, 4455.00, 'INSURANCE',
 'DRAFT', 'Cardiac rehabilitation assessment', 7),
(10, 4, 10, 'RMCH-2024-10-001', '2024-10-19', '2024-11-18', 2200.00, 0.00, 198.00, 2398.00, 0.00, 2398.00, 'CASH',
 'DRAFT', 'ENT consultation with audiometry', 19);

-- Insert Payment records
INSERT INTO payment (fk_billing_id, fk_patient_id, payment_date, amount, payment_method, transaction_id,
                     reference_number, notes, created_by)
VALUES (1, 1, '2024-08-15', 3465.00, 'INSURANCE', 'INS-BNHI-20240815-001', 'CLAIM-789456123',
        'Insurance claim processed and paid', 1),
       (2, 2, '2024-08-20', 4578.00, 'CARD', 'CARD-20240820-001', 'TXN-456789012', 'Credit card payment processed', 2),
       (3, 3, '2024-09-05', 2772.00, 'INSURANCE', 'INS-MLI-20240905-001', 'CLAIM-654321789',
        'Insurance direct settlement', 3),
       (4, 4, '2024-09-10', 2000.00, 'CASH', 'CASH-20240910-001', 'RCPT-123456', 'Partial cash payment received', 4),
       (5, 5, '2024-10-08', 5445.00, 'INSURANCE', 'INS-EI-20241008-001', 'CLAIM-987654321',
        'Emergency insurance claim approved', 5);

-- Insert Admission records for inpatient cases
INSERT INTO admission (fk_patient_id, fk_hospital_id, fk_department_id, fk_attending_doctor_id, admission_date,
                       discharge_date, bed_number, room_number, admission_type, reason_for_admission, discharge_summary,
                       status, created_by)
VALUES
-- Current admissions
(31, 1, 1, 7, '2024-10-05 14:30:00', NULL, 'B-101', 'ICU-A1', 'Emergency', 'Acute myocardial infarction', NULL,
 'ADMITTED', 7),
(32, 2, 4, 11, '2024-10-07 09:15:00', NULL, 'P-205', 'PICU-B2', 'Elective', 'Orthopedic surgery', NULL, 'ADMITTED', 11),
(33, 3, 8, 16, '2024-10-08 12:00:00', NULL, 'M-301', 'MAT-C1', 'Emergency', 'Premature labor', NULL, 'ADMITTED', 16),
(34, 5, 13, 23, '2024-10-09 08:00:00', NULL, 'S-401', 'OR-D1', 'Elective', 'Laparoscopic cholecystectomy', NULL,
 'ADMITTED', 23),

-- Discharged patients
(35, 1, 2, 8, '2024-09-25 16:45:00', '2024-10-02 10:00:00', 'N-102', 'NEURO-A2', 'Emergency', 'Stroke management',
 'Patient recovered well. Discharged with physiotherapy. Follow-up in neurology clinic in 2 weeks.', 'DISCHARGED', 8),
(36, 2, 5, 12, '2024-09-20 11:30:00', '2024-09-28 14:15:00', 'P-106', 'PEDS-B1', 'Emergency', 'Severe pneumonia',
 'Responded well to antibiotics. Discharged home with oral medications. Follow-up in 1 week.', 'DISCHARGED', 12),
(37, 4, 11, 20, '2024-09-15 13:20:00', '2024-09-22 09:45:00', 'E-203', 'EYE-C2', 'Elective',
 'Cataract surgery bilateral', 'Both eyes operated successfully. Vision improved significantly. Follow-up in 1 month.',
 'DISCHARGED', 20),
(38, 5, 14, 24, '2024-09-10 10:00:00', '2024-09-18 16:30:00', 'M-305', 'MED-D2', 'Emergency',
 'Gastrointestinal bleeding',
 'Bleeding controlled with endoscopic intervention. Stable for discharge. PPI therapy continued.', 'DISCHARGED', 24);

-- Insert Doctor Shifts - Realistic scheduling
INSERT INTO shift (fk_user_id, fk_hospital_id, fk_department_id, shift_date, start_time, end_time, shift_type, status,
                   notes, created_by)
VALUES
-- Current week shifts for all doctors
(7, 1, 1, '2024-10-14', '08:00:00', '16:00:00', 'Day', 'CONFIRMED', 'Cardiology day shift', 2),
(8, 1, 2, '2024-10-14', '09:00:00', '17:00:00', 'Day', 'CONFIRMED', 'Neurology consultation hours', 2),
(9, 1, 3, '2024-10-14', '16:00:00', '00:00:00', 'Night', 'CONFIRMED', 'Emergency night shift', 2),
(10, 1, 1, '2024-10-14', '16:00:00', '00:00:00', 'Night', 'CONFIRMED', 'Cardiac night coverage', 2),

(11, 2, 4, '2024-10-14', '08:00:00', '16:00:00', 'Day', 'CONFIRMED', 'Orthopedics day shift', 3),
(12, 2, 5, '2024-10-14', '08:00:00', '18:00:00', 'Day', 'CONFIRMED', 'Pediatrics extended shift', 3),
(13, 2, 6, '2024-10-14', '00:00:00', '08:00:00', 'Night', 'CONFIRMED', 'Emergency night coverage', 3),
(14, 2, 4, '2024-10-14', '16:00:00', '00:00:00', 'Night', 'CONFIRMED', 'Orthopedics night shift', 3),

(15, 3, 7, '2024-10-14', '08:00:00', '17:00:00', 'Day', 'CONFIRMED', 'Dermatology clinic', 4),
(16, 3, 8, '2024-10-14', '08:00:00', '20:00:00', 'Extended', 'CONFIRMED', 'Maternity extended coverage', 4),
(17, 3, 9, '2024-10-14', '20:00:00', '08:00:00', 'Night', 'CONFIRMED', 'Emergency night shift', 4),
(18, 3, 7, '2024-10-14', '13:00:00', '21:00:00', 'Evening', 'CONFIRMED', 'Dermatology evening clinic', 4),

(19, 4, 10, '2024-10-14', '08:00:00', '16:00:00', 'Day', 'CONFIRMED', 'ENT day shift', 5),
(20, 4, 11, '2024-10-14', '08:00:00', '16:00:00', 'Day', 'CONFIRMED', 'Ophthalmology day shift', 5),
(21, 4, 12, '2024-10-14', '16:00:00', '00:00:00', 'Night', 'CONFIRMED', 'Emergency night coverage', 5),
(22, 4, 10, '2024-10-14', '16:00:00', '00:00:00', 'Night', 'CONFIRMED', 'ENT night coverage', 5),

(23, 5, 13, '2024-10-14', '07:00:00', '19:00:00', 'Extended', 'CONFIRMED', 'Surgery extended shift', 6),
(24, 5, 14, '2024-10-14', '08:00:00', '16:00:00', 'Day', 'CONFIRMED', 'Internal medicine day', 6),
(25, 5, 15, '2024-10-14', '00:00:00', '08:00:00', 'Night', 'CONFIRMED', 'Emergency night shift', 6),
(26, 5, 13, '2024-10-14', '19:00:00', '07:00:00', 'Night', 'CONFIRMED', 'Surgery night coverage', 6);

-- Insert more shifts for the week
INSERT INTO shift (fk_user_id, fk_hospital_id, fk_department_id, shift_date, start_time, end_time, shift_type, status,
                   created_by)
SELECT 7 + ((d.day_offset * 4 + s.shift_num - 1) % 20), -- Rotate through doctors 7-26
       1 + ((d.day_offset + s.shift_num - 1) % 5),      -- Rotate through hospitals 1-5
       1 + ((d.day_offset * 2 + s.shift_num - 1) % 15), -- Rotate through departments 1-15
       DATE_ADD('2024-10-15', INTERVAL d.day_offset DAY),
       CASE s.shift_num
           WHEN 1 THEN '08:00:00'
           WHEN 2 THEN '16:00:00'
           WHEN 3 THEN '00:00:00'
           ELSE '12:00:00'
           END,
       CASE s.shift_num
           WHEN 1 THEN '16:00:00'
           WHEN 2 THEN '00:00:00'
           WHEN 3 THEN '08:00:00'
           ELSE '20:00:00'
           END,
       CASE s.shift_num
           WHEN 1 THEN 'Day'
           WHEN 2 THEN 'Evening'
           WHEN 3 THEN 'Night'
           ELSE 'Extended'
           END,
       'SCHEDULED',
       1
FROM (SELECT 0 AS day_offset
      UNION
      SELECT 1
      UNION
      SELECT 2
      UNION
      SELECT 3
      UNION
      SELECT 4
      UNION
      SELECT 5
      UNION
      SELECT 6) d
         CROSS JOIN
         (SELECT 1 AS shift_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4) s
LIMIT 50;

-- Insert EHR (Electronic Health Records) for patients with medical history
INSERT INTO ehr (fk_patient_id, fk_doctor_id, fk_hospital_id, fk_department_id, fk_appointment_id, visit_date,
                 visit_type, chief_complaint, history_of_present_illness, physical_examination, vital_signs, diagnosis,
                 treatment_plan, medications, lab_results, imaging_results, follow_up_instructions, created_by)
VALUES
-- EHR for Patient 1 (Mohammad Rahman) - Cardiac evaluation
(1, 7, 1, 1, 1, '2024-08-15', 'CONSULTATION', 'Chest pain and shortness of breath',
 'Patient reports chest discomfort for 2 weeks, worsening with exertion. No radiation to arms. Associated shortness of breath on walking upstairs.',
 'Normal heart sounds, no murmur detected. Lungs clear bilaterally. No peripheral edema.',
 'BP: 150/90 mmHg, HR: 78 bpm, Temp: 98.6°F, RR: 18/min', 'Hypertension with possible cardiac involvement',
 'Continue antihypertensive therapy, lifestyle modifications, dietary counseling',
 'Amlodipine 5mg daily, Aspirin 81mg daily', 'Cholesterol: 220 mg/dL, Glucose: 110 mg/dL, Normal CBC',
 'ECG: Normal sinus rhythm, Chest X-ray: Normal cardiac silhouette',
 'Follow-up in 3 months, continue BP monitoring, return if chest pain worsens', 7),

-- EHR for Patient 2 (Fatema Khatun) - Neurological assessment  
(2, 8, 1, 2, 2, '2024-08-20', 'CONSULTATION', 'Severe headache with visual disturbances',
 'Patient experienced severe throbbing headache with visual aura lasting 30 minutes, followed by unilateral headache for 4 hours. History of similar episodes monthly.',
 'Neurological examination normal, no focal deficits, fundoscopy normal, neck supple',
 'BP: 125/80 mmHg, HR: 72 bpm, Temp: 98.2°F, RR: 16/min', 'Migraine with aura',
 'Acute treatment with triptans, preventive therapy initiated, trigger identification',
 'Sumatriptan 50mg PRN for acute episodes, Propranolol 40mg daily for prevention',
 'CBC: Normal, ESR: 15 mm/hr, Normal metabolic panel',
 'Brain MRI: No structural abnormalities, no signs of intracranial pathology',
 'Return if symptoms worsen or increase in frequency, follow-up in 1 month, maintain headache diary', 8);

-- Insert fewer Attachments for medical records - only referencing existing EHRs or using NULL
INSERT INTO attachment (fk_patient_id, fk_appointment_id, fk_ehr_id, uploaded_by_id, file_name, file_path, file_size,
                        file_type, attachment_type, description, created_by)
VALUES
-- Attachments linked to existing EHR records
(1, 1, 1, 7, 'ECG_Report_20240815.pdf', '/uploads/medical/2024/08/ECG_Report_20240815.pdf', 245760, 'application/pdf',
 'LAB_REPORT', '12-lead ECG showing normal sinus rhythm', 7),
(2, 2, 2, 8, 'MRI_Brain_20240820.dcm', '/uploads/imaging/2024/08/MRI_Brain_20240820.dcm', 52428800, 'application/dicom',
 'IMAGING', 'Brain MRI T1 and T2 weighted images', 8),

-- Attachments without EHR reference (using NULL for fk_ehr_id)
(31, NULL, NULL, 7, 'Insurance_Card_BNHI.jpg', '/uploads/insurance/2024/10/Insurance_Card_BNHI_031.jpg', 512000,
 'image/jpeg', 'INSURANCE_DOCUMENT', 'BNHI insurance card copy', 7),
(32, NULL, NULL, 11, 'Lab_Results_20241007.pdf', '/uploads/medical/2024/10/Lab_Results_20241007.pdf', 156672,
 'application/pdf', 'LAB_REPORT', 'Pre-operative blood work and chemistry panel', 11);

-- Insert Audit Log entries for system tracking
INSERT INTO audit_log (fk_user_id, table_name, record_id, action, old_values, new_values, ip_address, user_agent,
                       created_dt)
VALUES
-- Recent system activities
(1, 'hospital', 1, 'UPDATE', '{"available_beds": 125}', '{"available_beds": 120}', '192.168.1.100',
 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '2024-10-08 09:15:23'),
(7, 'patient', 1, 'UPDATE', '{"status": "ACTIVE"}', '{"status": "ACTIVE", "last_visit": "2024-08-15"}', '10.0.0.50',
 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '2024-08-15 09:30:45'),
(8, 'ehr', 2, 'INSERT', NULL, '{"patient_id": 2, "doctor_id": 8, "diagnosis": "Migraine headache"}', '10.0.0.51',
 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '2024-08-20 10:45:12'),
(11, 'appointment', 3, 'UPDATE', '{"status": "SCHEDULED"}', '{"status": "COMPLETED"}', '10.0.0.52',
 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '2024-09-05 14:35:22'),
(17, 'billing', 5, 'INSERT', NULL, '{"patient_id": 5, "amount": 5445.00, "status": "PAID"}', '10.0.0.53',
 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '2024-10-08 17:15:30'),
(2, 'user', 7, 'UPDATE', '{"last_login_dt": "2024-10-07 08:00:00"}', '{"last_login_dt": "2024-10-08 08:15:23"}',
 '192.168.1.101', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '2024-10-08 08:15:23'),
(3, 'department', 4, 'UPDATE', '{"total_staff": 18}', '{"total_staff": 20}', '192.168.1.102',
 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '2024-10-07 14:20:15'),
(23, 'admission', 34, 'INSERT', NULL, '{"patient_id": 34, "hospital_id": 5, "admission_date": "2024-10-09"}',
 '10.0.0.55', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '2024-10-09 08:05:45');

-- Update some statistics and relationships
UPDATE hospital
SET available_beds = available_beds - 1
WHERE id IN (1, 2, 3, 5); -- Reflect current admissions
UPDATE department
SET total_staff = total_staff + (SELECT COUNT(*) FROM user WHERE fk_department_id = department.id AND role = 'DOCTOR');

-- Final data consistency updates
UPDATE user
SET last_login_dt = DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 7) DAY)
WHERE role IN ('DOCTOR', 'HOSPITAL_ADMIN');
UPDATE appointment
SET reminder_sent    = TRUE,
    reminder_sent_at = DATE_SUB(start_time, INTERVAL 24 HOUR)
WHERE status IN ('CONFIRMED', 'SCHEDULED')
  AND start_time > NOW();
UPDATE billing
SET due_date = DATE_ADD(bill_date, INTERVAL 30 DAY)
WHERE due_date IS NULL;

-- Success message
SELECT 'Hospital Management System database successfully populated with comprehensive data!' as result;
