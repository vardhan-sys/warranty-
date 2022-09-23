CREATE TABLE "system_document_site_lookup"
(
    id                         UUID         DEFAULT public.gen_random_uuid() NOT NULL CONSTRAINT system_document_site_pkey PRIMARY KEY,
	value                      VARCHAR NOT NULL,
    last_updated_by            VARCHAR,
    last_updated_date          TIMESTAMP DEFAULT now()
);


CREATE TABLE "system_document_type_lookup"
(
    id                         UUID         DEFAULT public.gen_random_uuid() NOT NULL CONSTRAINT system_document_type_lookup_pkey PRIMARY KEY,
    value                      VARCHAR NOT NULL,
    last_updated_by            VARCHAR,
    last_updated_date          TIMESTAMP DEFAULT now()
);



-- Lookup Tables for System Document Site--
INSERT INTO system_document_site_lookup (value, last_updated_by, last_updated_date)
    VALUES ('Avionics & Power Systems Cheltenham', '503252717', now());

INSERT INTO system_document_site_lookup (value, last_updated_by, last_updated_date)
    VALUES ('Avionics Systems Grand Rapids', '503252717', now());

INSERT INTO system_document_site_lookup (value, last_updated_by, last_updated_date)
    VALUES ('Defence Systems Clearwater', '503252717', now());

INSERT INTO system_document_site_lookup (value, last_updated_by, last_updated_date)
    VALUES ('Electronic Systems Rockford', '503252717', now());

INSERT INTO system_document_site_lookup (value, last_updated_by, last_updated_date)
    VALUES ('Power Systems Long Island', '503252717', now());

INSERT INTO system_document_site_lookup (value, last_updated_by, last_updated_date)
    VALUES ('Power Systems Vandalia', '503252717', now());



-- Lookup Tables for System Document Type--
INSERT INTO system_document_type_lookup (value, last_updated_by, last_updated_date)
    VALUES ('Abbreviated Component Maintenance Manual', '503252717', now());

INSERT INTO system_document_type_lookup (value, last_updated_by, last_updated_date)
    VALUES ('Atlas', '503252717', now());

INSERT INTO system_document_type_lookup (value, last_updated_by, last_updated_date)
    VALUES ('Component Maintenance Manual', '503252717', now());

INSERT INTO system_document_type_lookup (value, last_updated_by, last_updated_date)
    VALUES ('Component Maintenance Publication', '503252717', now());

INSERT INTO system_document_type_lookup (value, last_updated_by, last_updated_date)
    VALUES ('Flight Management Computer System', '503252717', now());

INSERT INTO system_document_type_lookup (value, last_updated_by, last_updated_date)
    VALUES ('Ground Equipment Manuals', '503252717', now());

INSERT INTO system_document_type_lookup (value, last_updated_by, last_updated_date)
    VALUES ('Illustrated Parts Catalog', '503252717', now());

INSERT INTO system_document_type_lookup (value, last_updated_by, last_updated_date)
    VALUES ('Overhaul Manual', '503252717', now());

INSERT INTO system_document_type_lookup (value, last_updated_by, last_updated_date)
    VALUES ('Pilot Guide', '503252717', now());

INSERT INTO system_document_type_lookup (value, last_updated_by, last_updated_date)
    VALUES ('Service Bulletin', '503252717', now());

INSERT INTO system_document_type_lookup (value, last_updated_by, last_updated_date)
    VALUES ('Service Information Letter', '503252717', now());

INSERT INTO system_document_type_lookup (value, last_updated_by, last_updated_date)
    VALUES ('Standard Practices Manual', '503252717', now());

INSERT INTO system_document_type_lookup (value, last_updated_by, last_updated_date)
    VALUES ('Service Sheet', '503252717', now());

INSERT INTO system_document_type_lookup (value, last_updated_by, last_updated_date)
    VALUES ('Test Equipment Manual', '503252717', now());

INSERT INTO system_document_type_lookup (value, last_updated_by, last_updated_date)
    VALUES ('TSDP', '503252717', now());

INSERT INTO system_document_type_lookup (value, last_updated_by, last_updated_date)
    VALUES ('User Manual', '503252717', now());






