ALTER TABLE salesforce_company_airframe_entitlement
ADD COLUMN IF NOT EXISTS publication_access_level varchar;

ALTER TABLE salesforce_company_airframe_entitlement
ADD COLUMN IF NOT EXISTS publications boolean DEFAULT false;