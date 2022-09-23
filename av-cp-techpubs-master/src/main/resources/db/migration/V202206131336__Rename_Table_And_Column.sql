ALTER TABLE IF EXISTS agreement_subtype_lookup
RENAME TO publication_access_level_lookup;

ALTER TABLE IF EXISTS agreement_subtype_document_type
RENAME TO publication_access_level_document_types;

ALTER TABLE IF EXISTS salesforce_company_airframe_entitlement
RENAME COLUMN agreement_subtype_id TO publication_access_level_id;

ALTER TABLE IF EXISTS publication_access_level_document_types
RENAME COLUMN agreement_subtype_id TO publication_access_level_id;

ALTER TABLE IF EXISTS agreement_subtype_lookup
DROP CONSTRAINT IF EXISTS agreement_subtype_lookup_unique;

UPDATE publication_access_level_lookup
SET value = 'Tier 1'
WHERE value = 'Systems 1';

UPDATE publication_access_level_lookup
SET value = 'Tier 2'
WHERE value = 'Systems 2';

ALTER TABLE IF EXISTS publication_access_level_lookup
ADD CONSTRAINT publication_access_level_lookup_unique UNIQUE (value);
