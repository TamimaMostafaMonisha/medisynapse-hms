-- Add missing columns to hospital table for frontend application mapping
-- Using individual ALTER TABLE statements for better MySQL version compatibility

ALTER TABLE hospital
    ADD COLUMN type VARCHAR(100);
ALTER TABLE hospital
    ADD COLUMN address TEXT;
ALTER TABLE hospital
    ADD COLUMN phone VARCHAR(20);
ALTER TABLE hospital
    ADD COLUMN email VARCHAR(100);
ALTER TABLE hospital
    ADD COLUMN total_beds INT;
ALTER TABLE hospital
    ADD COLUMN available_beds INT;
ALTER TABLE hospital
    ADD COLUMN total_departments INT;
ALTER TABLE hospital
    ADD COLUMN total_staff INT;
ALTER TABLE hospital
    ADD COLUMN established VARCHAR(4);
ALTER TABLE hospital
    ADD COLUMN accreditation VARCHAR(255);
ALTER TABLE hospital
    ADD COLUMN status VARCHAR(50) DEFAULT 'Active';
ALTER TABLE hospital
    ADD COLUMN admin_id BIGINT;

-- Update existing records with default values if needed
UPDATE hospital
SET type              = 'General Hospital',
    address           = 'Address Not Specified',
    phone             = '+1-000-0000',
    email             = 'info@hospital.com',
    total_beds        = 100,
    available_beds    = 50,
    total_departments = 5,
    total_staff       = 100,
    established       = '2000',
    accreditation     = 'Standard Accredited',
    status            = 'Active'
WHERE type IS NULL
   OR type = '';

-- ============================================================================
-- Frontend Schema Updates - Comprehensive Appointment Management & Enhancements
-- ============================================================================

-- Add department_type table if not exists
CREATE TABLE IF NOT EXISTS department_type
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(100) NOT NULL UNIQUE,
    description     TEXT,
    color_code      VARCHAR(7),  -- Hex color code for UI
    icon_name       VARCHAR(50), -- Icon identifier for UI
    created_dt      DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      BIGINT,
    updated_by      BIGINT,
    is_active       BOOLEAN  DEFAULT TRUE,
    version         INT      DEFAULT 1
);

-- Add foreign key relationship to department table
ALTER TABLE department
    ADD COLUMN IF NOT EXISTS fk_department_type_id BIGINT,
    ADD CONSTRAINT IF NOT EXISTS fk_department_type FOREIGN KEY (fk_department_type_id) REFERENCES department_type (id);

CREATE INDEX IF NOT EXISTS idx_department_type ON department (fk_department_type_id);

-- Add enhanced columns to user table for better user management
ALTER TABLE user
    ADD COLUMN IF NOT EXISTS phone VARCHAR (20) COMMENT 'Phone number for the user',
    ADD COLUMN IF NOT EXISTS password_reset_dt DATETIME COMMENT 'Timestamp when password was last reset',
    ADD COLUMN IF NOT EXISTS last_login_dt DATETIME COMMENT 'Timestamp when user last logged in';

-- Add indexes for user enhancements
CREATE INDEX IF NOT EXISTS idx_user_phone ON user (phone);
CREATE INDEX IF NOT EXISTS idx_user_password_reset ON user (password_reset_dt);
CREATE INDEX IF NOT EXISTS idx_user_last_login ON user (last_login_dt);

-- Enhanced appointment management columns
ALTER TABLE appointment
    ADD COLUMN IF NOT EXISTS start_time DATETIME,
    ADD COLUMN IF NOT EXISTS end_time DATETIME,
    ADD COLUMN IF NOT EXISTS duration_minutes INT DEFAULT 30,
    ADD COLUMN IF NOT EXISTS appointment_type VARCHAR (50) DEFAULT 'CONSULTATION',
    ADD COLUMN IF NOT EXISTS notes TEXT,
    ADD COLUMN IF NOT EXISTS reason TEXT,
    ADD COLUMN IF NOT EXISTS cancellation_reason TEXT,
    ADD COLUMN IF NOT EXISTS reminder_sent BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS reminder_sent_at DATETIME,
    ADD COLUMN IF NOT EXISTS checked_in_at DATETIME,
    ADD COLUMN IF NOT EXISTS completed_at DATETIME,
    ADD COLUMN IF NOT EXISTS cancelled_at DATETIME,
    ADD COLUMN IF NOT EXISTS is_recurring BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS recurring_pattern VARCHAR (255),
    ADD COLUMN IF NOT EXISTS parent_appointment_id BIGINT;

-- Update existing appointment data
UPDATE appointment
SET start_time = date_time,
    end_time   = DATE_ADD(date_time, INTERVAL 30 MINUTE)
WHERE start_time IS NULL;

-- Add appointment indexes for performance
CREATE INDEX IF NOT EXISTS idx_appointment_start_time ON appointment(start_time);
CREATE INDEX IF NOT EXISTS idx_appointment_end_time ON appointment(end_time);
CREATE INDEX IF NOT EXISTS idx_appointment_doctor_date ON appointment(fk_doctor_id, DATE (start_time));
CREATE INDEX IF NOT EXISTS idx_appointment_patient_hospital ON appointment(fk_patient_id, fk_hospital_id);
CREATE INDEX IF NOT EXISTS idx_appointment_status_active ON appointment(status, is_active);
CREATE INDEX IF NOT EXISTS idx_appointment_type ON appointment(appointment_type);
CREATE INDEX IF NOT EXISTS idx_appointment_hospital_date ON appointment(fk_hospital_id, DATE (start_time));

-- Add foreign key for parent appointment
ALTER TABLE appointment
    ADD CONSTRAINT IF NOT EXISTS fk_appointment_parent
    FOREIGN KEY (parent_appointment_id) REFERENCES appointment(id);

-- Create appointment_history table for tracking changes
CREATE TABLE IF NOT EXISTS appointment_history
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_id BIGINT NOT NULL,
    old_start_time DATETIME,
    new_start_time DATETIME,
    old_status     VARCHAR(50),
    new_status     VARCHAR(50),
    change_reason  TEXT,
    changed_by     BIGINT,
    changed_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_id) REFERENCES appointment (id),
    FOREIGN KEY (changed_by) REFERENCES user (id)
);

-- Create appointment_reminders table
CREATE TABLE IF NOT EXISTS appointment_reminders
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_id    BIGINT      NOT NULL,
    reminder_type     VARCHAR(20) NOT NULL,          -- EMAIL, SMS, PUSH
    scheduled_time    DATETIME    NOT NULL,
    sent_time         DATETIME,
    status            VARCHAR(20) DEFAULT 'PENDING', -- PENDING, SENT, FAILED
    recipient_contact VARCHAR(255),
    message_content   TEXT,
    created_at        DATETIME    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_id) REFERENCES appointment (id)
);

-- Create waiting_list table
CREATE TABLE IF NOT EXISTS appointment_waiting_list
(
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id           BIGINT NOT NULL,
    doctor_id            BIGINT NOT NULL,
    hospital_id          BIGINT NOT NULL,
    preferred_date       DATE,
    preferred_time_start TIME,
    preferred_time_end   TIME,
    appointment_type     VARCHAR(50) DEFAULT 'CONSULTATION',
    priority_level       INT         DEFAULT 1,        -- 1=Low, 2=Medium, 3=High, 4=Urgent
    reason               TEXT,
    status               VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, MATCHED, CANCELLED
    created_at           DATETIME    DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patient (id),
    FOREIGN KEY (doctor_id) REFERENCES user (id),
    FOREIGN KEY (hospital_id) REFERENCES hospital (id)
);

-- Create doctor_schedule table
CREATE TABLE IF NOT EXISTS doctor_schedule
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id        BIGINT NOT NULL,
    day_of_week      INT    NOT NULL, -- 1=Monday, 2=Tuesday, etc.
    start_time       TIME   NOT NULL,
    end_time         TIME   NOT NULL,
    break_start_time TIME,
    break_end_time   TIME,
    is_active        BOOLEAN  DEFAULT TRUE,
    effective_from   DATE,
    effective_to     DATE,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES user (id),
    UNIQUE KEY unique_doctor_day (doctor_id, day_of_week, effective_from)
);

-- Create doctor_availability_exceptions table
CREATE TABLE IF NOT EXISTS doctor_availability_exceptions
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id          BIGINT      NOT NULL,
    exception_date     DATE        NOT NULL,
    start_time         TIME,
    end_time           TIME,
    exception_type     VARCHAR(30) NOT NULL, -- UNAVAILABLE, BREAK, MEETING, VACATION
    reason             TEXT,
    is_recurring       BOOLEAN  DEFAULT FALSE,
    recurrence_pattern VARCHAR(100),
    created_by         BIGINT,
    created_at         DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES user (id),
    FOREIGN KEY (created_by) REFERENCES user (id)
);

-- Create appointment_analytics table
CREATE TABLE IF NOT EXISTS appointment_analytics
(
    id                       BIGINT AUTO_INCREMENT PRIMARY KEY,
    hospital_id              BIGINT NOT NULL,
    department_id            BIGINT,
    doctor_id                BIGINT,
    analytics_date           DATE   NOT NULL,
    total_appointments       INT      DEFAULT 0,
    scheduled_appointments   INT      DEFAULT 0,
    completed_appointments   INT      DEFAULT 0,
    cancelled_appointments   INT      DEFAULT 0,
    no_show_appointments     INT      DEFAULT 0,
    average_duration_minutes DECIMAL(5, 2),
    utilization_rate         DECIMAL(5, 2), -- percentage of available slots used
    created_at               DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at               DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (hospital_id) REFERENCES hospital (id),
    FOREIGN KEY (department_id) REFERENCES department (id),
    FOREIGN KEY (doctor_id) REFERENCES user (id),
    UNIQUE KEY unique_analytics (hospital_id, department_id, doctor_id, analytics_date)
);

-- Add indexes for analytics table
CREATE INDEX IF NOT EXISTS idx_analytics_hospital_date ON appointment_analytics(hospital_id, analytics_date);
CREATE INDEX IF NOT EXISTS idx_analytics_doctor_date ON appointment_analytics(doctor_id, analytics_date);
CREATE INDEX IF NOT EXISTS idx_analytics_department_date ON appointment_analytics(department_id, analytics_date);

-- Create a view for appointment statistics
CREATE OR REPLACE VIEW appointment_statistics_view AS
SELECT a.fk_hospital_id                                        as hospital_id,
       h.name                                                  as hospital_name,
       a.fk_department_id                                      as department_id,
       d.name                                                  as department_name,
       a.fk_doctor_id                                          as doctor_id,
       u.name                                                  as doctor_name,
       DATE(a.start_time)                                      as appointment_date,
       COUNT(*)                                                as total_appointments,
       SUM(CASE WHEN a.status = 'SCHEDULED' THEN 1 ELSE 0 END) as scheduled_count,
       SUM(CASE WHEN a.status = 'CONFIRMED' THEN 1 ELSE 0 END) as confirmed_count,
       SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_count,
       SUM(CASE WHEN a.status = 'CANCELLED' THEN 1 ELSE 0 END) as cancelled_count,
       SUM(CASE WHEN a.status = 'NO_SHOW' THEN 1 ELSE 0 END)   as no_show_count,
       AVG(a.duration_minutes)                                 as avg_duration_minutes,
       COUNT(DISTINCT a.fk_patient_id)                         as unique_patients
FROM appointment a
         JOIN hospital h ON a.fk_hospital_id = h.id
         LEFT JOIN department d ON a.fk_department_id = d.id
         JOIN user u ON a.fk_doctor_id = u.id
WHERE a.is_active = TRUE
GROUP BY a.fk_hospital_id, a.fk_department_id, a.fk_doctor_id, DATE(a.start_time);
