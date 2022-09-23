-- Values for Agreement Subtype Lookup - using names from SF --
INSERT INTO agreement_subtype_lookup (value, last_updated_by, last_updated_date)
    VALUES ('Systems 1', '212444811', now());

INSERT INTO agreement_subtype_lookup (value, last_updated_by, last_updated_date)
    VALUES ('Systems 2', '212444811', now());

-- Values for Agreement Subtype Document Type --
INSERT INTO agreement_subtype_document_type (agreement_subtype_id, system_document_type_id)
    VALUES ((SELECT id FROM agreement_subtype_lookup WHERE value='Systems 1'),
            (SELECT id FROM system_document_type_lookup WHERE value='Abbreviated Component Maintenance Manual'));

INSERT INTO agreement_subtype_document_type (agreement_subtype_id, system_document_type_id)
    VALUES ((SELECT id FROM agreement_subtype_lookup WHERE value='Systems 1'),
            (SELECT id FROM system_document_type_lookup WHERE value='Atlas'));

INSERT INTO agreement_subtype_document_type (agreement_subtype_id, system_document_type_id)
    VALUES ((SELECT id FROM agreement_subtype_lookup WHERE value='Systems 1'),
            (SELECT id FROM system_document_type_lookup WHERE value='Component Maintenance Manual'));

INSERT INTO agreement_subtype_document_type (agreement_subtype_id, system_document_type_id)
    VALUES ((SELECT id FROM agreement_subtype_lookup WHERE value='Systems 1'),
            (SELECT id FROM system_document_type_lookup WHERE value='Component Maintenance Publication'));

INSERT INTO agreement_subtype_document_type (agreement_subtype_id, system_document_type_id)
    VALUES ((SELECT id FROM agreement_subtype_lookup WHERE value='Systems 1'),
            (SELECT id FROM system_document_type_lookup WHERE value='Flight Management Computer System'));

INSERT INTO agreement_subtype_document_type (agreement_subtype_id, system_document_type_id)
    VALUES ((SELECT id FROM agreement_subtype_lookup WHERE value='Systems 1'),
            (SELECT id FROM system_document_type_lookup WHERE value='Illustrated Parts Catalog'));

INSERT INTO agreement_subtype_document_type (agreement_subtype_id, system_document_type_id)
    VALUES ((SELECT id FROM agreement_subtype_lookup WHERE value='Systems 1'),
            (SELECT id FROM system_document_type_lookup WHERE value='Overhaul Manual'));

INSERT INTO agreement_subtype_document_type (agreement_subtype_id, system_document_type_id)
    VALUES ((SELECT id FROM agreement_subtype_lookup WHERE value='Systems 1'),
            (SELECT id FROM system_document_type_lookup WHERE value='Pilot Guide'));

INSERT INTO agreement_subtype_document_type (agreement_subtype_id, system_document_type_id)
    VALUES ((SELECT id FROM agreement_subtype_lookup WHERE value='Systems 1'),
            (SELECT id FROM system_document_type_lookup WHERE value='Service Bulletin'));

INSERT INTO agreement_subtype_document_type (agreement_subtype_id, system_document_type_id)
    VALUES ((SELECT id FROM agreement_subtype_lookup WHERE value='Systems 2'),
            (SELECT id FROM system_document_type_lookup WHERE value='Service Bulletin'));

INSERT INTO agreement_subtype_document_type (agreement_subtype_id, system_document_type_id)
    VALUES ((SELECT id FROM agreement_subtype_lookup WHERE value='Systems 1'),
            (SELECT id FROM system_document_type_lookup WHERE value='Service Information Letter'));

INSERT INTO agreement_subtype_document_type (agreement_subtype_id, system_document_type_id)
    VALUES ((SELECT id FROM agreement_subtype_lookup WHERE value='Systems 2'),
            (SELECT id FROM system_document_type_lookup WHERE value='Service Information Letter'));

INSERT INTO agreement_subtype_document_type (agreement_subtype_id, system_document_type_id)
    VALUES ((SELECT id FROM agreement_subtype_lookup WHERE value='Systems 1'),
            (SELECT id FROM system_document_type_lookup WHERE value='Standard Practices Manual'));

INSERT INTO agreement_subtype_document_type (agreement_subtype_id, system_document_type_id)
    VALUES ((SELECT id FROM agreement_subtype_lookup WHERE value='Systems 2'),
            (SELECT id FROM system_document_type_lookup WHERE value='Standard Practices Manual'));

INSERT INTO agreement_subtype_document_type (agreement_subtype_id, system_document_type_id)
    VALUES ((SELECT id FROM agreement_subtype_lookup WHERE value='Systems 1'),
            (SELECT id FROM system_document_type_lookup WHERE value='TSDP'));

INSERT INTO agreement_subtype_document_type (agreement_subtype_id, system_document_type_id)
    VALUES ((SELECT id FROM agreement_subtype_lookup WHERE value='Systems 1'),
            (SELECT id FROM system_document_type_lookup WHERE value='User Manual'));