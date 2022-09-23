ALTER TABLE salesforce_company_airframe_entitlement
  ADD COLUMN IF NOT EXISTS "entitlement_id" VARCHAR;

ALTER TABLE salesforce_company_airframe_entitlement
  ADD COLUMN IF NOT EXISTS "last_modified_date" TIMESTAMP;