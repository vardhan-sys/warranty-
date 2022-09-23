ALTER TABLE salesforce_company_airframe_entitlement RENAME COLUMN company_id TO salesforce_company_id;

ALTER TABLE salesforce_company_airframe_entitlement
  DROP CONSTRAINT salesforce_company_airframe_entitlement_id;

ALTER TABLE salesforce_company_airframe_entitlement
  ADD CONSTRAINT salesforce_company_airframe_entitlement_pk
    PRIMARY KEY (airframe_id, salesforce_company_id, agreement_subtype_id);