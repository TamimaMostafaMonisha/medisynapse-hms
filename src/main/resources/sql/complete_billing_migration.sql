-- Complete Migration Script for Billing and Payment System
-- Date: 2025-11-12
-- Description: Update enums and add missing columns

USE medisynapse;

-- =====================================================
-- 1. Update billing status enum to include REFUNDED
-- =====================================================
ALTER TABLE billing
    MODIFY COLUMN status ENUM('DRAFT', 'SENT', 'PAID', 'PARTIALLY_PAID', 'OVERDUE', 'CANCELLED', 'REFUNDED') DEFAULT 'DRAFT';

-- =====================================================
-- 2. Update billing payment_method enum
-- =====================================================
ALTER TABLE billing
    MODIFY COLUMN payment_method ENUM('CASH', 'CARD', 'INSURANCE', 'BANK_TRANSFER', 'MOBILE_PAYMENT', 'CHECK', 'OTHER') DEFAULT 'CASH';

-- =====================================================
-- 3. Update payment payment_method enum
-- =====================================================
ALTER TABLE payment
    MODIFY COLUMN payment_method ENUM('CASH', 'CARD', 'INSURANCE', 'BANK_TRANSFER', 'MOBILE_PAYMENT', 'CHECK', 'OTHER') NOT NULL;

-- =====================================================
-- 4. Add reference_no column to payment table
-- =====================================================
-- Check if column exists before adding
SET @dbname = DATABASE();
SET @tablename = 'payment';
SET @columnname = 'reference_no';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (TABLE_SCHEMA = @dbname)
      AND (TABLE_NAME = @tablename)
      AND (COLUMN_NAME = @columnname)
  ) > 0,
  'SELECT 1',
  'ALTER TABLE payment ADD COLUMN reference_no VARCHAR(100) COMMENT "Payment reference number"'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- =====================================================
-- Verification Queries
-- =====================================================
SELECT 'Billing Status Enum' AS Check_Type,
       COLUMN_NAME,
       COLUMN_TYPE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'medisynapse'
  AND TABLE_NAME = 'billing'
  AND COLUMN_NAME = 'status'

UNION ALL

SELECT 'Billing Payment Method Enum' AS Check_Type,
       COLUMN_NAME,
       COLUMN_TYPE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'medisynapse'
  AND TABLE_NAME = 'billing'
  AND COLUMN_NAME = 'payment_method'

UNION ALL

SELECT 'Payment Payment Method Enum' AS Check_Type,
       COLUMN_NAME,
       COLUMN_TYPE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'medisynapse'
  AND TABLE_NAME = 'payment'
  AND COLUMN_NAME = 'payment_method'

UNION ALL

SELECT 'Payment Reference No Column' AS Check_Type,
       COLUMN_NAME,
       COLUMN_TYPE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'medisynapse'
  AND TABLE_NAME = 'payment'
  AND COLUMN_NAME = 'reference_no';

