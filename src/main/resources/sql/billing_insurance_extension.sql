-- Bill Item Table
CREATE TABLE bill_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_billing_id BIGINT NOT NULL,
    service_type VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    quantity INT DEFAULT 1,
    unit_price DECIMAL(15,2) NOT NULL,
    total DECIMAL(15,2) NOT NULL,
    created_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_updated_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    version INT DEFAULT 1,
    FOREIGN KEY (fk_billing_id) REFERENCES billing(id)
);

-- Refund Table
CREATE TABLE refund (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_billing_id BIGINT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    reason VARCHAR(255),
    refund_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    version INT DEFAULT 1,
    FOREIGN KEY (fk_billing_id) REFERENCES billing(id)
);

-- Insurance Claim Table
CREATE TABLE insurance_claim (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_billing_id BIGINT NOT NULL,
    fk_policy_id BIGINT NOT NULL,
    claim_amount DECIMAL(15,2) NOT NULL,
    status VARCHAR(50) DEFAULT 'SUBMITTED',
    submitted_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
    settled_dt DATETIME,
    created_by BIGINT,
    updated_by BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    version INT DEFAULT 1,
    FOREIGN KEY (fk_billing_id) REFERENCES billing(id),
    FOREIGN KEY (fk_policy_id) REFERENCES insurance(id)
);

-- Insurance Settlement Table
CREATE TABLE insurance_settlement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_claim_id BIGINT NOT NULL,
    amount_settled DECIMAL(15,2) NOT NULL,
    settlement_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    remarks VARCHAR(255),
    created_by BIGINT,
    updated_by BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    version INT DEFAULT 1,
    FOREIGN KEY (fk_claim_id) REFERENCES insurance_claim(id)
);

-- Report Metadata Table (for JasperReports)
CREATE TABLE report_metadata (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_billing_id BIGINT,
    fk_claim_id BIGINT,
    report_type VARCHAR(100),
    generated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    file_path VARCHAR(255),
    created_by BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    version INT DEFAULT 1,
    FOREIGN KEY (fk_billing_id) REFERENCES billing(id),
    FOREIGN KEY (fk_claim_id) REFERENCES insurance_claim(id)
);

-- Migration script to update billing and payment enums
-- Date: 2025-11-12
-- Description: Add REFUNDED status to billing and CHECK, OTHER payment methods

-- Update billing status enum to include REFUNDED
ALTER TABLE billing
    MODIFY COLUMN status ENUM('DRAFT', 'SENT', 'PAID', 'PARTIALLY_PAID', 'OVERDUE', 'CANCELLED', 'REFUNDED') DEFAULT 'DRAFT';

-- Update billing payment_method enum to include CHECK and OTHER
ALTER TABLE billing
    MODIFY COLUMN payment_method ENUM('CASH', 'CARD', 'INSURANCE', 'BANK_TRANSFER', 'MOBILE_PAYMENT', 'CHECK', 'OTHER') DEFAULT 'CASH';

-- Update payment payment_method enum to include CHECK and OTHER
ALTER TABLE payment
    MODIFY COLUMN payment_method ENUM('CASH', 'CARD', 'INSURANCE', 'BANK_TRANSFER', 'MOBILE_PAYMENT', 'CHECK', 'OTHER') NOT NULL;

-- Add reference_no column to payment table if it doesn't exist
ALTER TABLE payment
    ADD COLUMN reference_no VARCHAR(100) COMMENT 'Payment reference number';

-- Verify the changes
SELECT
    COLUMN_NAME,
    COLUMN_TYPE
FROM
    INFORMATION_SCHEMA.COLUMNS
WHERE
    TABLE_NAME = 'billing'
  AND COLUMN_NAME IN ('status', 'payment_method')
UNION ALL
SELECT
    COLUMN_NAME,
    COLUMN_TYPE
FROM
    INFORMATION_SCHEMA.COLUMNS
WHERE
    TABLE_NAME = 'payment'
  AND COLUMN_NAME IN ('payment_method', 'reference_no');


