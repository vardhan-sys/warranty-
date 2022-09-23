ALTER TABLE IF EXISTS salesforce_company_lookup
DROP COLUMN IF EXISTS enabled,
DROP COLUMN IF EXISTS company_name;

ALTER TABLE IF EXISTS salesforce_company_lookup
ADD COLUMN IF NOT EXISTS company_name varchar,
ADD COLUMN IF NOT EXISTS enabled boolean DEFAULT false;