-- Test data for H2 database
-- This file is automatically executed when the application starts with the 'local' profile

-- Insert test user
INSERT INTO USERS (USERNAME, EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, ROLE, IS_ACTIVE, CREATED_AT, UPDATED_AT)
VALUES ('test', 'testmail@securebank.com', 'test123', 'John', 'Doe', 'CUSTOMER', true, NOW(), NOW());

-- Insert test account (USER_ID = 1, assuming the user above gets ID 1)
INSERT INTO ACCOUNTS (BALANCE, USER_ID, ACCOUNT_NUMBER, ACCOUNT_TYPE, IS_ACTIVE, CREATED_AT, UPDATED_AT)
VALUES (10, 1, '2144', 'CHECKING', true, NOW(), NOW());

-- Insert test transaction (ACCOUNT_ID = 1, assuming the account above gets ID 1)
INSERT INTO TRANSACTIONS (AMOUNT, ACCOUNT_ID, DESCRIPTION, DEVICE_FINGERPRINT, FRAUD_REASONS, IP_ADDRESS, LOCATION, MERCHANT_CATEGORY, MERCHANT_NAME, TRANSACTION_TYPE) 
VALUES (10, 1, 'N/A', 'N/A', 'N/A', '127.0.0.0', 'unknown', 'N/A', 'Test', 'DEPOSIT');

