ALTER TABLE salesforce_company_lookup ADD CONSTRAINT salesforce_company_lookup_unique UNIQUE (salesforce_id);

ALTER TABLE airframe_lookup ADD CONSTRAINT airframe_lookup_unique UNIQUE (airframe);