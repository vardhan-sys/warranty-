ALTER TABLE system_document_type_lookup ADD CONSTRAINT system_document_type_lookup_unique UNIQUE (value);

ALTER TABLE agreement_subtype_lookup ADD CONSTRAINT agreement_subtype_lookup_unique UNIQUE (value);