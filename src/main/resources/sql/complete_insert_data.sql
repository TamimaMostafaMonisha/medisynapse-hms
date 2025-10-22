-- ============================================================================
-- Complete Insert Data Script - Missing Tables and Records
-- This script completes the missing data insertions for all tables
-- Run this after the existing insert_default_data.sql
-- ============================================================================

-- Complete Patient-Insurance relationships for all patients
INSERT INTO patient_insurance (fk_patient_id, fk_insurance_id, is_primary, relationship_to_insured, member_id,
                               created_by)
VALUES
-- First 30 patients with various insurance providers
(1, 1, TRUE, 'SELF', 'MEM001234567', 7),
(2, 2, TRUE, 'SELF', 'MEM002345678', 7),
(3, 3, TRUE, 'SELF', 'MEM003456789', 7),
(4, 4, TRUE, 'SELF', 'MEM004567890', 7),
(5, 5, TRUE, 'SELF', 'MEM005678901', 7),
(6, 6, TRUE, 'SELF', 'MEM006789012', 7),
(7, 7, TRUE, 'SELF', 'MEM007890123', 7),
(8, 8, TRUE, 'SELF', 'MEM008901234', 7),
(9, 9, TRUE, 'SELF', 'MEM009012345', 7),
(10, 10, TRUE, 'SELF', 'MEM010123456', 7),
(11, 1, TRUE, 'SELF', 'MEM011234567', 7),
(12, 2, TRUE, 'SELF', 'MEM012345678', 7),
(13, 3, TRUE, 'SELF', 'MEM013456789', 7),
(14, 4, TRUE, 'SELF', 'MEM014567890', 7),
(15, 5, TRUE, 'SELF', 'MEM015678901', 7),
(16, 6, TRUE, 'SELF', 'MEM016789012', 7),
(17, 7, TRUE, 'SELF', 'MEM017890123', 7),
(18, 8, TRUE, 'SELF', 'MEM018901234', 7),
(19, 9, TRUE, 'SELF', 'MEM019012345', 7),
(20, 10, TRUE, 'SELF', 'MEM020123456', 7),
(21, 1, TRUE, 'SELF', 'MEM021234567', 7),
(22, 2, TRUE, 'SELF', 'MEM022345678', 7),
(23, 3, TRUE, 'SELF', 'MEM023456789', 7),
(24, 4, TRUE, 'SELF', 'MEM024567890', 7),
(25, 5, TRUE, 'SELF', 'MEM025678901', 7),
(26, 6, TRUE, 'SELF', 'MEM026789012', 7),
(27, 7, TRUE, 'SELF', 'MEM027890123', 7),
(28, 8, TRUE, 'SELF', 'MEM028901234', 7),
(29, 9, TRUE, 'SELF', 'MEM029012345', 7),
(30, 10, TRUE, 'SELF', 'MEM030123456', 7);

-- Continue with remaining patients (31-150) - using cycling through insurance providers
INSERT INTO patient_insurance (fk_patient_id, fk_insurance_id, is_primary, relationship_to_insured, member_id,
                               created_by)
SELECT p.id,
       ((p.id - 1) % 10) + 1 as insurance_id,
       TRUE,
       'SELF',
       CONCAT('MEM', LPAD(p.id, 3, '0'), LPAD(((p.id - 1) % 10) + 1, 6, FLOOR(RAND() * 999999))),
       CASE
           WHEN p.id <= 30 THEN 7
           WHEN p.id <= 60 THEN 11
           WHEN p.id <= 90 THEN 15
           WHEN p.id <= 120 THEN 19
           ELSE 23
           END
FROM patient p
WHERE p.id > 30
  AND p.id <= 150;

-- Complete Appointments for all patients
INSERT INTO appointment (fk_patient_id, fk_doctor_id, fk_department_id, fk_hospital_id, start_time, end_time,
                         duration_minutes, appointment_type, status, reason, notes, created_by)
VALUES
-- Dhaka Medical College Hospital appointments
(1, 7, 1, 1, '2024-10-15 09:00:00', '2024-10-15 09:30:00', 30, 'CONSULTATION', 'COMPLETED', 'Cardiac evaluation',
 'Patient completed initial consultation', 7),
(2, 8, 2, 1, '2024-10-15 10:00:00', '2024-10-15 10:45:00', 45, 'CONSULTATION', 'COMPLETED', 'Neurological assessment',
 'Comprehensive neurological examination completed', 8),
(3, 9, 3, 1, '2024-10-15 14:00:00', '2024-10-15 14:30:00', 30, 'EMERGENCY', 'COMPLETED', 'Emergency care',
 'Emergency treatment provided', 9),
(4, 10, 1, 1, '2024-10-16 11:00:00', '2024-10-16 11:30:00', 30, 'FOLLOW_UP', 'SCHEDULED', 'Cardiac follow-up',
 'Follow-up appointment scheduled', 10),
(5, 7, 1, 1, '2024-10-17 15:00:00', '2024-10-17 15:30:00', 30, 'CONSULTATION', 'SCHEDULED', 'Chest pain evaluation',
 'New patient consultation', 7),
-- Chittagong General Hospital appointments
(6, 11, 4, 2, '2024-10-15 08:00:00', '2024-10-15 08:45:00', 45, 'CONSULTATION', 'COMPLETED', 'Orthopedic consultation',
 'Joint pain assessment completed', 11),
(7, 12, 5, 2, '2024-10-15 09:30:00', '2024-10-15 10:00:00', 30, 'CONSULTATION', 'COMPLETED', 'Pediatric checkup',
 'Routine pediatric examination', 12),
(8, 13, 6, 2, '2024-10-15 16:00:00', '2024-10-15 16:30:00', 30, 'EMERGENCY', 'COMPLETED', 'Emergency treatment',
 'Emergency care provided', 13),
(9, 14, 4, 2, '2024-10-16 14:00:00', '2024-10-16 14:30:00', 30, 'FOLLOW_UP', 'SCHEDULED', 'Orthopedic follow-up',
 'Post-treatment follow-up', 14),
(10, 11, 4, 2, '2024-10-17 10:00:00', '2024-10-17 10:30:00', 30, 'CONSULTATION', 'SCHEDULED',
 'Joint replacement consultation', 'Surgical consultation', 11),
-- Sylhet Osmani Medical College Hospital appointments
(11, 15, 7, 3, '2024-10-15 11:00:00', '2024-10-15 11:30:00', 30, 'CONSULTATION', 'COMPLETED',
 'Dermatology consultation', 'Skin condition evaluation', 15),
(12, 16, 8, 3, '2024-10-15 14:30:00', '2024-10-15 15:15:00', 45, 'CONSULTATION', 'COMPLETED',
 'Gynecological examination', 'Annual gynecological checkup', 16),
(13, 17, 9, 3, '2024-10-15 18:00:00', '2024-10-15 18:30:00', 30, 'EMERGENCY', 'COMPLETED', 'Emergency care',
 'Critical care treatment', 17),
(14, 18, 7, 3, '2024-10-16 09:00:00', '2024-10-16 09:30:00', 30, 'FOLLOW_UP', 'SCHEDULED', 'Dermatology follow-up',
 'Treatment progress review', 18),
-- Rajshahi Medical College Hospital appointments
(15, 19, 10, 4, '2024-10-15 13:00:00', '2024-10-15 13:30:00', 30, 'CONSULTATION', 'COMPLETED', 'ENT consultation',
 'Hearing assessment', 19),
(16, 20, 11, 4, '2024-10-15 15:30:00', '2024-10-15 16:00:00', 30, 'CONSULTATION', 'COMPLETED', 'Eye examination',
 'Vision screening completed', 20),
(17, 21, 12, 4, '2024-10-15 19:00:00', '2024-10-15 19:30:00', 30, 'EMERGENCY', 'COMPLETED', 'Emergency treatment',
 'Emergency medical care', 21),
-- Khulna Medical College Hospital appointments
(18, 23, 13, 5, '2024-10-15 12:00:00', '2024-10-15 12:45:00', 45, 'CONSULTATION', 'COMPLETED', 'Surgical consultation',
 'Pre-operative consultation', 23),
(19, 24, 14, 5, '2024-10-15 16:30:00', '2024-10-15 17:00:00', 30, 'CONSULTATION', 'COMPLETED', 'Internal medicine',
 'General health assessment', 24),
(20, 25, 15, 5, '2024-10-15 20:00:00', '2024-10-15 20:30:00', 30, 'EMERGENCY', 'COMPLETED', 'Emergency care',
 'Emergency treatment provided', 25);

-- Generate more appointments for remaining patients
INSERT INTO appointment (fk_patient_id, fk_doctor_id, fk_department_id, fk_hospital_id, start_time, end_time,
                         duration_minutes, appointment_type, status, reason, notes, created_by)
SELECT p.id,
       CASE
           WHEN p.id <= 40 THEN 7 + ((p.id - 21) % 4) -- DMCH doctors
           WHEN p.id <= 60 THEN 11 + ((p.id - 41) % 4) -- CGH doctors
           WHEN p.id <= 80 THEN 15 + ((p.id - 61) % 4) -- SOMCH doctors
           WHEN p.id <= 100 THEN 19 + ((p.id - 81) % 4) -- RMCH doctors
           ELSE 23 + ((p.id - 101) % 4) -- KMCH doctors
           END                                                                                                 as doctor_id,
       CASE
           WHEN p.id <= 40 THEN 1 + ((p.id - 21) % 3) -- DMCH departments
           WHEN p.id <= 60 THEN 4 + ((p.id - 41) % 3) -- CGH departments
           WHEN p.id <= 80 THEN 7 + ((p.id - 61) % 3) -- SOMCH departments
           WHEN p.id <= 100 THEN 10 + ((p.id - 81) % 3) -- RMCH departments
           ELSE 13 + ((p.id - 101) % 3) -- KMCH departments
           END                                                                                                 as department_id,
       CASE
           WHEN p.id <= 40 THEN 1
           WHEN p.id <= 60 THEN 2
           WHEN p.id <= 80 THEN 3
           WHEN p.id <= 100 THEN 4
           ELSE 5
           END                                                                                                 as hospital_id,
       DATE_ADD('2024-10-01', INTERVAL (p.id % 30) DAY) +
       INTERVAL (8 + (p.id % 10)) HOUR                                                                         as start_time,
       DATE_ADD('2024-10-01', INTERVAL (p.id % 30) DAY) + INTERVAL (8 + (p.id % 10)) HOUR +
       INTERVAL 30 MINUTE                                                                                      as end_time,
       30                                                                                                      as duration_minutes,
       ELT(((p.id - 1) % 3) + 1, 'CONSULTATION', 'FOLLOW_UP', 'EMERGENCY')                                     as appointment_type,
       ELT(((p.id - 1) % 4) + 1, 'COMPLETED', 'SCHEDULED', 'CANCELLED',
           'NO_SHOW')                                                                                          as status,
       CONCAT('Medical consultation for patient ', p.id)                                                       as reason,
       CONCAT('Appointment notes for patient ', p.first_name, ' ', p.last_name)                                as notes,
       CASE
           WHEN p.id <= 40 THEN 7
           WHEN p.id <= 60 THEN 11
           WHEN p.id <= 80 THEN 15
           WHEN p.id <= 100 THEN 19
           ELSE 23
           END                                                                                                 as created_by
FROM patient p
WHERE p.id > 20
  AND p.id <= 150;

-- Complete EHR records for patients
INSERT INTO ehr (fk_patient_id, fk_doctor_id, fk_department_id, fk_hospital_id, fk_appointment_id, visit_date,
                 visit_type, chief_complaint, history_of_present_illness, physical_examination, vital_signs, diagnosis,
                 treatment_plan, medications, follow_up_instructions, created_by)
SELECT a.fk_patient_id,
       a.fk_doctor_id,
       a.fk_department_id,
       a.fk_hospital_id,
       a.id,
       DATE(a.start_time),
       a.appointment_type,
       a.reason,
       CONCAT('Patient presents with ', a.reason, '. Symptoms have been present for varying duration.'),
       'Physical examination completed with appropriate findings documented.',
       'Vital signs stable and within normal parameters for patient age and condition.',
       CONCAT('Clinical impression based on ', a.reason),
       'Treatment plan formulated based on clinical assessment and patient needs.',
       'Medications prescribed as per clinical indication.',
       'Patient advised to follow up as scheduled and return if symptoms worsen.',
       a.created_by
FROM appointment a
WHERE a.status = 'COMPLETED'
  AND a.id > 3;

-- Insert Billing records
INSERT INTO billing (fk_patient_id, fk_appointment_id, fk_hospital_id, billing_date, total_amount, discount_amount,
                     tax_amount, net_amount, payment_status, due_date, billing_type, itemized_charges,
                     insurance_claim_amount, patient_responsibility, created_by)
VALUES
-- Sample billing records for completed appointments
(1, 1, 1, '2024-10-15', 2500.00, 250.00, 225.00, 2475.00, 'PAID', '2024-10-30', 'CONSULTATION',
 'Consultation fee: 2000.00, Diagnostic tests: 500.00', 2000.00, 475.00, 7),
(2, 2, 1, '2024-10-15', 3000.00, 300.00, 270.00, 2970.00, 'PAID', '2024-10-30', 'CONSULTATION',
 'Neurological consultation: 2500.00, MRI brain: 500.00', 2400.00, 570.00, 8),
(3, 3, 1, '2024-10-15', 1500.00, 0.00, 135.00, 1635.00, 'PAID', '2024-10-30', 'EMERGENCY',
 'Emergency consultation: 1500.00', 1200.00, 435.00, 9),
(6, 6, 2, '2024-10-15', 1800.00, 180.00, 162.00, 1782.00, 'PENDING', '2024-11-15', 'CONSULTATION',
 'Orthopedic consultation: 1800.00', 1440.00, 342.00, 11),
(7, 7, 2, '2024-10-15', 1200.00, 120.00, 108.00, 1188.00, 'PAID', '2024-10-30', 'CONSULTATION',
 'Pediatric consultation: 1200.00', 960.00, 228.00, 12),
(8, 8, 2, '2024-10-15', 1600.00, 0.00, 144.00, 1744.00, 'PAID', '2024-10-30', 'EMERGENCY',
 'Emergency treatment: 1600.00', 1280.00, 464.00, 13),
(11, 11, 3, '2024-10-15', 1300.00, 130.00, 117.00, 1287.00, 'PAID', '2024-10-30', 'CONSULTATION',
 'Dermatology consultation: 1300.00', 1040.00, 247.00, 15),
(12, 12, 3, '2024-10-15', 1900.00, 190.00, 171.00, 1881.00, 'PENDING', '2024-11-15', 'CONSULTATION',
 'Gynecological examination: 1900.00', 1520.00, 361.00, 16),
(15, 15, 4, '2024-10-15', 1400.00, 140.00, 126.00, 1386.00, 'PAID', '2024-10-30', 'CONSULTATION',
 'ENT consultation: 1400.00', 1120.00, 266.00, 19),
(18, 18, 5, '2024-10-15', 2300.00, 230.00, 207.00, 2277.00, 'PENDING', '2024-11-15', 'CONSULTATION',
 'Surgical consultation: 2300.00', 1840.00, 437.00, 23);

-- Generate billing records for more completed appointments
INSERT INTO billing (fk_patient_id, fk_appointment_id, fk_hospital_id, billing_date, total_amount, discount_amount,
                     tax_amount, net_amount, payment_status, due_date, billing_type, itemized_charges,
                     insurance_claim_amount, patient_responsibility, created_by)
SELECT a.fk_patient_id,
       a.id,
       a.fk_hospital_id,
       DATE(a.start_time),
       ROUND(1000 + (RAND() * 2000), 2)                         as total_amount,
       ROUND((1000 + (RAND() * 2000)) * 0.1, 2)                 as discount_amount,
       ROUND((1000 + (RAND() * 2000)) * 0.09, 2)                as tax_amount,
       ROUND((1000 + (RAND() * 2000)) * 0.99, 2)                as net_amount,
       ELT(FLOOR(RAND() * 3) + 1, 'PAID', 'PENDING', 'OVERDUE') as payment_status,
       DATE_ADD(DATE(a.start_time), INTERVAL 30 DAY)            as due_date,
       a.appointment_type                                       as billing_type,
       CONCAT('Medical services for ', a.reason)                as itemized_charges,
       ROUND((1000 + (RAND() * 2000)) * 0.8, 2)                 as insurance_claim_amount,
       ROUND((1000 + (RAND() * 2000)) * 0.19, 2)                as patient_responsibility,
       a.created_by
FROM appointment a
WHERE a.status = 'COMPLETED'
  AND a.id > 10
LIMIT 50;

-- Insert Payment records for paid bills
INSERT INTO payment (fk_billing_id, payment_date, payment_amount, payment_method, transaction_id, payment_status,
                     payer_name, notes, created_by)
SELECT b.id,
       DATE_ADD(b.billing_date, INTERVAL FLOOR(RAND() * 10) DAY),
       b.net_amount,
       ELT(FLOOR(RAND() * 4) + 1, 'CASH', 'CARD', 'BANK_TRANSFER', 'MOBILE_BANKING'),
       CONCAT('TXN', LPAD(b.id, 8, '0'), FLOOR(RAND() * 1000)),
       'COMPLETED',
       CONCAT('Payment by patient ID ', b.fk_patient_id),
       'Payment completed successfully',
       b.created_by
FROM billing b
WHERE b.payment_status = 'PAID';

-- Insert Admission records
INSERT INTO admission (fk_patient_id, fk_doctor_id, fk_department_id, fk_hospital_id, admission_date,
                       expected_discharge_date, actual_discharge_date, admission_type, room_number, bed_number,
                       admission_reason, diagnosis, treatment_notes, discharge_summary, status, created_by)
VALUES (1, 7, 1, 1, '2024-10-20', '2024-10-25', NULL, 'PLANNED', '301A', '1', 'Cardiac catheterization procedure',
        'Coronary artery disease', 'Patient admitted for cardiac intervention', NULL, 'ADMITTED', 7),
       (3, 9, 3, 1, '2024-10-18', '2024-10-20', '2024-10-19', 'EMERGENCY', '101', '5', 'Acute chest pain',
        'Rule out myocardial infarction', 'Emergency admission, ruled out MI',
        'Patient stable, discharged with medications', 'DISCHARGED', 9),
       (6, 11, 4, 2, '2024-10-22', '2024-10-27', NULL, 'PLANNED', '205B', '3', 'Knee replacement surgery',
        'Osteoarthritis knee', 'Admitted for total knee replacement', NULL, 'ADMITTED', 11),
       (12, 16, 8, 3, '2024-10-19', '2024-10-21', '2024-10-21', 'PLANNED', '402', '2', 'Normal delivery',
        'Term pregnancy', 'Admitted for delivery', 'Normal vaginal delivery, mother and baby healthy', 'DISCHARGED',
        16),
       (18, 23, 13, 5, '2024-10-25', '2024-10-30', NULL, 'PLANNED', '501A', '1', 'Laparoscopic surgery',
        'Gallbladder stones', 'Admitted for laparoscopic cholecystectomy', NULL, 'ADMITTED', 23);

-- Insert additional admissions
INSERT INTO admission (fk_patient_id, fk_doctor_id, fk_department_id, fk_hospital_id, admission_date,
                       expected_discharge_date, actual_discharge_date, admission_type, room_number, bed_number,
                       admission_reason, diagnosis, treatment_notes, discharge_summary, status, created_by)
SELECT p.id,
       CASE
           WHEN p.id <= 40 THEN 7 + ((p.id - 1) % 4)
           WHEN p.id <= 60 THEN 11 + ((p.id - 41) % 4)
           WHEN p.id <= 80 THEN 15 + ((p.id - 61) % 4)
           WHEN p.id <= 100 THEN 19 + ((p.id - 81) % 4)
           ELSE 23 + ((p.id - 101) % 4)
           END,
       CASE
           WHEN p.id <= 40 THEN 1 + ((p.id - 1) % 3)
           WHEN p.id <= 60 THEN 4 + ((p.id - 41) % 3)
           WHEN p.id <= 80 THEN 7 + ((p.id - 61) % 3)
           WHEN p.id <= 100 THEN 10 + ((p.id - 81) % 3)
           ELSE 13 + ((p.id - 101) % 3)
           END,
       CASE
           WHEN p.id <= 40 THEN 1
           WHEN p.id <= 60 THEN 2
           WHEN p.id <= 80 THEN 3
           WHEN p.id <= 100 THEN 4
           ELSE 5
           END,
       DATE_ADD('2024-09-01', INTERVAL (p.id % 60) DAY),
       DATE_ADD('2024-09-01', INTERVAL (p.id % 60) + 3 DAY),
       CASE WHEN p.id % 3 = 0 THEN DATE_ADD('2024-09-01', INTERVAL (p.id % 60) + 3 DAY) ELSE NULL END,
       ELT(((p.id - 1) % 2) + 1, 'PLANNED', 'EMERGENCY'),
       CONCAT(FLOOR(100 + (p.id % 500)), ELT(((p.id - 1) % 2) + 1, 'A', 'B')),
       ((p.id - 1) % 4) + 1,
       CONCAT('Medical treatment for patient ', p.id),
       CONCAT('Clinical diagnosis for patient ', p.first_name, ' ', p.last_name),
       CONCAT('Treatment notes for admission of patient ', p.id),
       CASE WHEN p.id % 3 = 0 THEN CONCAT('Patient discharged in stable condition') ELSE NULL END,
       CASE WHEN p.id % 3 = 0 THEN 'DISCHARGED' ELSE 'ADMITTED' END,
       CASE
           WHEN p.id <= 40 THEN 7
           WHEN p.id <= 60 THEN 11
           WHEN p.id <= 80 THEN 15
           WHEN p.id <= 100 THEN 19
           ELSE 23
           END
FROM patient p
WHERE p.id > 25
  AND p.id <= 50
  AND p.id % 5 = 0;

-- Insert Shift records for staff
INSERT INTO shift (fk_user_id, fk_department_id, fk_hospital_id, shift_date, shift_type, start_time, end_time,
                   actual_start_time, actual_end_time, status, break_duration_minutes, overtime_hours, notes,
                   created_by)
VALUES
-- Doctor shifts
(7, 1, 1, '2024-10-15', 'DAY', '08:00:00', '16:00:00', '08:00:00', '16:30:00', 'COMPLETED', 60, 0.5,
 'Regular day shift completed', 2),
(8, 2, 1, '2024-10-15', 'DAY', '08:00:00', '16:00:00', '07:45:00', '16:00:00', 'COMPLETED', 60, 0,
 'Early arrival, shift completed on time', 2),
(9, 3, 1, '2024-10-15', 'NIGHT', '20:00:00', '08:00:00', '20:00:00', '08:00:00', 'COMPLETED', 120, 0,
 'Night emergency shift', 2),
(11, 4, 2, '2024-10-15', 'DAY', '08:00:00', '16:00:00', '08:00:00', '17:00:00', 'COMPLETED', 60, 1.0,
 'Extended shift for surgery', 3),
(12, 5, 2, '2024-10-15', 'DAY', '08:00:00', '16:00:00', '08:00:00', '16:00:00', 'COMPLETED', 60, 0,
 'Regular pediatric shift', 3),
(15, 7, 3, '2024-10-15', 'DAY', '08:00:00', '17:00:00', '08:00:00', '17:00:00', 'COMPLETED', 60, 0,
 'Dermatology clinic day', 4),
(16, 8, 3, '2024-10-15', 'DAY', '08:00:00', '17:00:00', '08:00:00', '17:30:00', 'COMPLETED', 60, 0.5,
 'Gynecology OPD extended', 4),
(19, 10, 4, '2024-10-15', 'DAY', '08:00:00', '16:00:00', '08:00:00', '16:00:00', 'COMPLETED', 60, 0, 'ENT clinic shift',
 5),
(23, 13, 5, '2024-10-15', 'DAY', '08:00:00', '18:00:00', '07:30:00', '18:00:00', 'COMPLETED', 90, 0,
 'Surgery department shift', 6);

-- Generate more shift records
INSERT INTO shift (fk_user_id, fk_department_id, fk_hospital_id, shift_date, shift_type, start_time, end_time,
                   actual_start_time, actual_end_time, status, break_duration_minutes, overtime_hours, notes,
                   created_by)
SELECT u.id,
       u.fk_department_id,
       u.fk_hospital_id,
       DATE_ADD('2024-10-01', INTERVAL (u.id % 30) DAY),
       ELT(((u.id - 1) % 3) + 1, 'DAY', 'EVENING', 'NIGHT'),
       CASE
           WHEN ((u.id - 1) % 3) + 1 = 1 THEN '08:00:00'
           WHEN ((u.id - 1) % 3) + 1 = 2 THEN '16:00:00'
           ELSE '20:00:00'
           END,
       CASE
           WHEN ((u.id - 1) % 3) + 1 = 1 THEN '16:00:00'
           WHEN ((u.id - 1) % 3) + 1 = 2 THEN '20:00:00'
           ELSE '08:00:00'
           END,
       CASE
           WHEN ((u.id - 1) % 3) + 1 = 1 THEN '08:00:00'
           WHEN ((u.id - 1) % 3) + 1 = 2 THEN '16:00:00'
           ELSE '20:00:00'
           END,
       CASE
           WHEN ((u.id - 1) % 3) + 1 = 1 THEN '16:00:00'
           WHEN ((u.id - 1) % 3) + 1 = 2 THEN '20:00:00'
           ELSE '08:00:00'
           END,
       'COMPLETED',
       60,
       ROUND(RAND() * 2, 1),
       CONCAT('Shift completed by ', u.name),
       CASE
           WHEN u.fk_hospital_id = 1 THEN 2
           WHEN u.fk_hospital_id = 2 THEN 3
           WHEN u.fk_hospital_id = 3 THEN 4
           WHEN u.fk_hospital_id = 4 THEN 5
           ELSE 6
           END
FROM user u
WHERE u.role = 'DOCTOR'
  AND u.id > 10;

-- Insert Attachment records
INSERT INTO attachment (entity_type, entity_id, file_name, file_path, file_size_bytes, mime_type, file_type,
                        description, uploaded_by, created_by)
VALUES ('PATIENT', 1, 'chest_xray_patient_1.jpg', '/uploads/medical/chest_xray_patient_1.jpg', 2048576, 'image/jpeg',
        'MEDICAL_IMAGE', 'Chest X-ray for cardiac evaluation', 7, 7),
       ('PATIENT', 2, 'mri_brain_patient_2.dcm', '/uploads/medical/mri_brain_patient_2.dcm', 52428800,
        'application/dicom', 'MEDICAL_IMAGE', 'MRI brain scan for neurological assessment', 8, 8),
       ('PATIENT', 3, 'ecg_patient_3.pdf', '/uploads/medical/ecg_patient_3.pdf', 1024000, 'application/pdf',
        'MEDICAL_DOCUMENT', 'ECG report for emergency patient', 9, 9),
       ('EHR', 1, 'lab_report_patient_1.pdf', '/uploads/lab/lab_report_patient_1.pdf', 512000, 'application/pdf',
        'LAB_RESULT', 'Complete blood count and lipid profile', 7, 7),
       ('EHR', 2, 'neurology_report_patient_2.pdf', '/uploads/reports/neurology_report_patient_2.pdf', 768000,
        'application/pdf', 'MEDICAL_REPORT', 'Detailed neurological examination report', 8, 8),
       ('PRESCRIPTION', 1, 'prescription_patient_1.pdf', '/uploads/prescriptions/prescription_patient_1.pdf', 256000,
        'application/pdf', 'PRESCRIPTION', 'Digital prescription for cardiac medications', 7, 7),
       ('APPOINTMENT', 5, 'appointment_confirmation.pdf', '/uploads/appointments/appointment_confirmation_5.pdf',
        128000, 'application/pdf', 'APPOINTMENT_DOCUMENT', 'Appointment confirmation letter', 7, 7),
       ('BILLING', 1, 'invoice_patient_1.pdf', '/uploads/billing/invoice_patient_1.pdf', 384000, 'application/pdf',
        'INVOICE', 'Detailed billing invoice', 7, 7);

-- Generate more attachment records
INSERT INTO attachment (entity_type, entity_id, file_name, file_path, file_size_bytes, mime_type, file_type,
                        description, uploaded_by, created_by)
SELECT ELT(((ROW_NUMBER() OVER ()) % 4) + 1, 'PATIENT', 'EHR', 'PRESCRIPTION', 'APPOINTMENT'),
       ((ROW_NUMBER() OVER ()) % 50) + 1,
       CONCAT('document_', ROW_NUMBER() OVER (), '.pdf'),
       CONCAT('/uploads/documents/document_', ROW_NUMBER() OVER (), '.pdf'),
       FLOOR(100000 + (RAND() * 5000000)),
       'application/pdf',
       'MEDICAL_DOCUMENT',
       CONCAT('Medical document ', ROW_NUMBER() OVER ()),
       7 + (((ROW_NUMBER() OVER ()) - 1) % 20),
       7 + (((ROW_NUMBER() OVER ()) - 1) % 20)
FROM patient p
WHERE p.id <= 30;

-- Insert Audit Log records
INSERT INTO audit_log (user_id, entity_type, entity_id, action, old_values, new_values, ip_address, user_agent,
                       session_id, created_dt)
VALUES (1, 'USER', 2, 'CREATE', NULL, '{"name":"System Administrator","role":"SUPER_ADMIN"}', '192.168.1.100',
        'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'sess_001', '2024-10-01 08:00:00'),
       (2, 'HOSPITAL', 1, 'UPDATE', '{"status":"INACTIVE"}', '{"status":"ACTIVE"}', '192.168.1.101',
        'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'sess_002', '2024-10-01 09:30:00'),
       (7, 'PATIENT', 1, 'CREATE', NULL, '{"first_name":"Mohammad","last_name":"Rahman"}', '192.168.1.102',
        'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'sess_003', '2024-10-01 10:15:00'),
       (7, 'APPOINTMENT', 1, 'CREATE', NULL, '{"patient_id":1,"doctor_id":7,"status":"SCHEDULED"}', '192.168.1.102',
        'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'sess_003', '2024-10-01 10:30:00'),
       (7, 'APPOINTMENT', 1, 'UPDATE', '{"status":"SCHEDULED"}', '{"status":"COMPLETED"}', '192.168.1.102',
        'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'sess_003', '2024-10-15 09:30:00'),
       (8, 'EHR', 1, 'CREATE', NULL, '{"patient_id":1,"doctor_id":7,"diagnosis":"Coronary artery disease"}',
        '192.168.1.103', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'sess_004', '2024-10-15 09:45:00'),
       (11, 'PATIENT', 6, 'UPDATE', '{"status":"INACTIVE"}', '{"status":"ACTIVE"}', '192.168.1.104',
        'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'sess_005', '2024-10-15 08:00:00'),
       (15, 'BILLING', 1, 'CREATE', NULL, '{"patient_id":1,"amount":2500.00,"status":"PENDING"}', '192.168.1.105',
        'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'sess_006', '2024-10-15 16:00:00'),
       (7, 'BILLING', 1, 'UPDATE', '{"status":"PENDING"}', '{"status":"PAID"}', '192.168.1.102',
        'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'sess_003', '2024-10-16 10:00:00');

-- Generate more audit log entries
INSERT INTO audit_log (user_id, entity_type, entity_id, action, old_values, new_values, ip_address, user_agent,
                       session_id, created_dt)
SELECT 7 + ((ROW_NUMBER() OVER ()) % 20),
       ELT(((ROW_NUMBER() OVER ()) % 5) + 1, 'PATIENT', 'APPOINTMENT', 'EHR', 'BILLING', 'PRESCRIPTION'),
       ((ROW_NUMBER() OVER ()) % 50) + 1,
       ELT(((ROW_NUMBER() OVER ()) % 3) + 1, 'CREATE', 'UPDATE', 'DELETE'),
       CASE WHEN ((ROW_NUMBER() OVER ()) % 3) + 1 != 1 THEN '{"status":"old_value"}' ELSE NULL END,
       '{"status":"new_value"}',
       CONCAT('192.168.1.', 100 + ((ROW_NUMBER() OVER ()) % 50)),
       'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
       CONCAT('sess_', LPAD(ROW_NUMBER() OVER (), 3, '0')),
       DATE_ADD('2024-10-01', INTERVAL ((ROW_NUMBER() OVER ()) % 30) DAY) + INTERVAL ((ROW_NUMBER() OVER ()) % 24) HOUR
FROM patient p
WHERE p.id <= 100;

-- Complete Prescription records for all patients with EHR records
INSERT INTO prescription (fk_patient_id, fk_doctor_id, fk_appointment_id, fk_ehr_id, prescription_date, medication_name,
                          dosage, frequency, duration, instructions, quantity, refills_allowed, created_by)
SELECT e.fk_patient_id,
       e.fk_doctor_id,
       e.fk_appointment_id,
       e.id,
       e.visit_date,
       ELT(((e.id - 1) % 10) + 1,
           'Amlodipine', 'Metformin', 'Omeprazole', 'Aspirin', 'Lisinopril',
           'Atorvastatin', 'Levothyroxine', 'Prednisolone', 'Salbutamol', 'Paracetamol'),
       ELT(((e.id - 1) % 10) + 1,
           '5mg', '500mg', '20mg', '75mg', '10mg',
           '20mg', '50mcg', '10mg', '100mcg', '500mg'),
       ELT(((e.id - 1) % 4) + 1, 'Once daily', 'Twice daily', 'Three times daily', 'As needed'),
       ELT(((e.id - 1) % 3) + 1, '30 days', '60 days', '90 days'),
       'Take as prescribed by your doctor. Follow medication instructions carefully.',
       CASE
           WHEN ((e.id - 1) % 4) + 1 = 1 THEN 30
           WHEN ((e.id - 1) % 4) + 1 = 2 THEN 60
           WHEN ((e.id - 1) % 4) + 1 = 3 THEN 90
           ELSE 10
           END,
       ELT(((e.id - 1) % 3) + 1, 0, 1, 2),
       e.created_by
FROM ehr e
WHERE e.id > 3;

-- Update statistics in hospital table
UPDATE hospital h
SET available_beds = total_beds - (SELECT COUNT(*)
                                   FROM admission a
                                   WHERE a.fk_hospital_id = h.id
                                     AND a.status = 'ADMITTED'),
    total_staff    = (SELECT COUNT(*)
                      FROM user u
                      WHERE u.fk_hospital_id = h.id
                        AND u.role IN ('DOCTOR', 'NURSE', 'RECEPTIONIST'));

-- Final verification queries (commented out - uncomment to verify data)
/*
SELECT 'department_type' as table_name, COUNT(*) as record_count FROM department_type
UNION ALL
SELECT 'address', COUNT(*) FROM address
UNION ALL
SELECT 'hospital', COUNT(*) FROM hospital
UNION ALL
SELECT 'user', COUNT(*) FROM user
UNION ALL
SELECT 'department', COUNT(*) FROM department
UNION ALL
SELECT 'patient', COUNT(*) FROM patient
UNION ALL
SELECT 'patient_hospital', COUNT(*) FROM patient_hospital
UNION ALL
SELECT 'insurance', COUNT(*) FROM insurance
UNION ALL
SELECT 'patient_insurance', COUNT(*) FROM patient_insurance
UNION ALL
SELECT 'appointment', COUNT(*) FROM appointment
UNION ALL
SELECT 'ehr', COUNT(*) FROM ehr
UNION ALL
SELECT 'prescription', COUNT(*) FROM prescription
UNION ALL
SELECT 'billing', COUNT(*) FROM billing
UNION ALL
SELECT 'payment', COUNT(*) FROM payment
UNION ALL
SELECT 'admission', COUNT(*) FROM admission
UNION ALL
SELECT 'shift', COUNT(*) FROM shift
UNION ALL
SELECT 'attachment', COUNT(*) FROM attachment
UNION ALL
SELECT 'audit_log', COUNT(*) FROM audit_log;
*/

-- ============================================================================
-- End of Complete Insert Data Script
-- All tables should now have comprehensive sample data
-- ============================================================================
