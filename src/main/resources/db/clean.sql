BEGIN;

TRUNCATE TABLE
  account_tx,
  account,
  account_type,
  contact_person,
  client_email,
  client_phone,
  client_address,
  client,
  branch
RESTART IDENTITY CASCADE;

COMMIT;