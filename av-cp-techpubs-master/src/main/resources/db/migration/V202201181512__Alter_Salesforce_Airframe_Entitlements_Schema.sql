ALTER TABLE salesforce_company_airframe_entitlement DROP CONSTRAINT salesforce_company_airframe_entitlement_pk;
ALTER TABLE salesforce_company_airframe_entitlement ADD PRIMARY KEY (id);
ALTER TABLE salesforce_company_airframe_entitlement ADD CONSTRAINT salesforce_company_airframe_entitlement_unique UNIQUE (airframe_id, salesforce_company_id, agreement_subtype_id);