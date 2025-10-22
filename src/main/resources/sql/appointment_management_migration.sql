-- Database migration script for comprehensive appointment management system
-- This script adds new columns to support enhanced appointment functionality

-- Add new columns to appointment table for comprehensive appointment management
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

-- Update existing data: copy date_time to start_time if start_time is null
UPDATE appointment
SET start_time = date_time,
    end_time   = DATE_ADD(date_time, INTERVAL 30 MINUTE)
WHERE start_time IS NULL;

-- Add indexes for performance optimization
CREATE INDEX IF NOT EXISTS idx_appointment_start_time ON appointment(start_time);
CREATE INDEX IF NOT EXISTS idx_appointment_end_time ON appointment(end_time);
CREATE INDEX IF NOT EXISTS idx_appointment_doctor_date ON appointment(fk_doctor_id, DATE (start_time));
CREATE INDEX IF NOT EXISTS idx_appointment_patient_hospital ON appointment(fk_patient_id, fk_hospital_id);
CREATE INDEX IF NOT EXISTS idx_appointment_status_active ON appointment(status, is_active);
CREATE INDEX IF NOT EXISTS idx_appointment_type ON appointment(appointment_type);
CREATE INDEX IF NOT EXISTS idx_appointment_hospital_date ON appointment(fk_hospital_id, DATE (start_time));

-- Add foreign key constraint for parent appointment (self-referencing)
ALTER TABLE appointment
    ADD CONSTRAINT fk_appointment_parent
        FOREIGN KEY (parent_appointment_id) REFERENCES appointment (id);

-- Update appointment status enum to include new statuses
-- Note: This might need to be done manually depending on your database system
-- ALTER TABLE appointment MODIFY COLUMN status ENUM('SCHEDULED', 'CONFIRMED', 'CANCELLED', 'COMPLETED', 'NO_SHOW', 'IN_PROGRESS', 'RESCHEDULED');

-- Create appointment_history table for tracking changes
CREATE TABLE IF NOT EXISTS appointment_history
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    appointment_id
    BIGINT
    NOT
    NULL,
    old_start_time
    DATETIME,
    new_start_time
    DATETIME,
    old_status
    VARCHAR
(
    50
),
    new_status VARCHAR
(
    50
),
    change_reason TEXT,
    changed_by BIGINT,
    changed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY
(
    appointment_id
) REFERENCES appointment
(
    id
),
    FOREIGN KEY
(
    changed_by
) REFERENCES user
(
    id
)
    );

-- Create appointment_reminders table for managing reminder notifications
CREATE TABLE IF NOT EXISTS appointment_reminders
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    appointment_id
    BIGINT
    NOT
    NULL,
    reminder_type
    VARCHAR
(
    20
) NOT NULL, -- EMAIL, SMS, PUSH
    scheduled_time DATETIME NOT NULL,
    sent_time DATETIME,
    status VARCHAR
(
    20
) DEFAULT 'PENDING', -- PENDING, SENT, FAILED
    recipient_contact VARCHAR
(
    255
),
    message_content TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY
(
    appointment_id
) REFERENCES appointment
(
    id
)
    );

-- Create waiting_list table for managing appointment waiting lists
CREATE TABLE IF NOT EXISTS appointment_waiting_list
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    patient_id
    BIGINT
    NOT
    NULL,
    doctor_id
    BIGINT
    NOT
    NULL,
    hospital_id
    BIGINT
    NOT
    NULL,
    preferred_date
    DATE,
    preferred_time_start
    TIME,
    preferred_time_end
    TIME,
    appointment_type
    VARCHAR
(
    50
) DEFAULT 'CONSULTATION',
    priority_level INT DEFAULT 1, -- 1=Low, 2=Medium, 3=High, 4=Urgent
    reason TEXT,
    status VARCHAR
(
    20
) DEFAULT 'ACTIVE', -- ACTIVE, MATCHED, CANCELLED
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY
(
    patient_id
) REFERENCES patient
(
    id
),
    FOREIGN KEY
(
    doctor_id
) REFERENCES user
(
    id
),
    FOREIGN KEY
(
    hospital_id
) REFERENCES hospital
(
    id
)
    );

-- Create doctor_schedule table for managing doctor working hours
CREATE TABLE IF NOT EXISTS doctor_schedule
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    doctor_id
    BIGINT
    NOT
    NULL,
    day_of_week
    INT
    NOT
    NULL, -- 1=Monday, 2=Tuesday, etc.
    start_time
    TIME
    NOT
    NULL,
    end_time
    TIME
    NOT
    NULL,
    break_start_time
    TIME,
    break_end_time
    TIME,
    is_active
    BOOLEAN
    DEFAULT
    TRUE,
    effective_from
    DATE,
    effective_to
    DATE,
    created_at
    DATETIME
    DEFAULT
    CURRENT_TIMESTAMP,
    updated_at
    DATETIME
    DEFAULT
    CURRENT_TIMESTAMP
    ON
    UPDATE
    CURRENT_TIMESTAMP,
    FOREIGN
    KEY
(
    doctor_id
) REFERENCES user
(
    id
),
    UNIQUE KEY unique_doctor_day
(
    doctor_id,
    day_of_week,
    effective_from
)
    );

-- Create doctor_availability_exceptions table for managing doctor unavailability
CREATE TABLE IF NOT EXISTS doctor_availability_exceptions
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    doctor_id
    BIGINT
    NOT
    NULL,
    exception_date
    DATE
    NOT
    NULL,
    start_time
    TIME,
    end_time
    TIME,
    exception_type
    VARCHAR
(
    30
) NOT NULL, -- UNAVAILABLE, BREAK, MEETING, VACATION
    reason TEXT,
    is_recurring BOOLEAN DEFAULT FALSE,
    recurrence_pattern VARCHAR
(
    100
),
    created_by BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY
(
    doctor_id
) REFERENCES user
(
    id
),
    FOREIGN KEY
(
    created_by
) REFERENCES user
(
    id
)
    );

-- Insert default doctor schedules (9 AM to 5 PM, Monday to Friday)
INSERT
IGNORE INTO doctor_schedule (doctor_id, day_of_week, start_time, end_time, effective_from)
SELECT u.id       as doctor_id,
       wd.day_of_week,
       '09:00:00' as start_time,
       '17:00:00' as end_time,
       CURDATE()  as effective_from
FROM user u
         CROSS JOIN (SELECT 1 as day_of_week UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) wd
WHERE u.role = 'DOCTOR'
  AND u.is_active = TRUE;

-- Create appointment_analytics table for storing pre-calculated statistics
CREATE TABLE IF NOT EXISTS appointment_analytics
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    hospital_id
    BIGINT
    NOT
    NULL,
    department_id
    BIGINT,
    doctor_id
    BIGINT,
    analytics_date
    DATE
    NOT
    NULL,
    total_appointments
    INT
    DEFAULT
    0,
    scheduled_appointments
    INT
    DEFAULT
    0,
    completed_appointments
    INT
    DEFAULT
    0,
    cancelled_appointments
    INT
    DEFAULT
    0,
    no_show_appointments
    INT
    DEFAULT
    0,
    average_duration_minutes
    DECIMAL
(
    5,
    2
),
    utilization_rate DECIMAL
(
    5,
    2
), -- percentage of available slots used
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY
(
    hospital_id
) REFERENCES hospital
(
    id
),
    FOREIGN KEY
(
    department_id
) REFERENCES department
(
    id
),
    FOREIGN KEY
(
    doctor_id
) REFERENCES user
(
    id
),
    UNIQUE KEY unique_analytics
(
    hospital_id,
    department_id,
    doctor_id,
    analytics_date
)
    );

-- Add indexes for analytics table
CREATE INDEX idx_analytics_hospital_date ON appointment_analytics (hospital_id, analytics_date);
CREATE INDEX idx_analytics_doctor_date ON appointment_analytics (doctor_id, analytics_date);
CREATE INDEX idx_analytics_department_date ON appointment_analytics (department_id, analytics_date);

-- Create a view for easy appointment statistics reporting
CREATE
OR REPLACE VIEW appointment_statistics_view AS
SELECT a.fk_hospital_id   as hospital_id,
       h.name             as hospital_name,
       a.fk_department_id as department_id,
       d.name             as department_name,
       a.fk_doctor_id     as doctor_id,
       u.name             as doctor_name, DATE (a.start_time) as appointment_date, COUNT (*) as total_appointments, SUM (CASE WHEN a.status = 'SCHEDULED' THEN 1 ELSE 0 END) as scheduled_count, SUM (CASE WHEN a.status = 'CONFIRMED' THEN 1 ELSE 0 END) as confirmed_count, SUM (CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_count, SUM (CASE WHEN a.status = 'CANCELLED' THEN 1 ELSE 0 END) as cancelled_count, SUM (CASE WHEN a.status = 'NO_SHOW' THEN 1 ELSE 0 END) as no_show_count, AVG (a.duration_minutes) as avg_duration_minutes, COUNT (DISTINCT a.fk_patient_id) as unique_patients
FROM appointment a
    JOIN hospital h
ON a.fk_hospital_id = h.id
    LEFT JOIN department d ON a.fk_department_id = d.id
    JOIN user u ON a.fk_doctor_id = u.id
WHERE a.is_active = TRUE
GROUP BY a.fk_hospital_id, a.fk_department_id, a.fk_doctor_id, DATE (a.start_time);

-- Add comments to document the schema changes
ALTER TABLE appointment COMMENT = 'Enhanced appointment table with comprehensive appointment management features';
ALTER TABLE appointment_history COMMENT = 'Tracks all changes made to appointments for audit purposes';
ALTER TABLE appointment_reminders COMMENT = 'Manages reminder notifications for appointments';
ALTER TABLE appointment_waiting_list COMMENT = 'Manages waiting list for patients when preferred slots are unavailable';
ALTER TABLE doctor_schedule COMMENT = 'Defines regular working hours for doctors';
ALTER TABLE doctor_availability_exceptions COMMENT = 'Tracks doctor unavailability periods and exceptions';
ALTER TABLE appointment_analytics COMMENT = 'Pre-calculated appointment statistics for reporting and analytics';
