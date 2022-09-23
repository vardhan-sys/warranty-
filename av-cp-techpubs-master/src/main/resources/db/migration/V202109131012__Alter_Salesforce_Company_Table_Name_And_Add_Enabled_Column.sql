ALTER TABLE IF EXISTS salesforce_company
RENAME TO salesforce_company_lookup;

ALTER TABLE salesforce_company_lookup
ADD COLUMN enabled boolean DEFAULT false;

