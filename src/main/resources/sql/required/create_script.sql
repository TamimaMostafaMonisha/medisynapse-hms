-- Drop tables if they exist (in reverse dependency order)
DROP TABLE IF EXISTS audit_log;
DROP TABLE IF EXISTS attachment;
DROP TABLE IF EXISTS shift;
DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS billing;
DROP TABLE IF EXISTS prescription;
DROP TABLE IF EXISTS ehr;
DROP TABLE IF EXISTS admission;
DROP TABLE IF EXISTS appointment;
DROP TABLE IF EXISTS patient_insurance;
DROP TABLE IF EXISTS insurance;
DROP TABLE IF EXISTS patient_hospital;
DROP TABLE IF EXISTS patient;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS department;
DROP TABLE IF EXISTS department_type;
DROP TABLE IF EXISTS hospital;
DROP TABLE IF EXISTS address;

-- Create Address table
CREATE TABLE address
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    line1           VARCHAR(255) NOT NULL,
    line2           VARCHAR(255),
    city            VARCHAR(100) NOT NULL,
    state           VARCHAR(100),
    postal_code     VARCHAR(20),
    country         VARCHAR(100) NOT NULL                                 DEFAULT 'Bangladesh',
    type            ENUM ('HOME', 'WORK', 'BILLING', 'SHIPPING', 'OTHER') DEFAULT 'HOME',
    created_dt      DATETIME                                              DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt DATETIME                                              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      BIGINT,
    updated_by      BIGINT,
    is_active       BOOLEAN                                               DEFAULT TRUE,
    version         INT                                                   DEFAULT 1
);

-- Create Hospital table
CREATE TABLE hospital
(
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    name              VARCHAR(255) NOT NULL,
    type              VARCHAR(100),
    address_string    VARCHAR(500),
    fk_address_id     BIGINT,
    phone             VARCHAR(20),
    email             VARCHAR(255),
    total_beds        INT         DEFAULT 0,
    available_beds    INT         DEFAULT 0,
    total_departments INT         DEFAULT 0,
    total_staff       INT         DEFAULT 0,
    established       VARCHAR(20),
    accreditation     VARCHAR(255),
    status            VARCHAR(50) DEFAULT 'ACTIVE',
    admin_id          BIGINT,
    contact           VARCHAR(100),
    created_dt        DATETIME    DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt   DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by        BIGINT,
    updated_by        BIGINT,
    is_active         BOOLEAN     DEFAULT TRUE,
    version           INT         DEFAULT 1,
    CONSTRAINT fk_hospital_address FOREIGN KEY (fk_address_id) REFERENCES address (id)
);

-- Create Department Type table
CREATE TABLE department_type
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(255) NOT NULL UNIQUE,
    code            VARCHAR(10)  NOT NULL UNIQUE,
    description     VARCHAR(500),
    is_active       BOOLEAN  DEFAULT TRUE,
    created_dt      DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create Department table
CREATE TABLE department
(
    id                    BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_hospital_id        BIGINT       NOT NULL,
    fk_department_type_id BIGINT,
    name                  VARCHAR(255) NOT NULL,
    description           VARCHAR(500),
    head_of_department_id BIGINT,
    total_staff           INT      DEFAULT 0,
    total_beds            INT      DEFAULT 0,
    contact_phone         VARCHAR(20),
    contact_email         VARCHAR(255),
    location_floor        VARCHAR(50),
    is_emergency          BOOLEAN  DEFAULT FALSE,
    operating_hours       VARCHAR(100),
    created_dt            DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by            BIGINT,
    updated_by            BIGINT,
    is_active             BOOLEAN  DEFAULT TRUE,
    version               INT      DEFAULT 1,
    CONSTRAINT fk_department_hospital FOREIGN KEY (fk_hospital_id) REFERENCES hospital (id),
    CONSTRAINT fk_department_type FOREIGN KEY (fk_department_type_id) REFERENCES department_type (id)
);

-- Create User table (updated with all missing columns from entity class)
CREATE TABLE user
(
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    name                VARCHAR(255)                                                                         NOT NULL,
    email               VARCHAR(255) UNIQUE                                                                  NOT NULL,
    password_hash       VARCHAR(255)                                                                         NOT NULL,
    phone               VARCHAR(20),
    national_id         VARCHAR(50) UNIQUE,
    role                ENUM ('SUPER_ADMIN', 'HOSPITAL_ADMIN', 'DOCTOR', 'NURSE', 'RECEPTIONIST', 'PATIENT') NOT NULL,
    fk_hospital_id      BIGINT,
    fk_department_id    BIGINT,
    fk_address_id       BIGINT,
    status              ENUM ('ACTIVE', 'INACTIVE', 'SUSPENDED') DEFAULT 'ACTIVE',
    specialization      VARCHAR(255),
    license_number      VARCHAR(100),
    years_of_experience INT,
    qualification       VARCHAR(500),
    consultation_fee    DECIMAL(10, 2),
    password_reset_dt   DATETIME,
    last_login_dt       DATETIME,
    created_dt          DATETIME                                 DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt     DATETIME                                 DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by          BIGINT,
    updated_by          BIGINT,
    is_active           BOOLEAN                                  DEFAULT TRUE,
    version             INT                                      DEFAULT 1,
    CONSTRAINT fk_user_hospital FOREIGN KEY (fk_hospital_id) REFERENCES hospital (id),
    CONSTRAINT fk_user_department FOREIGN KEY (fk_department_id) REFERENCES department (id),
    CONSTRAINT fk_user_address FOREIGN KEY (fk_address_id) REFERENCES address (id)
);

-- Create Patient table
CREATE TABLE patient
(
    id                         BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name                 VARCHAR(100) NOT NULL,
    last_name                  VARCHAR(100) NOT NULL,
    national_id                VARCHAR(50) UNIQUE,
    dob                        DATE,
    gender                     ENUM ('MALE', 'FEMALE', 'OTHER')         DEFAULT 'OTHER',
    contact                    VARCHAR(100),
    email                      VARCHAR(255),
    blood_group                VARCHAR(10),
    emergency_contact_name     VARCHAR(255),
    emergency_contact_relation VARCHAR(100),
    emergency_contact_phone    VARCHAR(20),
    medical_history            TEXT,
    allergies                  TEXT,
    current_medications        TEXT,
    occupation                 VARCHAR(255),
    marital_status             VARCHAR(50),
    fk_address_id              BIGINT,
    status                     ENUM ('ACTIVE', 'INACTIVE', 'SUSPENDED') DEFAULT 'ACTIVE',
    created_dt                 DATETIME                                 DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt            DATETIME                                 DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by                 BIGINT,
    updated_by                 BIGINT,
    is_active                  BOOLEAN                                  DEFAULT TRUE,
    version                    INT                                      DEFAULT 1,
    CONSTRAINT fk_patient_address FOREIGN KEY (fk_address_id) REFERENCES address (id)
);

-- Create Patient Hospital table (for multi-hospital patient history)
CREATE TABLE patient_hospital
(
    id                        BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_patient_id             BIGINT NOT NULL,
    fk_hospital_id            BIGINT NOT NULL,
    registration_date         DATE                                                     DEFAULT (CURRENT_DATE),
    patient_id_number         VARCHAR(50),
    status                    ENUM ('ACTIVE', 'INACTIVE', 'TRANSFERRED', 'DISCHARGED') DEFAULT 'ACTIVE',
    primary_hospital          BOOLEAN                                                  DEFAULT FALSE,
    referred_by               VARCHAR(255),
    referred_from_hospital_id BIGINT,
    discharge_date            DATE,
    notes                     TEXT,
    created_dt                DATETIME                                                 DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt           DATETIME                                                 DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by                BIGINT,
    updated_by                BIGINT,
    is_active                 BOOLEAN                                                  DEFAULT TRUE,
    version                   INT                                                      DEFAULT 1,
    CONSTRAINT fk_patient_hospital_patient FOREIGN KEY (fk_patient_id) REFERENCES patient (id),
    CONSTRAINT fk_patient_hospital_hospital FOREIGN KEY (fk_hospital_id) REFERENCES hospital (id),
    CONSTRAINT fk_patient_hospital_referred_from FOREIGN KEY (referred_from_hospital_id) REFERENCES hospital (id),
    UNIQUE KEY unique_patient_hospital (fk_patient_id, fk_hospital_id)
);

-- Create Insurance table
CREATE TABLE insurance
(
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    provider          VARCHAR(255) NOT NULL,
    policy_number     VARCHAR(100) NOT NULL,
    group_number      VARCHAR(100),
    coverage_type     VARCHAR(100),
    coverage_amount   DECIMAL(15, 2),
    deductible_amount DECIMAL(10, 2),
    valid_from        DATE,
    valid_to          DATE,
    created_dt        DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by        BIGINT,
    updated_by        BIGINT,
    is_active         BOOLEAN  DEFAULT TRUE,
    version           INT      DEFAULT 1
);

-- Create Patient Insurance table
CREATE TABLE patient_insurance
(
    id                      BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_patient_id           BIGINT NOT NULL,
    fk_insurance_id         BIGINT NOT NULL,
    is_primary              BOOLEAN  DEFAULT FALSE,
    relationship_to_insured VARCHAR(100),
    member_id               VARCHAR(100),
    created_dt              DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by              BIGINT,
    updated_by              BIGINT,
    is_active               BOOLEAN  DEFAULT TRUE,
    version                 INT      DEFAULT 1,
    CONSTRAINT fk_patient_insurance_patient FOREIGN KEY (fk_patient_id) REFERENCES patient (id),
    CONSTRAINT fk_patient_insurance_insurance FOREIGN KEY (fk_insurance_id) REFERENCES insurance (id)
);

-- Create Appointment table (updated with enhanced columns from migration files)
CREATE TABLE appointment
(
    id                    BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_patient_id         BIGINT   NOT NULL,
    fk_doctor_id          BIGINT   NOT NULL,
    fk_department_id      BIGINT   NOT NULL,
    fk_hospital_id        BIGINT   NOT NULL,
    start_time            DATETIME NOT NULL,
    end_time              DATETIME NOT NULL,
    duration_minutes      INT                                                                      DEFAULT 30,
    appointment_type      ENUM ('CONSULTATION', 'FOLLOW_UP', 'EMERGENCY', 'SURGERY', 'ROUTINE_CHECKUP', 'SURGICAL_CONSULTATION', 'DIAGNOSTIC') DEFAULT 'CONSULTATION',
    status                ENUM ('SCHEDULED', 'CONFIRMED', 'CANCELLED', 'COMPLETED', 'NO_SHOW', 'IN_PROGRESS', 'RESCHEDULED')     DEFAULT 'SCHEDULED',
    notes                 TEXT,
    reason                TEXT,
    symptoms              TEXT,
    vital_signs           TEXT,
    cancellation_reason   TEXT,
    reminder_sent         BOOLEAN                                                                  DEFAULT FALSE,
    reminder_sent_at      DATETIME,
    checked_in_at         DATETIME,
    completed_at          DATETIME,
    cancelled_at          DATETIME,
    is_recurring          BOOLEAN                                                                  DEFAULT FALSE,
    recurring_pattern     VARCHAR(255),
    parent_appointment_id BIGINT,
    created_dt            DATETIME                                                                 DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt       DATETIME                                                                 DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by            BIGINT,
    updated_by            BIGINT,
    is_active             BOOLEAN                                                                  DEFAULT TRUE,
    version               INT                                                                      DEFAULT 1,
    CONSTRAINT fk_appointment_patient FOREIGN KEY (fk_patient_id) REFERENCES patient (id),
    CONSTRAINT fk_appointment_doctor FOREIGN KEY (fk_doctor_id) REFERENCES user (id),
    CONSTRAINT fk_appointment_department FOREIGN KEY (fk_department_id) REFERENCES department (id),
    CONSTRAINT fk_appointment_hospital FOREIGN KEY (fk_hospital_id) REFERENCES hospital (id),
    CONSTRAINT fk_appointment_parent FOREIGN KEY (parent_appointment_id) REFERENCES appointment (id)
);

-- Create EHR (Electronic Health Record) table
CREATE TABLE ehr
(
    id                         BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_patient_id              BIGINT NOT NULL,
    fk_doctor_id               BIGINT NOT NULL,
    fk_department_id           BIGINT NOT NULL,
    fk_hospital_id             BIGINT NOT NULL,
    fk_appointment_id          BIGINT,
    visit_date                 DATE   NOT NULL,
    visit_type                 VARCHAR(100),
    chief_complaint            TEXT,
    history_of_present_illness TEXT,
    physical_examination       TEXT,
    vital_signs                TEXT,
    diagnosis                  TEXT,
    treatment_plan             TEXT,
    medications                TEXT,
    lab_results                TEXT,
    imaging_results            TEXT,
    follow_up_instructions     TEXT,
    created_dt                 DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt            DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by                 BIGINT,
    updated_by                 BIGINT,
    is_active                  BOOLEAN  DEFAULT TRUE,
    version                    INT      DEFAULT 1,
    CONSTRAINT fk_ehr_patient FOREIGN KEY (fk_patient_id) REFERENCES patient (id),
    CONSTRAINT fk_ehr_doctor FOREIGN KEY (fk_doctor_id) REFERENCES user (id),
    CONSTRAINT fk_ehr_department FOREIGN KEY (fk_department_id) REFERENCES department (id),
    CONSTRAINT fk_ehr_hospital FOREIGN KEY (fk_hospital_id) REFERENCES hospital (id),
    CONSTRAINT fk_ehr_appointment FOREIGN KEY (fk_appointment_id) REFERENCES appointment (id)
);

-- Create Prescription table
CREATE TABLE prescription
(
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_patient_id      BIGINT       NOT NULL,
    fk_doctor_id       BIGINT       NOT NULL,
    fk_hospital_id     BIGINT       NOT NULL,
    fk_appointment_id  BIGINT,
    fk_ehr_id          BIGINT,
    prescription_date  DATE         NOT NULL,
    medication_name    VARCHAR(255) NOT NULL,
    dosage             VARCHAR(100),
    frequency          VARCHAR(100),
    duration           VARCHAR(100),
    instructions       TEXT,
    notes              TEXT,
    status             ENUM ('ACTIVE', 'COMPLETED', 'CANCELLED', 'EXPIRED') DEFAULT 'ACTIVE',
    quantity           INT,
    refills_allowed    INT          DEFAULT 0,
    is_generic_allowed BOOLEAN      DEFAULT TRUE,
    created_dt         DATETIME     DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by         BIGINT,
    updated_by         BIGINT,
    is_active          BOOLEAN      DEFAULT TRUE,
    version            INT          DEFAULT 1,
    CONSTRAINT fk_prescription_patient FOREIGN KEY (fk_patient_id) REFERENCES patient (id),
    CONSTRAINT fk_prescription_doctor FOREIGN KEY (fk_doctor_id) REFERENCES user (id),
    CONSTRAINT fk_prescription_hospital FOREIGN KEY (fk_hospital_id) REFERENCES hospital (id),
    CONSTRAINT fk_prescription_appointment FOREIGN KEY (fk_appointment_id) REFERENCES appointment (id),
    CONSTRAINT fk_prescription_ehr FOREIGN KEY (fk_ehr_id) REFERENCES ehr (id),
    INDEX idx_prescription_patient (fk_patient_id),
    INDEX idx_prescription_doctor (fk_doctor_id),
    INDEX idx_prescription_hospital (fk_hospital_id),
    INDEX idx_prescription_appointment (fk_appointment_id),
    INDEX idx_prescription_status (status),
    INDEX idx_prescription_date (prescription_date)
);

-- Create Billing table
CREATE TABLE billing
(
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_patient_id      BIGINT              NOT NULL,
    fk_hospital_id     BIGINT              NOT NULL,
    fk_appointment_id  BIGINT,
    bill_number        VARCHAR(100) UNIQUE NOT NULL,
    bill_date          DATE                NOT NULL,
    due_date           DATE,
    total_amount       DECIMAL(15, 2)      NOT NULL,
    discount_amount    DECIMAL(15, 2)                                                           DEFAULT 0,
    tax_amount         DECIMAL(15, 2)                                                           DEFAULT 0,
    net_amount         DECIMAL(15, 2)      NOT NULL,
    paid_amount        DECIMAL(15, 2)                                                                       DEFAULT 0,
    outstanding_amount DECIMAL(15, 2)      NOT NULL,
    payment_method     ENUM ('CASH', 'CARD', 'INSURANCE', 'BANK_TRANSFER', 'MOBILE_PAYMENT', 'CHECK', 'OTHER') DEFAULT 'CASH',
    status             ENUM ('DRAFT', 'SENT', 'PAID', 'PARTIALLY_PAID', 'OVERDUE', 'CANCELLED', 'REFUNDED')    DEFAULT 'DRAFT',
    notes              TEXT,
    created_dt         DATETIME                                                                 DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt    DATETIME                                                                 DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by         BIGINT,
    updated_by         BIGINT,
    is_active          BOOLEAN                                                                  DEFAULT TRUE,
    version            INT                                                                      DEFAULT 1,
    CONSTRAINT fk_billing_patient FOREIGN KEY (fk_patient_id) REFERENCES patient (id),
    CONSTRAINT fk_billing_hospital FOREIGN KEY (fk_hospital_id) REFERENCES hospital (id),
    CONSTRAINT fk_billing_appointment FOREIGN KEY (fk_appointment_id) REFERENCES appointment (id)
);

-- Create Payment table
CREATE TABLE payment
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_billing_id    BIGINT                                                                NOT NULL,
    fk_patient_id    BIGINT                                                                            NOT NULL,
    payment_date     DATE                                                                              NOT NULL,
    amount           DECIMAL(15, 2)                                                                    NOT NULL,
    payment_method   ENUM ('CASH', 'CARD', 'INSURANCE', 'BANK_TRANSFER', 'MOBILE_PAYMENT', 'CHECK', 'OTHER') NOT NULL,
    transaction_id   VARCHAR(255),
    reference_number VARCHAR(255),
    notes            TEXT,
    created_dt       DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by       BIGINT,
    updated_by       BIGINT,
    is_active        BOOLEAN  DEFAULT TRUE,
    version          INT      DEFAULT 1,
    CONSTRAINT fk_payment_billing FOREIGN KEY (fk_billing_id) REFERENCES billing (id),
    CONSTRAINT fk_payment_patient FOREIGN KEY (fk_patient_id) REFERENCES patient (id)
);

-- Create Admission table
CREATE TABLE admission
(
    id                     BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_patient_id          BIGINT   NOT NULL,
    fk_hospital_id         BIGINT   NOT NULL,
    fk_department_id       BIGINT   NOT NULL,
    fk_attending_doctor_id BIGINT   NOT NULL,
    admission_date         DATETIME NOT NULL,
    discharge_date         DATETIME,
    bed_number             VARCHAR(50),
    room_number            VARCHAR(50),
    admission_type         VARCHAR(100),
    reason_for_admission   TEXT,
    discharge_summary      TEXT,
    status                 ENUM ('ADMITTED', 'DISCHARGED', 'TRANSFERRED') DEFAULT 'ADMITTED',
    created_dt             DATETIME                                       DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt        DATETIME                                       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by             BIGINT,
    updated_by             BIGINT,
    is_active              BOOLEAN                                        DEFAULT TRUE,
    version                INT                                            DEFAULT 1,
    CONSTRAINT fk_admission_patient FOREIGN KEY (fk_patient_id) REFERENCES patient (id),
    CONSTRAINT fk_admission_hospital FOREIGN KEY (fk_hospital_id) REFERENCES hospital (id),
    CONSTRAINT fk_admission_department FOREIGN KEY (fk_department_id) REFERENCES department (id),
    CONSTRAINT fk_admission_doctor FOREIGN KEY (fk_attending_doctor_id) REFERENCES user (id)
);

-- Create Shift table
CREATE TABLE shift
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_user_id       BIGINT NOT NULL,
    fk_hospital_id   BIGINT NOT NULL,
    fk_department_id BIGINT,
    shift_date       DATE   NOT NULL,
    start_time       TIME   NOT NULL,
    end_time         TIME   NOT NULL,
    shift_type       VARCHAR(50),
    status           ENUM ('SCHEDULED', 'CONFIRMED', 'CANCELLED', 'COMPLETED') DEFAULT 'SCHEDULED',
    notes            TEXT,
    created_dt       DATETIME                                                  DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt  DATETIME                                                  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by       BIGINT,
    updated_by       BIGINT,
    is_active        BOOLEAN                                                   DEFAULT TRUE,
    version          INT                                                       DEFAULT 1,
    CONSTRAINT fk_shift_user FOREIGN KEY (fk_user_id) REFERENCES user (id),
    CONSTRAINT fk_shift_hospital FOREIGN KEY (fk_hospital_id) REFERENCES hospital (id),
    CONSTRAINT fk_shift_department FOREIGN KEY (fk_department_id) REFERENCES department (id)
);

-- Create Attachment table
CREATE TABLE attachment
(
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_patient_id     BIGINT,
    fk_appointment_id BIGINT,
    fk_ehr_id         BIGINT,
    uploaded_by_id    BIGINT       NOT NULL,
    file_name         VARCHAR(255) NOT NULL,
    file_path         VARCHAR(500) NOT NULL,
    file_size         BIGINT,
    file_type         VARCHAR(100),
    attachment_type   ENUM ('LAB_REPORT', 'IMAGING', 'PRESCRIPTION', 'INSURANCE_DOCUMENT', 'OTHER') DEFAULT 'OTHER',
    description       TEXT,
    upload_date       DATETIME                                                                      DEFAULT CURRENT_TIMESTAMP,
    created_dt        DATETIME                                                                      DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt   DATETIME                                                                      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by        BIGINT,
    updated_by        BIGINT,
    is_active         BOOLEAN                                                                       DEFAULT TRUE,
    version           INT                                                                           DEFAULT 1,
    CONSTRAINT fk_attachment_patient FOREIGN KEY (fk_patient_id) REFERENCES patient (id),
    CONSTRAINT fk_attachment_appointment FOREIGN KEY (fk_appointment_id) REFERENCES appointment (id),
    CONSTRAINT fk_attachment_ehr FOREIGN KEY (fk_ehr_id) REFERENCES ehr (id),
    CONSTRAINT fk_attachment_uploaded_by FOREIGN KEY (uploaded_by_id) REFERENCES user (id)
);

-- Create Audit Log table
CREATE TABLE audit_log
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_user_id BIGINT,
    table_name VARCHAR(100) NOT NULL,
    record_id  BIGINT       NOT NULL,
    action     VARCHAR(50)  NOT NULL,
    old_values TEXT,
    new_values TEXT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_log_user FOREIGN KEY (fk_user_id) REFERENCES user (id)
);

-- Create Indexes for better performance
CREATE INDEX idx_hospital_name ON hospital (name);
CREATE INDEX idx_hospital_status ON hospital (status);

CREATE INDEX idx_department_hospital ON department (fk_hospital_id);
CREATE INDEX idx_department_type ON department (fk_department_type_id);
CREATE INDEX idx_department_active ON department (is_active);

CREATE INDEX idx_user_email ON user (email);
CREATE INDEX idx_user_hospital_role ON user (fk_hospital_id, role);
CREATE INDEX idx_user_national_id ON user (national_id);
CREATE INDEX idx_user_phone ON user (phone);
CREATE INDEX idx_user_role ON user (role);
CREATE INDEX idx_user_status ON user (status);

CREATE INDEX idx_patient_name ON patient (last_name, first_name);
CREATE INDEX idx_patient_dob ON patient (dob);
CREATE INDEX idx_patient_national_id ON patient (national_id);
CREATE INDEX idx_patient_contact ON patient (contact);
CREATE INDEX idx_patient_status ON patient (status);

CREATE INDEX idx_patient_hospital_patient ON patient_hospital (fk_patient_id);
CREATE INDEX idx_patient_hospital_hospital ON patient_hospital (fk_hospital_id);
CREATE INDEX idx_patient_hospital_status ON patient_hospital (status);

CREATE INDEX idx_appointment_doctor_datetime ON appointment (fk_doctor_id, start_time);
CREATE INDEX idx_appointment_patient_datetime ON appointment (fk_patient_id, start_time);
CREATE INDEX idx_appointment_hospital_dept_datetime ON appointment (fk_hospital_id, fk_department_id, start_time);
CREATE INDEX idx_appointment_status ON appointment (status);
CREATE INDEX idx_appointment_date ON appointment (start_time);

CREATE INDEX idx_ehr_patient ON ehr (fk_patient_id);
CREATE INDEX idx_ehr_doctor ON ehr (fk_doctor_id);
CREATE INDEX idx_ehr_visit_date ON ehr (visit_date);
CREATE INDEX idx_ehr_hospital ON ehr (fk_hospital_id);

CREATE INDEX idx_billing_patient ON billing (fk_patient_id);
CREATE INDEX idx_billing_hospital ON billing (fk_hospital_id);
CREATE INDEX idx_billing_status ON billing (status);
CREATE INDEX idx_billing_date ON billing (bill_date);
CREATE INDEX idx_billing_number ON billing (bill_number);

CREATE INDEX idx_payment_billing ON payment (fk_billing_id);
CREATE INDEX idx_payment_date ON payment (payment_date);
CREATE INDEX idx_payment_method ON payment (payment_method);

CREATE INDEX idx_shift_user_date ON shift (fk_user_id, shift_date);
CREATE INDEX idx_shift_hospital ON shift (fk_hospital_id);
CREATE INDEX idx_shift_status ON shift (status);

CREATE INDEX idx_audit_log_table_record ON audit_log (table_name, record_id);
CREATE INDEX idx_audit_log_user ON audit_log (fk_user_id);
CREATE INDEX idx_audit_log_created ON audit_log (created_dt);


-- Success message
SELECT 'Hospital Management System database table successfully created!' as result;