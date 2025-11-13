-- Migration script to add reference_no to payment table
-- Date: 2025-11-12
-- Description: Add reference_no column for payment tracking

USE medisynapse;

-- Add reference_no column to payment table
ALTER TABLE payment
    ADD COLUMN IF NOT EXISTS reference_no VARCHAR(100) COMMENT 'Payment reference number';

-- Verify the change
SELECT
    COLUMN_NAME,
    COLUMN_TYPE,
    IS_NULLABLE,
    COLUMN_COMMENT
FROM
    INFORMATION_SCHEMA.COLUMNS
WHERE
    TABLE_SCHEMA = 'medisynapse'
  AND TABLE_NAME = 'payment'
  AND COLUMN_NAME = 'reference_no';
