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
    AND COLUMN_NAME = 'payment_method';

