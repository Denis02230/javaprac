SELECT 'CREATE DATABASE bankinfo'
WHERE NOT EXISTS (
    SELECT FROM pg_database WHERE datname = 'bankinfo'
)\gexec
