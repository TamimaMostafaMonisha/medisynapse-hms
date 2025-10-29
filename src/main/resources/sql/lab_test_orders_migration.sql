-- ============================================================================
-- Lab Test Orders & Enhanced Prescription Workflow Migration
-- Project: MediSynapse HMS
-- Date: October 28, 2025
-- Description: Adds lab test ordering functionality and prescription enhancements
-- ============================================================================

-- Step 1: Create lab_test_master table FIRST (needs to exist before lab_test_orders)
-- ============================================================================
CREATE TABLE IF NOT EXISTS lab_test_master
(
    id                       BIGINT PRIMARY KEY AUTO_INCREMENT,
    test_name                VARCHAR(255) NOT NULL UNIQUE,
    test_code                VARCHAR(50) UNIQUE,
    test_type                VARCHAR(50)  NOT NULL,
    category                 VARCHAR(100),
    description              TEXT,
    typical_turnaround_hours INT,
    requires_fasting         BOOLEAN  DEFAULT FALSE,
    sample_type              VARCHAR(100),
    is_active                BOOLEAN  DEFAULT TRUE,
    created_dt               DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by               BIGINT,
    updated_by               BIGINT,
    version                  INT      DEFAULT 1
);

-- Create indexes for lab_test_master
CREATE INDEX idx_lab_test_master_active ON lab_test_master (is_active);
CREATE INDEX idx_lab_test_master_type ON lab_test_master (test_type);
CREATE INDEX idx_lab_test_master_category ON lab_test_master (category);

-- Insert sample lab test data (35 common tests)
INSERT INTO lab_test_master (test_name, test_code, test_type, category, description, typical_turnaround_hours,
                             requires_fasting, sample_type)
VALUES ('Complete Blood Count (CBC)', 'CBC', 'BLOOD_TEST', 'Hematology',
        'Measures different components of blood including RBC, WBC, Hemoglobin, Hematocrit, and Platelets', 2, FALSE,
        'Whole Blood'),
       ('Blood Glucose (Fasting)', 'FBS', 'BLOOD_TEST', 'Biochemistry',
        'Measures blood sugar level after overnight fasting', 1, TRUE, 'Serum'),
       ('Blood Glucose (Random)', 'RBS', 'BLOOD_TEST', 'Biochemistry', 'Measures blood sugar level at any time', 1,
        FALSE, 'Serum'),
       ('Lipid Profile', 'LIPID', 'BLOOD_TEST', 'Biochemistry',
        'Measures cholesterol (Total, HDL, LDL) and triglycerides', 4, TRUE, 'Serum'),
       ('Liver Function Test (LFT)', 'LFT', 'BLOOD_TEST', 'Biochemistry',
        'Evaluates liver health including ALT, AST, ALP, Bilirubin, and Albumin', 4, FALSE, 'Serum'),
       ('Kidney Function Test (KFT)', 'KFT', 'BLOOD_TEST', 'Biochemistry',
        'Evaluates kidney function including Creatinine, Urea, and Uric Acid', 4, FALSE, 'Serum'),
       ('Thyroid Function Test', 'TFT', 'BLOOD_TEST', 'Endocrinology', 'Measures thyroid hormones (TSH, T3, T4)', 24,
        FALSE, 'Serum'),
       ('HbA1c', 'HBA1C', 'BLOOD_TEST', 'Biochemistry',
        'Glycated hemoglobin - measures average blood sugar over 3 months', 4, FALSE, 'Whole Blood'),
       ('Urine Routine Examination', 'URE', 'URINE_TEST', 'Urinalysis',
        'Analyzes urine composition including color, pH, protein, glucose, and microscopy', 1, FALSE, 'Urine'),
       ('Urine Culture', 'UC', 'URINE_TEST', 'Microbiology', 'Identifies bacteria causing urinary tract infection', 48,
        FALSE, 'Urine'),
       ('Chest X-Ray', 'CXR', 'IMAGING', 'Radiology', 'X-ray image of chest to evaluate lungs, heart, and chest wall',
        1, FALSE, 'N/A'),
       ('Chest X-Ray (PA & Lateral)', 'CXR-PA-LAT', 'IMAGING', 'Radiology',
        'Chest X-ray with posterior-anterior and lateral views', 1, FALSE, 'N/A'),
       ('ECG', 'ECG', 'OTHER', 'Cardiology', 'Electrocardiogram - records electrical activity of the heart', 0, FALSE,
        'N/A'),
       ('Ultrasound Abdomen', 'USG-ABD', 'IMAGING', 'Radiology',
        'Ultrasound of abdominal organs (liver, gallbladder, pancreas, spleen, kidneys)', 1, TRUE, 'N/A'),
       ('Ultrasound Pelvis', 'USG-PELV', 'IMAGING', 'Radiology', 'Ultrasound of pelvic organs', 1, TRUE, 'N/A'),
       ('CT Scan Head', 'CT-HEAD', 'IMAGING', 'Radiology', 'Computed Tomography scan of head and brain', 2, FALSE,
        'N/A'),
       ('MRI Brain', 'MRI-BRAIN', 'IMAGING', 'Radiology', 'Magnetic Resonance Imaging of brain', 24, FALSE, 'N/A'),
       ('Blood Group & Rh', 'BG-RH', 'BLOOD_TEST', 'Hematology', 'Determines ABO blood group and Rh factor', 1, FALSE,
        'Whole Blood'),
       ('ESR', 'ESR', 'BLOOD_TEST', 'Hematology', 'Erythrocyte Sedimentation Rate - measures inflammation', 1, FALSE,
        'Whole Blood'),
       ('Prothrombin Time (PT)', 'PT', 'BLOOD_TEST', 'Hematology', 'Measures blood clotting time', 2, FALSE, 'Plasma'),
       ('APTT', 'APTT', 'BLOOD_TEST', 'Hematology', 'Activated Partial Thromboplastin Time - measures blood clotting',
        2, FALSE, 'Plasma'),
       ('Vitamin D', 'VIT-D', 'BLOOD_TEST', 'Biochemistry', 'Measures Vitamin D (25-OH) level', 24, FALSE, 'Serum'),
       ('Vitamin B12', 'VIT-B12', 'BLOOD_TEST', 'Biochemistry', 'Measures Vitamin B12 level', 24, FALSE, 'Serum'),
       ('Serum Electrolytes', 'ELECTRO', 'BLOOD_TEST', 'Biochemistry', 'Measures Sodium, Potassium, Chloride levels', 2,
        FALSE, 'Serum'),
       ('HIV Test', 'HIV', 'BLOOD_TEST', 'Serology', 'Screening test for HIV antibodies', 24, FALSE, 'Serum'),
       ('Hepatitis B Surface Antigen', 'HBSAG', 'BLOOD_TEST', 'Serology', 'Screening test for Hepatitis B infection',
        24, FALSE, 'Serum'),
       ('Hepatitis C Antibody', 'HCV', 'BLOOD_TEST', 'Serology', 'Screening test for Hepatitis C infection', 24, FALSE,
        'Serum'),
       ('Widal Test', 'WIDAL', 'BLOOD_TEST', 'Serology', 'Test for typhoid fever', 4, FALSE, 'Serum'),
       ('Dengue NS1 Antigen', 'DENGUE-NS1', 'BLOOD_TEST', 'Serology', 'Early detection of Dengue virus', 2, FALSE,
        'Serum'),
       ('Dengue IgM/IgG', 'DENGUE-AB', 'BLOOD_TEST', 'Serology', 'Dengue antibodies test', 4, FALSE, 'Serum'),
       ('Stool Routine', 'STOOL-RE', 'OTHER', 'Microbiology', 'Microscopic examination of stool', 1, FALSE, 'Stool'),
       ('Stool Culture', 'STOOL-C', 'OTHER', 'Microbiology', 'Identifies bacteria in stool', 48, FALSE, 'Stool'),
       ('Sputum Culture', 'SPUTUM-C', 'OTHER', 'Microbiology', 'Identifies bacteria in sputum', 48, FALSE, 'Sputum'),
       ('Pap Smear', 'PAP', 'BIOPSY', 'Pathology', 'Cervical cancer screening test', 72, FALSE, 'Cervical Cells'),
       ('Biopsy - General', 'BIOPSY', 'BIOPSY', 'Pathology', 'Microscopic examination of tissue sample', 120, FALSE,
        'Tissue')
ON DUPLICATE KEY UPDATE test_name = VALUES(test_name);

-- Step 2: Create lab_test_orders table
-- ============================================================================
CREATE TABLE IF NOT EXISTS lab_test_orders
(
    id                    BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_patient_id         BIGINT       NOT NULL,
    fk_appointment_id     BIGINT       NOT NULL,
    fk_doctor_id          BIGINT       NOT NULL,
    fk_prescription_id    BIGINT       NULL,
    fk_hospital_id        BIGINT       NOT NULL,
    fk_lab_test_master_id BIGINT       NOT NULL, -- Foreign key to lab_test_master

    -- Urgency and Status
    urgency               ENUM ('ROUTINE', 'URGENT', 'STAT') DEFAULT 'ROUTINE',
    status                ENUM ('ORDERED', 'SAMPLE_COLLECTED', 'IN_PROGRESS', 'COMPLETED', 'REVIEWED', 'CANCELLED')
                                                             DEFAULT 'ORDERED',

    -- Clinical Information
    clinical_notes        TEXT,
    suspected_diagnosis   VARCHAR(500),

    -- Timestamps
    ordered_at            DATETIME                           DEFAULT CURRENT_TIMESTAMP,
    sample_collected_at   DATETIME     NULL,
    completed_at          DATETIME     NULL,
    reviewed_at           DATETIME     NULL,
    reviewed_by           BIGINT       NULL,

    -- Report Information (Links to attachment table)
    report_file_url       VARCHAR(500) NULL,
    uploaded_by           BIGINT       NULL,
    uploaded_at           DATETIME     NULL,

    -- Audit Fields
    created_dt            DATETIME                           DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt       DATETIME                           DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by            BIGINT,
    updated_by            BIGINT,
    is_active             BOOLEAN                            DEFAULT TRUE,
    version               INT                                DEFAULT 1,

    -- Foreign Keys
    CONSTRAINT fk_lab_orders_patient FOREIGN KEY (fk_patient_id) REFERENCES patient (id) ON DELETE CASCADE,
    CONSTRAINT fk_lab_orders_appointment FOREIGN KEY (fk_appointment_id) REFERENCES appointment (id) ON DELETE CASCADE,
    CONSTRAINT fk_lab_orders_doctor FOREIGN KEY (fk_doctor_id) REFERENCES user (id) ON DELETE RESTRICT,
    CONSTRAINT fk_lab_orders_prescription FOREIGN KEY (fk_prescription_id) REFERENCES prescription (id) ON DELETE SET NULL,
    CONSTRAINT fk_lab_orders_hospital FOREIGN KEY (fk_hospital_id) REFERENCES hospital (id) ON DELETE CASCADE,
    CONSTRAINT fk_lab_orders_lab_test_master FOREIGN KEY (fk_lab_test_master_id) REFERENCES lab_test_master (id) ON DELETE RESTRICT,
    CONSTRAINT fk_lab_orders_reviewed_by FOREIGN KEY (reviewed_by) REFERENCES user (id) ON DELETE SET NULL,
    CONSTRAINT fk_lab_orders_uploaded_by FOREIGN KEY (uploaded_by) REFERENCES user (id) ON DELETE SET NULL
);

-- Step 3: Create indexes for performance
-- ============================================================================
CREATE INDEX idx_lab_orders_patient ON lab_test_orders (fk_patient_id);
CREATE INDEX idx_lab_orders_appointment ON lab_test_orders (fk_appointment_id);
CREATE INDEX idx_lab_orders_doctor ON lab_test_orders (fk_doctor_id);
CREATE INDEX idx_lab_orders_hospital ON lab_test_orders (fk_hospital_id);
CREATE INDEX idx_lab_orders_lab_test_master ON lab_test_orders (fk_lab_test_master_id);
CREATE INDEX idx_lab_orders_status ON lab_test_orders (status);
CREATE INDEX idx_lab_orders_ordered_at ON lab_test_orders (ordered_at);
CREATE INDEX idx_lab_orders_prescription ON lab_test_orders (fk_prescription_id);

-- Step 4: Enhance prescription table
-- ============================================================================
-- Check if columns exist before adding them
SET @dbname = DATABASE();
SET @tablename = 'prescription';
SET @columnname = 'prescription_type';
SET @preparedStatement = (SELECT IF(
                                         (SELECT COUNT(*)
                                          FROM INFORMATION_SCHEMA.COLUMNS
                                          WHERE (table_name = @tablename)
                                            AND (table_schema = @dbname)
                                            AND (column_name = @columnname)) > 0,
                                         'SELECT 1',
                                         CONCAT('ALTER TABLE ', @tablename,
                                                ' ADD COLUMN prescription_type ENUM(''PRELIMINARY'', ''FINAL'') DEFAULT ''FINAL'' AFTER notes;')
                                 ));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- Add clinical_diagnosis
SET @columnname = 'clinical_diagnosis';
SET @preparedStatement = (SELECT IF(
                                         (SELECT COUNT(*)
                                          FROM INFORMATION_SCHEMA.COLUMNS
                                          WHERE (table_name = @tablename)
                                            AND (table_schema = @dbname)
                                            AND (column_name = @columnname)) > 0,
                                         'SELECT 1',
                                         CONCAT('ALTER TABLE ', @tablename,
                                                ' ADD COLUMN clinical_diagnosis VARCHAR(500) AFTER prescription_type;')
                                 ));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- Add follow_up_required
SET @columnname = 'follow_up_required';
SET @preparedStatement = (SELECT IF(
                                         (SELECT COUNT(*)
                                          FROM INFORMATION_SCHEMA.COLUMNS
                                          WHERE (table_name = @tablename)
                                            AND (table_schema = @dbname)
                                            AND (column_name = @columnname)) > 0,
                                         'SELECT 1',
                                         CONCAT('ALTER TABLE ', @tablename,
                                                ' ADD COLUMN follow_up_required BOOLEAN DEFAULT FALSE AFTER clinical_diagnosis;')
                                 ));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- Add follow_up_date
SET @columnname = 'follow_up_date';
SET @preparedStatement = (SELECT IF(
                                         (SELECT COUNT(*)
                                          FROM INFORMATION_SCHEMA.COLUMNS
                                          WHERE (table_name = @tablename)
                                            AND (table_schema = @dbname)
                                            AND (column_name = @columnname)) > 0,
                                         'SELECT 1',
                                         CONCAT('ALTER TABLE ', @tablename,
                                                ' ADD COLUMN follow_up_date DATE NULL AFTER follow_up_required;')
                                 ));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- Add superseded_by
SET @columnname = 'superseded_by';
SET @preparedStatement = (SELECT IF(
                                         (SELECT COUNT(*)
                                          FROM INFORMATION_SCHEMA.COLUMNS
                                          WHERE (table_name = @tablename)
                                            AND (table_schema = @dbname)
                                            AND (column_name = @columnname)) > 0,
                                         'SELECT 1',
                                         CONCAT('ALTER TABLE ', @tablename,
                                                ' ADD COLUMN superseded_by BIGINT NULL AFTER follow_up_date;')
                                 ));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- Step 5: Add indexes to prescription table
-- ============================================================================
-- Index for prescription_type (if column exists)
SET @s = (SELECT IF(
                         (SELECT COUNT(*)
                          FROM INFORMATION_SCHEMA.STATISTICS
                          WHERE table_schema = DATABASE()
                            AND table_name = 'prescription'
                            AND index_name = 'idx_prescription_type') > 0,
                         'SELECT 1',
                         'CREATE INDEX idx_prescription_type ON prescription(prescription_type)'
                 ));
PREPARE stmt FROM @s;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Index for superseded_by
SET @s = (SELECT IF(
                         (SELECT COUNT(*)
                          FROM INFORMATION_SCHEMA.STATISTICS
                          WHERE table_schema = DATABASE()
                            AND table_name = 'prescription'
                            AND index_name = 'idx_prescription_superseded') > 0,
                         'SELECT 1',
                         'CREATE INDEX idx_prescription_superseded ON prescription(superseded_by)'
                 ));
PREPARE stmt FROM @s;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Step 6: Update prescription status enum to include SUPERSEDED
-- ============================================================================
-- Note: This is a cautious approach that won't fail if enum already has the value
ALTER TABLE prescription
    MODIFY COLUMN status ENUM ('ACTIVE', 'COMPLETED', 'CANCELLED', 'EXPIRED', 'SUPERSEDED')
        DEFAULT 'ACTIVE';

-- Step 6: Create lab_test_master table (Optional - for lab test catalog)
-- ============================================================================
CREATE TABLE IF NOT EXISTS lab_test_master
(
    id                       BIGINT PRIMARY KEY AUTO_INCREMENT,
    test_name                VARCHAR(255) NOT NULL UNIQUE,
    test_code                VARCHAR(50) UNIQUE,
    test_type                VARCHAR(50)  NOT NULL,
    category                 VARCHAR(100),
    description              TEXT,
    typical_turnaround_hours INT,
    requires_fasting         BOOLEAN  DEFAULT FALSE,
    sample_type              VARCHAR(100),
    is_active                BOOLEAN  DEFAULT TRUE,
    created_dt               DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by               BIGINT,
    updated_by               BIGINT,
    version                  INT      DEFAULT 1
);

# -- Step 7: Create indexes for lab_test_master
# -- ============================================================================
# CREATE INDEX idx_lab_test_master_active ON lab_test_master (is_active);
# CREATE INDEX idx_lab_test_master_type ON lab_test_master (test_type);
# CREATE INDEX idx_lab_test_master_category ON lab_test_master (category);
#
# -- Step 8: Insert sample lab test data
# -- ============================================================================
# INSERT INTO lab_test_master (test_name, test_code, test_type, category, description, typical_turnaround_hours,
#                              requires_fasting, sample_type)
# VALUES ('Complete Blood Count (CBC)', 'CBC', 'BLOOD_TEST', 'Hematology',
#         'Measures different components of blood including RBC, WBC, Hemoglobin, Hematocrit, and Platelets', 2, FALSE,
#         'Whole Blood'),
#        ('Blood Glucose (Fasting)', 'FBS', 'BLOOD_TEST', 'Biochemistry',
#         'Measures blood sugar level after overnight fasting', 1, TRUE, 'Serum'),
#        ('Blood Glucose (Random)', 'RBS', 'BLOOD_TEST', 'Biochemistry', 'Measures blood sugar level at any time', 1,
#         FALSE, 'Serum'),
#        ('Lipid Profile', 'LIPID', 'BLOOD_TEST', 'Biochemistry',
#         'Measures cholesterol (Total, HDL, LDL) and triglycerides', 4, TRUE, 'Serum'),
#        ('Liver Function Test (LFT)', 'LFT', 'BLOOD_TEST', 'Biochemistry',
#         'Evaluates liver health including ALT, AST, ALP, Bilirubin, and Albumin', 4, FALSE, 'Serum'),
#        ('Kidney Function Test (KFT)', 'KFT', 'BLOOD_TEST', 'Biochemistry',
#         'Evaluates kidney function including Creatinine, Urea, and Uric Acid', 4, FALSE, 'Serum'),
#        ('Thyroid Function Test', 'TFT', 'BLOOD_TEST', 'Endocrinology', 'Measures thyroid hormones (TSH, T3, T4)', 24,
#         FALSE, 'Serum'),
#        ('HbA1c', 'HBA1C', 'BLOOD_TEST', 'Biochemistry',
#         'Glycated hemoglobin - measures average blood sugar over 3 months', 4, FALSE, 'Whole Blood'),
#        ('Urine Routine Examination', 'URE', 'URINE_TEST', 'Urinalysis',
#         'Analyzes urine composition including color, pH, protein, glucose, and microscopy', 1, FALSE, 'Urine'),
#        ('Urine Culture', 'UC', 'URINE_TEST', 'Microbiology', 'Identifies bacteria causing urinary tract infection', 48,
#         FALSE, 'Urine'),
#        ('Chest X-Ray', 'CXR', 'IMAGING', 'Radiology', 'X-ray image of chest to evaluate lungs, heart, and chest wall',
#         1, FALSE, 'N/A'),
#        ('Chest X-Ray (PA & Lateral)', 'CXR-PA-LAT', 'IMAGING', 'Radiology',
#         'Chest X-ray with posterior-anterior and lateral views', 1, FALSE, 'N/A'),
#        ('ECG', 'ECG', 'OTHER', 'Cardiology', 'Electrocardiogram - records electrical activity of the heart', 0, FALSE,
#         'N/A'),
#        ('Ultrasound Abdomen', 'USG-ABD', 'IMAGING', 'Radiology',
#         'Ultrasound of abdominal organs (liver, gallbladder, pancreas, spleen, kidneys)', 1, TRUE, 'N/A'),
#        ('Ultrasound Pelvis', 'USG-PELV', 'IMAGING', 'Radiology', 'Ultrasound of pelvic organs', 1, TRUE, 'N/A'),
#        ('CT Scan Head', 'CT-HEAD', 'IMAGING', 'Radiology', 'Computed Tomography scan of head and brain', 2, FALSE,
#         'N/A'),
#        ('MRI Brain', 'MRI-BRAIN', 'IMAGING', 'Radiology', 'Magnetic Resonance Imaging of brain', 24, FALSE, 'N/A'),
#        ('Blood Group & Rh', 'BG-RH', 'BLOOD_TEST', 'Hematology', 'Determines ABO blood group and Rh factor', 1, FALSE,
#         'Whole Blood'),
#        ('ESR', 'ESR', 'BLOOD_TEST', 'Hematology', 'Erythrocyte Sedimentation Rate - measures inflammation', 1, FALSE,
#         'Whole Blood'),
#        ('Prothrombin Time (PT)', 'PT', 'BLOOD_TEST', 'Hematology', 'Measures blood clotting time', 2, FALSE, 'Plasma'),
#        ('APTT', 'APTT', 'BLOOD_TEST', 'Hematology', 'Activated Partial Thromboplasin Time - measures blood clotting', 2,
#         FALSE, 'Plasma'),
#        ('Vitamin D', 'VIT-D', 'BLOOD_TEST', 'Biochemistry', 'Measures Vitamin D (25-OH) level', 24, FALSE, 'Serum'),
#        ('Vitamin B12', 'VIT-B12', 'BLOOD_TEST', 'Biochemistry', 'Measures Vitamin B12 level', 24, FALSE, 'Serum'),
#        ('Serum Electrolytes', 'ELECTRO', 'BLOOD_TEST', 'Biochemistry', 'Measures Sodium, Potassium, Chloride levels', 2,
#         FALSE, 'Serum'),
#        ('HIV Test', 'HIV', 'BLOOD_TEST', 'Serology', 'Screening test for HIV antibodies', 24, FALSE, 'Serum'),
#        ('Hepatitis B Surface Antigen', 'HBSAG', 'BLOOD_TEST', 'Serology', 'Screening test for Hepatitis B infection',
#         24, FALSE, 'Serum'),
#        ('Hepatitis C Antibody', 'HCV', 'BLOOD_TEST', 'Serology', 'Screening test for Hepatitis C infection', 24, FALSE,
#         'Serum'),
#        ('Widal Test', 'WIDAL', 'BLOOD_TEST', 'Serology', 'Test for typhoid fever', 4, FALSE, 'Serum'),
#        ('Dengue NS1 Antigen', 'DENGUE-NS1', 'BLOOD_TEST', 'Serology', 'Early detection of Dengue virus', 2, FALSE,
#         'Serum'),
#        ('Dengue IgM/IgG', 'DENGUE-AB', 'BLOOD_TEST', 'Serology', 'Dengue antibodies test', 4, FALSE, 'Serum'),
#        ('Stool Routine', 'STOOL-RE', 'OTHER', 'Microbiology', 'Microscopic examination of stool', 1, FALSE, 'Stool'),
#        ('Stool Culture', 'STOOL-C', 'OTHER', 'Microbiology', 'Identifies bacteria in stool', 48, FALSE, 'Stool'),
#        ('Sputum Culture', 'SPUTUM-C', 'OTHER', 'Microbiology', 'Identifies bacteria in sputum', 48, FALSE, 'Sputum'),
#        ('Pap Smear', 'PAP', 'BIOPSY', 'Pathology', 'Cervical cancer screening test', 72, FALSE, 'Cervical Cells'),
#        ('Biopsy - General', 'BIOPSY', 'BIOPSY', 'Pathology', 'Microscopic examination of tissue sample', 120, FALSE,
#         'Tissue')
# ON DUPLICATE KEY UPDATE test_name = VALUES(test_name);

-- ============================================================================
-- Migration Complete
-- ============================================================================
-- Summary:
-- 1. Created lab_test_orders table with full audit trail
-- 2. Enhanced prescription table with 5 new fields
-- 3. Added appropriate indexes for performance
-- 4. Updated prescription status enum
-- 5. Created lab_test_master table with sample test catalog (35 common tests)
-- ============================================================================

