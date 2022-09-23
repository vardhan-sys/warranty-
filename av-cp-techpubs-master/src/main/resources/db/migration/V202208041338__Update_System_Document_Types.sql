-- Adding missing value for System Document Types --
INSERT INTO system_document_type_lookup (value, last_updated_by) values ('Abbreviated Maintenance Manual', '212444811');

-- Update values in publication access level document types --
INSERT INTO publication_access_level_document_types (publication_access_level_id, system_document_type_id)
    VALUES ((SELECT id FROM publication_access_level_lookup WHERE value='Tier 2'),
            (SELECT id FROM system_document_type_lookup WHERE value='Pilot Guide'));

DELETE FROM publication_access_level_document_types
WHERE publication_access_level_id = (SELECT id FROM publication_access_level_lookup WHERE value='Tier 2')
AND system_document_type_id = (SELECT id FROM system_document_type_lookup WHERE value='Service Bulletin');

DELETE FROM publication_access_level_document_types
WHERE publication_access_level_id = (SELECT id FROM publication_access_level_lookup WHERE value='Tier 2')
AND system_document_type_id = (SELECT id FROM system_document_type_lookup WHERE value='Standard Practices Manual');

-- Delete value TSDP as it is no longer needed --
DELETE FROM publication_access_level_document_types
WHERE system_document_type_id = (SELECT id FROM system_document_type_lookup WHERE value='TSDP');

DELETE FROM system_document_type_lookup
WHERE value = 'TSDP';
