-- ============================================================================
-- Lab Test Orders - Enum Format Update
-- Project: MediSynapse HMS
-- Date: October 28, 2025
-- Description: Updates enum format from Blood/Urine to BLOOD_TEST/URINE_TEST
-- ============================================================================

-- IMPORTANT: Run this ONLY if you already ran the previous migration
-- This will update the enum format to match frontend expectations

-- Step 1: Drop existing tables (drop child table FIRST to avoid FK constraint errors)
-- ============================================================================
DROP TABLE IF EXISTS lab_test_orders;
DROP TABLE IF EXISTS lab_test_master;

-- Step 2: Create lab_test_master table FIRST (parent table)
-- ============================================================================
CREATE TABLE lab_test_master
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

-- Create indexes
CREATE INDEX idx_lab_test_master_active ON lab_test_master (is_active);
CREATE INDEX idx_lab_test_master_type ON lab_test_master (test_type);
CREATE INDEX idx_lab_test_master_category ON lab_test_master (category);

-- Step 3: Insert sample lab test data (BEFORE creating lab_test_orders)
-- ============================================================================
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
        'Tissue');

-- Step 4: Create lab_test_orders table (child table with FK to lab_test_master)
-- ============================================================================
CREATE TABLE lab_test_orders
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

    -- Report Information
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

-- Create indexes
CREATE INDEX idx_lab_orders_patient ON lab_test_orders (fk_patient_id);
CREATE INDEX idx_lab_orders_appointment ON lab_test_orders (fk_appointment_id);
CREATE INDEX idx_lab_orders_doctor ON lab_test_orders (fk_doctor_id);
CREATE INDEX idx_lab_orders_hospital ON lab_test_orders (fk_hospital_id);
CREATE INDEX idx_lab_orders_lab_test_master ON lab_test_orders (fk_lab_test_master_id);
CREATE INDEX idx_lab_orders_status ON lab_test_orders (status);
CREATE INDEX idx_lab_orders_ordered_at ON lab_test_orders (ordered_at);
CREATE INDEX idx_lab_orders_prescription ON lab_test_orders (fk_prescription_id);

-- ============================================================================
-- Update Complete
-- ============================================================================
-- Summary:
-- 1. Created lab_test_master table with 35 pre-loaded tests (parent table)
-- 2. Inserted sample lab test data
-- 3. Created lab_test_orders table with FK to lab_test_master (child table)
-- 4. All indexes created successfully
-- ============================================================================

SELECT 'Migration completed successfully. Lab test master catalog ready with 35 tests!' AS status;

