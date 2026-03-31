BEGIN;


DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'client_type_enum') THEN
    CREATE TYPE client_type_enum AS ENUM ('PERSON', 'ORG');
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'account_status_enum') THEN
    CREATE TYPE account_status_enum AS ENUM ('OPEN', 'CLOSED');
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'tx_type_enum') THEN
    CREATE TYPE tx_type_enum AS ENUM ('CREDIT', 'DEBIT', 'INTEREST');
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'interest_interval_enum') THEN
    CREATE TYPE interest_interval_enum AS ENUM ('DAILY', 'MONTHLY', 'QUARTERLY', 'YEARLY');
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'interest_method_enum') THEN
    CREATE TYPE interest_method_enum AS ENUM ('TO_SAME_ACCOUNT', 'TO_OTHER_ACCOUNT');
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'address_type_enum') THEN
    CREATE TYPE address_type_enum AS ENUM ('LEGAL', 'POSTAL', 'OTHER');
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'phone_type_enum') THEN
    CREATE TYPE phone_type_enum AS ENUM ('MOBILE', 'WORK', 'OTHER');
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'email_type_enum') THEN
    CREATE TYPE email_type_enum AS ENUM ('MAIN', 'BILLING', 'OTHER');
  END IF;
END $$;



CREATE TABLE IF NOT EXISTS branch (
  branch_id  BIGSERIAL PRIMARY KEY,
  name       VARCHAR(200) NOT NULL,
  address    VARCHAR(400) NOT NULL
);

CREATE TABLE IF NOT EXISTS client (
  client_id     BIGSERIAL PRIMARY KEY,
  client_type   client_type_enum NOT NULL,
  display_name  VARCHAR(300) NOT NULL,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS client_address (
  address_id    BIGSERIAL PRIMARY KEY,
  client_id     BIGINT NOT NULL REFERENCES client(client_id) ON DELETE CASCADE,
  address       VARCHAR(400) NOT NULL,
  address_type  address_type_enum NOT NULL
);

CREATE TABLE IF NOT EXISTS client_phone (
  phone_id    BIGSERIAL PRIMARY KEY,
  client_id   BIGINT NOT NULL REFERENCES client(client_id) ON DELETE CASCADE,
  phone       VARCHAR(50) NOT NULL,
  phone_type  phone_type_enum NOT NULL
);

CREATE TABLE IF NOT EXISTS client_email (
  email_id    BIGSERIAL PRIMARY KEY,
  client_id   BIGINT NOT NULL REFERENCES client(client_id) ON DELETE CASCADE,
  email       VARCHAR(254) NOT NULL,
  email_type  email_type_enum NOT NULL
);

CREATE TABLE IF NOT EXISTS contact_person (
  contact_id  BIGSERIAL PRIMARY KEY,
  client_id   BIGINT NOT NULL REFERENCES client(client_id) ON DELETE CASCADE,
  full_name   VARCHAR(200) NOT NULL,
  phone       VARCHAR(50),
  email       VARCHAR(254),
  position    VARCHAR(120)
);

CREATE TABLE IF NOT EXISTS account_type (
  account_type_id    BIGSERIAL PRIMARY KEY,
  name               VARCHAR(200) NOT NULL,

  -- Max allowed overdraft/credit
  max_credit         NUMERIC(14,2) NOT NULL DEFAULT 0 CHECK (max_credit >= 0),

  -- Rule description in English
  credit_repay_rule  VARCHAR(400) NOT NULL DEFAULT '',

  -- Interest settings
  interest_rate      NUMERIC(7,4) NOT NULL DEFAULT 0 CHECK (interest_rate >= 0),
  interest_interval  interest_interval_enum NOT NULL,
  interest_method    interest_method_enum NOT NULL,

  -- Permissions for transactions
  allow_debit        BOOLEAN NOT NULL DEFAULT TRUE,
  allow_credit       BOOLEAN NOT NULL DEFAULT TRUE,

  -- Separate limits for CREDIT and DEBIT
  min_credit_amount  NUMERIC(14,2),
  max_credit_amount  NUMERIC(14,2),
  min_debit_amount   NUMERIC(14,2),
  max_debit_amount   NUMERIC(14,2),

  CHECK (min_credit_amount IS NULL OR min_credit_amount >= 0),
  CHECK (max_credit_amount IS NULL OR max_credit_amount >= 0),
  CHECK (min_debit_amount  IS NULL OR min_debit_amount  >= 0),
  CHECK (max_debit_amount  IS NULL OR max_debit_amount  >= 0),

  CHECK (min_credit_amount IS NULL OR max_credit_amount IS NULL OR min_credit_amount <= max_credit_amount),
  CHECK (min_debit_amount  IS NULL OR max_debit_amount  IS NULL OR min_debit_amount  <= max_debit_amount)
);

CREATE TABLE IF NOT EXISTS account (
  account_id                 BIGSERIAL PRIMARY KEY,
  account_number             VARCHAR(40) NOT NULL UNIQUE,

  client_id                  BIGINT NOT NULL REFERENCES client(client_id),
  branch_id                  BIGINT NOT NULL REFERENCES branch(branch_id),
  account_type_id            BIGINT NOT NULL REFERENCES account_type(account_type_id),

  status                     account_status_enum NOT NULL DEFAULT 'OPEN',
  opened_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),
  closed_at                  TIMESTAMPTZ,

  balance                    NUMERIC(14,2) NOT NULL DEFAULT 0,

  -- If interest_method=TO_OTHER_ACCOUNT, application should set this (cross-table rule)
  interest_target_account_id BIGINT REFERENCES account(account_id),

  CHECK (
    (status = 'OPEN'   AND closed_at IS NULL) OR
    (status = 'CLOSED' AND closed_at IS NOT NULL)
  )
);

CREATE TABLE IF NOT EXISTS account_tx (
  tx_id      BIGSERIAL PRIMARY KEY,
  account_id BIGINT NOT NULL REFERENCES account(account_id) ON DELETE CASCADE,
  tx_time    TIMESTAMPTZ NOT NULL DEFAULT now(),
  tx_type    tx_type_enum NOT NULL,
  amount     NUMERIC(14,2) NOT NULL CHECK (amount > 0),
  comment    VARCHAR(400)
);

CREATE INDEX IF NOT EXISTS idx_account_client      ON account(client_id);
CREATE INDEX IF NOT EXISTS idx_account_branch      ON account(branch_id);
CREATE INDEX IF NOT EXISTS idx_account_type        ON account(account_type_id);
CREATE INDEX IF NOT EXISTS idx_tx_account_time     ON account_tx(account_id, tx_time);
CREATE INDEX IF NOT EXISTS idx_tx_time             ON account_tx(tx_time);

COMMIT;
