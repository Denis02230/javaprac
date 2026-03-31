BEGIN;


INSERT INTO branch (branch_id, name, address) VALUES
  (1, 'Central Branch', 'Moscow, Mikluho-Maklaya st, 5'),
  (2, 'North Branch',   'Moscow, Kolmogorova st, 7'),
  (3, 'South Branch',   'Moscow, Vatutina st, 37');

INSERT INTO client (client_id, client_type, display_name, created_at) VALUES
  (1, 'PERSON', 'Ivan Petrov',      '2026-02-10 10:00:00+00'),
  (2, 'PERSON', 'Anna Sargsyan',    '2026-02-11 11:00:00+00'),
  (3, 'PERSON', 'Denis Karpyshev',  '2026-02-12 12:00:00+00'),
  (4, 'ORG',    'Alpha LLC',        '2026-02-13 13:00:00+00'),
  (5, 'ORG',    'Bella Trade JSC',  '2026-02-14 14:00:00+00'),
  (6, 'ORG',    'Gamma NGO',        '2026-02-15 15:00:00+00');

INSERT INTO client_address (address_id, client_id, address, address_type) VALUES
  (1, 4, 'Moscow, Respublica st, 1', 'LEGAL'),
  (2, 4, 'Moscow, Rezhina st, 2',    'POSTAL'),
  (3, 5, 'Moscow, Lion st, 3',       'LEGAL'),
  (4, 5, 'Moscow, Kalugina st, 4',   'POSTAL'),
  (5, 1, 'Moscow, Cat st, 5',        'OTHER'),
  (6, 6, 'Moscow. Super Cat st, 6',  'OTHER');

INSERT INTO client_phone (phone_id, client_id, phone, phone_type) VALUES
  (1, 1, '+7-916-101-11-11', 'MOBILE'),
  (2, 2, '+7-916-150-22-22', 'MOBILE'),
  (3, 4, '+7-916-510-44-00', 'WORK'),
  (4, 5, '+7-916-102-55-00', 'WORK'),
  (5, 3, '+7-916-101-33-33', 'OTHER'),
  (6, 6, '+7-916-102-69-67', 'OTHER');

INSERT INTO client_email (email_id, client_id, email, email_type) VALUES
  (1, 1, 'ivan.petrov@example.com',      'MAIN'),
  (2, 2, 'anna.sargsyan@example.com',    'MAIN'),
  (3, 3, 'denis.karpyshev@example.com',  'MAIN'),
  (4, 4, 'a.murmov@example.com',         'MAIN'),
  (5, 5, 'bella.kharlamyan@example.com', 'MAIN'),
  (6, 6, 'tMAyE@example.com',            'MAIN'),
  (7, 4, 'billing@alpha.example.com',    'BILLING'),
  (8, 5, 'billing@beta.example.com',     'BILLING'),
  (9, 2, 'anna.alt@example.com',         'OTHER');

INSERT INTO contact_person (contact_id, client_id, full_name, phone, email, position) VALUES
  (1, 4, 'Arman Hakobyan',   '+7-916-140-44-44', 'arman@alpha.example.com',  'Finance'),
  (2, 4, 'Mariam Grigoryan', NULL,               'mariam@alpha.example.com', 'CEO Assistant'),
  (3, 5, 'Karen Mkrtchyan',  '+7-916-410-55-11', NULL,                       'Accountant'),
  (4, 6, 'Lilit Hovsepyan',  '+7-916-122-66-11', 'lilit@gamma.example.com',  'Coordinator');

INSERT INTO account_type (
  account_type_id, name,
  max_credit, credit_repay_rule,
  interest_rate, interest_interval, interest_method,
  allow_debit, allow_credit,
  min_credit_amount, max_credit_amount,
  min_debit_amount,  max_debit_amount
) VALUES
  (1, 'Savings Basic',
      0, 'No credit. Withdrawals limited.',
      0.0500, 'MONTHLY', 'TO_SAME_ACCOUNT',
      TRUE, TRUE,
      10.00,  50000.00,
      10.00,   5000.00),

  (2, 'Checking Standard',
      0, 'No credit.',
      0.0000, 'YEARLY', 'TO_SAME_ACCOUNT',
      TRUE, TRUE,
      1.00,  200000.00,
      1.00,  200000.00),

  (3, 'Overdraft Account',
      1000.00, 'Repayment must restore balance above -max_credit within 30 days.',
      0.0000, 'QUARTERLY', 'TO_SAME_ACCOUNT',
      TRUE, TRUE,
      1.00,  500000.00,
      1.00,  500000.00),

  (4, 'Deposit To Other',
      0, 'No credit. Interest paid to another account.',
      0.0300, 'DAILY', 'TO_OTHER_ACCOUNT',
      FALSE, TRUE,
      100.00, 1000000.00,
      NULL, NULL);

INSERT INTO account (
  account_id, account_number, client_id, branch_id, account_type_id,
  status, opened_at, closed_at, balance, interest_target_account_id
) VALUES
  (1, 'ACC-0001', 1, 1, 2, 'OPEN',   '2026-02-01 09:00:00+00', NULL, 0, NULL), -- Ivan Checking
  (2, 'ACC-0002', 2, 1, 1, 'OPEN',   '2026-02-02 09:00:00+00', NULL, 0, NULL), -- Anna Savings
  (3, 'ACC-0003', 3, 2, 3, 'OPEN',   '2026-02-03 09:00:00+00', NULL, 0, NULL), -- Denis Overdraft
  (4, 'ACC-0004', 4, 1, 2, 'OPEN',   '2026-02-04 09:00:00+00', NULL, 0, NULL), -- Alpha Checking (interest target)
  (5, 'ACC-0005', 4, 1, 4, 'OPEN',   '2026-02-05 09:00:00+00', NULL, 0, 4),    -- Alpha Deposit To Other (pays to ACC-0004)
  (6, 'ACC-0006', 5, 3, 2, 'OPEN',   '2026-02-06 09:00:00+00', NULL, 0, NULL), -- Beta Checking
  (7, 'ACC-0007', 6, 3, 1, 'CLOSED', '2026-01-10 09:00:00+00', '2026-02-10 10:00:00+00', 0, NULL), -- Gamma Savings (closed)
  (8, 'ACC-0008', 2, 2, 2, 'CLOSED', '2026-01-12 09:00:00+00', '2026-02-12 10:00:00+00', 0, NULL); -- Anna Checking (closed)

-- ACCOUNT_TX (>= 20), covers CREDIT/DEBIT/INTEREST, has date ranges for filtering
INSERT INTO account_tx (tx_id, account_id, tx_time, tx_type, amount, comment) VALUES
  -- Account 1 (Ivan, Checking)
  (1,  1, '2026-02-05 10:00:00+00', 'CREDIT',   1500.00, 'Salary'),
  (2,  1, '2026-02-07 12:00:00+00', 'DEBIT',     200.00, 'Card payment'),
  (3,  1, '2026-02-15 18:00:00+00', 'DEBIT',     100.00, 'ATM withdrawal'),

  -- Account 2 (Anna, Savings)
  (4,  2, '2026-02-06 10:00:00+00', 'CREDIT',   5000.00, 'Initial deposit'),
  (5,  2, '2026-02-20 10:00:00+00', 'INTEREST',   10.00, 'Monthly interest (example)'),
  (6,  2, '2026-02-21 09:00:00+00', 'CREDIT',    300.00, 'Top-up'),

  -- Account 3 (Denis, Overdraft)
  (7,  3, '2026-02-05 09:30:00+00', 'CREDIT',    800.00, 'Transfer in'),
  (8,  3, '2026-02-06 15:00:00+00', 'DEBIT',     900.00, 'Large purchase (uses overdraft)'),
  (9,  3, '2026-02-10 15:00:00+00', 'CREDIT',    200.00, 'Partial repayment'),

  -- Account 4 (Alpha, Checking) + receives interest from deposit account 5
  (10, 4, '2026-02-04 11:00:00+00', 'CREDIT',  20000.00, 'Client payment'),
  (11, 4, '2026-02-08 16:00:00+00', 'DEBIT',    2500.00, 'Supplier invoice'),
  (12, 4, '2026-02-18 09:00:00+00', 'DEBIT',    1800.00, 'Payroll'),
  (13, 4, '2026-02-06 10:01:00+00', 'INTEREST',   50.00, 'Interest from ACC-0005 (daily example)'),
  (14, 4, '2026-02-07 10:01:00+00', 'INTEREST',   50.00, 'Interest from ACC-0005 (daily example)'),
  (15, 4, '2026-02-08 10:01:00+00', 'INTEREST',   50.00, 'Interest from ACC-0005 (daily example)'),

  -- Account 5 (Alpha, Deposit To Other) principal only (interest paid elsewhere)
  (16, 5, '2026-02-05 10:00:00+00', 'CREDIT',  50000.00, 'Term deposit principal'),

  -- Account 6 (Beta, Checking)
  (17, 6, '2026-02-03 10:00:00+00', 'CREDIT',  12000.00, 'Incoming payment'),
  (18, 6, '2026-02-10 10:00:00+00', 'DEBIT',    1000.00, 'Office rent'),
  (19, 6, '2026-02-22 10:00:00+00', 'DEBIT',     500.00, 'Utilities'),

  -- Account 7 (Gamma, Savings, CLOSED) -> force balance to 0 before close
  (20, 7, '2026-01-15 10:00:00+00', 'CREDIT',   2000.00, 'Donation'),
  (21, 7, '2026-01-20 10:00:00+00', 'DEBIT',     400.00, 'Event expense'),
  (22, 7, '2026-02-01 10:00:00+00', 'INTEREST',    5.00, 'Interest before close'),
  (23, 7, '2026-02-10 09:59:00+00', 'DEBIT',    1605.00, 'Transfer out before closing'),

  -- Account 8 (Anna, Checking, CLOSED) -> force balance to 0 before close
  (24, 8, '2026-01-18 10:00:00+00', 'CREDIT',   1000.00, 'Old account funding'),
  (25, 8, '2026-01-25 10:00:00+00', 'DEBIT',     200.00, 'Purchase'),
  (26, 8, '2026-02-12 09:59:00+00', 'DEBIT',     800.00, 'Transfer out before closing');


UPDATE account a
SET balance = COALESCE(t.sum_effect, 0)
FROM (
  SELECT
    account_id,
    SUM(
      CASE
        WHEN tx_type IN ('CREDIT','INTEREST') THEN amount
        WHEN tx_type = 'DEBIT' THEN -amount
        ELSE 0
      END
    ) AS sum_effect
  FROM account_tx
  GROUP BY account_id
) t
WHERE a.account_id = t.account_id;


SELECT setval(pg_get_serial_sequence('branch','branch_id'), (SELECT MAX(branch_id) FROM branch));
SELECT setval(pg_get_serial_sequence('client','client_id'), (SELECT MAX(client_id) FROM client));
SELECT setval(pg_get_serial_sequence('client_address','address_id'), (SELECT MAX(address_id) FROM client_address));
SELECT setval(pg_get_serial_sequence('client_phone','phone_id'), (SELECT MAX(phone_id) FROM client_phone));
SELECT setval(pg_get_serial_sequence('client_email','email_id'), (SELECT MAX(email_id) FROM client_email));
SELECT setval(pg_get_serial_sequence('contact_person','contact_id'), (SELECT MAX(contact_id) FROM contact_person));
SELECT setval(pg_get_serial_sequence('account_type','account_type_id'), (SELECT MAX(account_type_id) FROM account_type));
SELECT setval(pg_get_serial_sequence('account','account_id'), (SELECT MAX(account_id) FROM account));
SELECT setval(pg_get_serial_sequence('account_tx','tx_id'), (SELECT MAX(tx_id) FROM account_tx));

COMMIT;