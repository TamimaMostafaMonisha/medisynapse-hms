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
