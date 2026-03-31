-- show.sql (PostgreSQL) - pure SQL (no psql meta-commands)

SELECT 'BRANCH' AS section;
SELECT branch_id, name, address FROM branch ORDER BY branch_id;

SELECT 'CLIENT' AS section;
SELECT client_id, client_type, display_name, created_at
FROM client
ORDER BY client_id;

SELECT 'CONTACTS (address)' AS section;
SELECT address_id, client_id, address_type, address
FROM client_address
ORDER BY client_id, address_id;

SELECT 'CONTACTS (phone)' AS section;
SELECT phone_id, client_id, phone_type, phone
FROM client_phone
ORDER BY client_id, phone_id;

SELECT 'CONTACTS (email)' AS section;
SELECT email_id, client_id, email_type, email
FROM client_email
ORDER BY client_id, email_id;

SELECT 'CONTACT_PERSON (org)' AS section;
SELECT contact_id, client_id, full_name, position, phone, email
FROM contact_person
ORDER BY client_id, contact_id;

SELECT 'ACCOUNT_TYPE' AS section;
SELECT
  account_type_id, name,
  max_credit, interest_rate, interest_interval, interest_method,
  allow_debit, allow_credit,
  min_credit_amount, max_credit_amount,
  min_debit_amount, max_debit_amount
FROM account_type
ORDER BY account_type_id;

SELECT 'ACCOUNT' AS section;
SELECT
  a.account_id,
  a.account_number,
  c.display_name AS client,
  a.status,
  a.balance,
  b.name AS branch,
  at.name AS account_type,
  a.interest_target_account_id,
  a.opened_at,
  a.closed_at
FROM account a
JOIN client c ON c.client_id = a.client_id
JOIN branch b ON b.branch_id = a.branch_id
JOIN account_type at ON at.account_type_id = a.account_type_id
ORDER BY a.account_id;

SELECT 'ACCOUNT_TX (latest 50)' AS section;
SELECT tx_id, account_id, tx_time, tx_type, amount, comment
FROM account_tx
ORDER BY tx_time DESC
LIMIT 50;