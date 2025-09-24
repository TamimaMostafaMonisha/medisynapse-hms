CREATE TABLE address
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    line1           VARCHAR(255) NOT NULL,
    line2           VARCHAR(255),
    city            VARCHAR(100) NOT NULL,
    state           VARCHAR(100),
    postal_code     VARCHAR(20),
    country         VARCHAR(100) NOT NULL,
    type            ENUM ('HOME', 'WORK', 'BILLING', 'SHIPPING', 'OTHER') DEFAULT 'HOME',
    created_dt      DATETIME                                              DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt DATETIME                                              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      BIGINT,
    updated_by      BIGINT,
    is_active       BOOLEAN                                               DEFAULT TRUE,
    version         INT                                                   DEFAULT 1
);

CREATE TABLE hospital
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(255) NOT NULL,
    fk_address_id   BIGINT,
    contact         VARCHAR(100),
    created_dt      DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      BIGINT,
    updated_by      BIGINT,
    is_active       BOOLEAN  DEFAULT TRUE,
    version         INT      DEFAULT 1,
    CONSTRAINT fk_hospital_address FOREIGN KEY (fk_address_id) REFERENCES address (id)
);

CREATE TABLE department
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_hospital_id  BIGINT       NOT NULL,
    name            VARCHAR(255) NOT NULL,
    description     VARCHAR(255),
    created_dt      DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      BIGINT,
    updated_by      BIGINT,
    is_active       BOOLEAN  DEFAULT TRUE,
    version         INT      DEFAULT 1,
    CONSTRAINT fk_department_hospital FOREIGN KEY (fk_hospital_id) REFERENCES hospital (id)
);
CREATE INDEX idx_department_hospital ON department (fk_hospital_id);

CREATE TABLE user
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    name             VARCHAR(255)                                                                         NOT NULL,
    email            VARCHAR(255) UNIQUE                                                                  NOT NULL,
    password_hash    VARCHAR(255)                                                                         NOT NULL,
    national_id      VARCHAR(50) UNIQUE, -- National ID card number (NID)
    role             ENUM ('SUPER_ADMIN', 'HOSPITAL_ADMIN', 'DOCTOR', 'NURSE', 'RECEPTIONIST', 'PATIENT') NOT NULL,
    fk_hospital_id   BIGINT,
    fk_department_id BIGINT,
    fk_address_id    BIGINT,
    status           ENUM ('ACTIVE', 'INACTIVE', 'SUSPENDED') DEFAULT 'ACTIVE',
    created_dt       DATETIME                                 DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt  DATETIME                                 DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by       BIGINT,
    updated_by       BIGINT,
    is_active        BOOLEAN                                  DEFAULT TRUE,
    version          INT                                      DEFAULT 1,
    CONSTRAINT fk_user_hospital FOREIGN KEY (fk_hospital_id) REFERENCES hospital (id),
    CONSTRAINT fk_user_department FOREIGN KEY (fk_department_id) REFERENCES department (id),
    CONSTRAINT fk_user_address FOREIGN KEY (fk_address_id) REFERENCES address (id)
);
CREATE INDEX idx_user_hospital_role ON user (fk_hospital_id, role);
CREATE INDEX idx_user_national_id ON user (national_id);

CREATE TABLE patient
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    national_id     VARCHAR(50) UNIQUE, -- National ID card number (NID)
    dob             DATE,
    gender          ENUM ('MALE', 'FEMALE', 'OTHER')         DEFAULT 'OTHER',
    contact         VARCHAR(100),
    fk_address_id   BIGINT,
    status          ENUM ('ACTIVE', 'INACTIVE', 'SUSPENDED') DEFAULT 'ACTIVE',
    created_dt      DATETIME                                 DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt DATETIME                                 DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      BIGINT,
    updated_by      BIGINT,
    is_active       BOOLEAN                                  DEFAULT TRUE,
    version         INT                                      DEFAULT 1,
    CONSTRAINT fk_patient_address FOREIGN KEY (fk_address_id) REFERENCES address (id)
);
CREATE INDEX idx_patient_name ON patient (last_name, first_name);
CREATE INDEX idx_patient_dob ON patient (dob);
CREATE INDEX idx_patient_national_id ON patient (national_id);

-- New table to support patients taking treatment from multiple hospitals
CREATE TABLE patient_hospital
(
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_patient_id     BIGINT NOT NULL,
    fk_hospital_id    BIGINT NOT NULL,
    registration_date DATE                                       DEFAULT (CURRENT_DATE),
    patient_id_number VARCHAR(50), -- Hospital-specific patient ID
    status            ENUM ('ACTIVE', 'INACTIVE', 'TRANSFERRED') DEFAULT 'ACTIVE',
    created_dt        DATETIME                                   DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt   DATETIME                                   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by        BIGINT,
    updated_by        BIGINT,
    is_active         BOOLEAN                                    DEFAULT TRUE,
    version           INT                                        DEFAULT 1,
    CONSTRAINT fk_patient_hospital_patient FOREIGN KEY (fk_patient_id) REFERENCES patient (id),
    CONSTRAINT fk_patient_hospital_hospital FOREIGN KEY (fk_hospital_id) REFERENCES hospital (id),
    UNIQUE KEY unique_patient_hospital (fk_patient_id, fk_hospital_id)
);
CREATE INDEX idx_patient_hospital_patient ON patient_hospital (fk_patient_id);
CREATE INDEX idx_patient_hospital_hospital ON patient_hospital (fk_hospital_id);
CREATE INDEX idx_patient_hospital_status ON patient_hospital (status);

CREATE TABLE insurance
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    provider        VARCHAR(255) NOT NULL,
    policy_number   VARCHAR(100) NOT NULL,
    group_number    VARCHAR(100),
    valid_from      DATE,
    valid_to        DATE,
    created_dt      DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      BIGINT,
    updated_by      BIGINT,
    is_active       BOOLEAN  DEFAULT TRUE,
    version         INT      DEFAULT 1
);

CREATE TABLE patient_insurance
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_patient_id   BIGINT NOT NULL,
    fk_insurance_id BIGINT NOT NULL,
    is_primary      BOOLEAN  DEFAULT FALSE,
    created_dt      DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      BIGINT,
    updated_by      BIGINT,
    is_active       BOOLEAN  DEFAULT TRUE,
    version         INT      DEFAULT 1,
    CONSTRAINT fk_patient_insurance_patient FOREIGN KEY (fk_patient_id) REFERENCES patient (id),
    CONSTRAINT fk_patient_insurance_insurance FOREIGN KEY (fk_insurance_id) REFERENCES insurance (id)
);
CREATE INDEX idx_patient_insurance_patient ON patient_insurance (fk_patient_id);
CREATE INDEX idx_patient_insurance_insurance ON patient_insurance (fk_insurance_id);

CREATE TABLE appointment
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_patient_id    BIGINT   NOT NULL,
    fk_doctor_id     BIGINT   NOT NULL,
    fk_department_id BIGINT   NOT NULL,
    fk_hospital_id   BIGINT   NOT NULL,
    date_time        DATETIME NOT NULL,
    status           ENUM ('SCHEDULED', 'CONFIRMED', 'CANCELLED', 'COMPLETED', 'NO_SHOW') DEFAULT 'SCHEDULED',
    created_dt       DATETIME                                                             DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt  DATETIME                                                             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by       BIGINT,
    updated_by       BIGINT,
    is_active        BOOLEAN                                                              DEFAULT TRUE,
    version          INT                                                                  DEFAULT 1,
    CONSTRAINT fk_appointment_patient FOREIGN KEY (fk_patient_id) REFERENCES patient (id),
    CONSTRAINT fk_appointment_doctor FOREIGN KEY (fk_doctor_id) REFERENCES user (id),
    CONSTRAINT fk_appointment_department FOREIGN KEY (fk_department_id) REFERENCES department (id),
    CONSTRAINT fk_appointment_hospital FOREIGN KEY (fk_hospital_id) REFERENCES hospital (id)
);
CREATE INDEX idx_appointment_doctor_datetime ON appointment (fk_doctor_id, date_time);
CREATE INDEX idx_appointment_patient_datetime ON appointment (fk_patient_id, date_time);
CREATE INDEX idx_appointment_hospital_dept_datetime ON appointment (fk_hospital_id, fk_department_id, date_time);
CREATE INDEX idx_appointment_status ON appointment (status);

CREATE TABLE ehr
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_patient_id    BIGINT NOT NULL,
    fk_doctor_id     BIGINT NOT NULL,
    fk_department_id BIGINT NOT NULL,
    visit_date       DATE   NOT NULL,
    symptoms         VARCHAR(255),
    diagnosis        VARCHAR(255),
    notes            TEXT,
    created_dt       DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by       BIGINT,
    updated_by       BIGINT,
    is_active        BOOLEAN  DEFAULT TRUE,
    version          INT      DEFAULT 1,
    CONSTRAINT fk_ehr_patient FOREIGN KEY (fk_patient_id) REFERENCES patient (id),
    CONSTRAINT fk_ehr_doctor FOREIGN KEY (fk_doctor_id) REFERENCES user (id),
    CONSTRAINT fk_ehr_department FOREIGN KEY (fk_department_id) REFERENCES department (id)
);
CREATE INDEX idx_ehr_patient_visitdate ON ehr (fk_patient_id, visit_date);
CREATE INDEX idx_ehr_doctor_visitdate ON ehr (fk_doctor_id, visit_date);

CREATE TABLE prescription
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_ehr_id       BIGINT       NOT NULL,
    medication      VARCHAR(255) NOT NULL,
    dosage          VARCHAR(100),
    frequency       VARCHAR(100),
    duration        VARCHAR(100),
    created_dt      DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      BIGINT,
    updated_by      BIGINT,
    is_active       BOOLEAN  DEFAULT TRUE,
    version         INT      DEFAULT 1,
    CONSTRAINT fk_prescription_ehr FOREIGN KEY (fk_ehr_id) REFERENCES ehr (id)
);
CREATE INDEX idx_prescription_ehr ON prescription (fk_ehr_id);

CREATE TABLE billing
(
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_patient_id     BIGINT         NOT NULL,
    fk_appointment_id BIGINT,
    total_amount      DECIMAL(12, 2) NOT NULL,
    payment_method    ENUM ('CASH', 'CARD', 'INSURANCE', 'BANK_TRANSFER', 'OTHER'),
    status            ENUM ('PENDING', 'PAID', 'PARTIALLY_PAID', 'CANCELLED', 'REFUNDED') DEFAULT 'PENDING',
    created_dt        DATETIME                                                            DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt   DATETIME                                                            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by        BIGINT,
    updated_by        BIGINT,
    is_active         BOOLEAN                                                             DEFAULT TRUE,
    version           INT                                                                 DEFAULT 1,
    CONSTRAINT fk_billing_patient FOREIGN KEY (fk_patient_id) REFERENCES patient (id),
    CONSTRAINT fk_billing_appointment FOREIGN KEY (fk_appointment_id) REFERENCES appointment (id)
);
CREATE INDEX idx_billing_patient_appointment ON billing (fk_patient_id, fk_appointment_id);
CREATE INDEX idx_billing_status ON billing (status);

CREATE TABLE payment
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_billing_id   BIGINT         NOT NULL,
    amount          DECIMAL(12, 2) NOT NULL,
    payment_date    DATETIME       NOT NULL,
    payment_method  ENUM ('CASH', 'CARD', 'INSURANCE', 'BANK_TRANSFER', 'OTHER'),
    reference_no    VARCHAR(100),
    created_dt      DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      BIGINT,
    updated_by      BIGINT,
    is_active       BOOLEAN  DEFAULT TRUE,
    version         INT      DEFAULT 1,
    CONSTRAINT fk_payment_billing FOREIGN KEY (fk_billing_id) REFERENCES billing (id)
);
CREATE INDEX idx_payment_billing ON payment (fk_billing_id);
CREATE INDEX idx_payment_date ON payment (payment_date);


CREATE TABLE attachment
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_ehr_id       BIGINT       NOT NULL,
    file_path       VARCHAR(255) NOT NULL,
    type            ENUM ('IMAGE', 'PDF', 'DOCUMENT', 'LAB_REPORT', 'X_RAY', 'OTHER'),
    fk_uploaded_by  BIGINT       NOT NULL,
    created_dt      DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      BIGINT,
    updated_by      BIGINT,
    is_active       BOOLEAN  DEFAULT TRUE,
    version         INT      DEFAULT 1,
    CONSTRAINT fk_attachment_ehr FOREIGN KEY (fk_ehr_id) REFERENCES ehr (id),
    CONSTRAINT fk_attachment_uploaded_by FOREIGN KEY (fk_uploaded_by) REFERENCES user (id)
);
CREATE INDEX idx_attachment_ehr ON attachment (fk_ehr_id);
CREATE INDEX idx_attachment_uploaded_by ON attachment (fk_uploaded_by);


CREATE TABLE audit_log
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_user_id BIGINT,
    action     VARCHAR(100) NOT NULL,
    entity     VARCHAR(100) NOT NULL,
    entity_id  BIGINT,
    timestamp  DATETIME DEFAULT CURRENT_TIMESTAMP,
    details    TEXT,
    CONSTRAINT fk_audit_log_user FOREIGN KEY (fk_user_id) REFERENCES user (id)
);
CREATE INDEX idx_audit_log_user_timestamp ON audit_log (fk_user_id, timestamp);
CREATE INDEX idx_audit_log_entity ON audit_log (entity, entity_id);
