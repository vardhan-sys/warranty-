CREATE TABLE "system_document"
(
    id                        UUID         DEFAULT public.gen_random_uuid() NOT NULL CONSTRAINT system_document_pkey PRIMARY KEY,
	system_document_type_id   UUID NOT NULL CONSTRAINT system_documents_system_documents_type_id_fk REFERENCES system_document_type_lookup,
	document_number           VARCHAR NOT NULL,
	system_document_site_id   UUID NOT NULL CONSTRAINT system_documents_system_document_site_id_fk REFERENCES system_document_site_lookup,
    document_description      VARCHAR,
    revision_date             TIMESTAMP NOT NULL,
	revision                  VARCHAR NOT NULL,
	distribution_date         TIMESTAMP,
	file_name                 VARCHAR NOT NULL,
	s3_file_path              VARCHAR NOT NULL,
	company_specific          BOOLEAN,
	email_notification        BOOLEAN
);



CREATE TABLE "airframe_system_document"
(
    airframe_id               UUID         NOT NULL CONSTRAINT airframe_system_document_airframe_id_fk REFERENCES airframe_lookup,
	system_document_id        UUID         NOT NULL CONSTRAINT airframe_system_document_system_document_id_fk REFERENCES system_document,
    CONSTRAINT airframe_system_document_pk PRIMARY KEY (airframe_id, system_document_id)
);



CREATE TABLE "part_numbers_affected"
(
    system_document_id            UUID NOT NULL CONSTRAINT part_numbers_affected_system_document_id_fk REFERENCES system_document,
    part_number                   VARCHAR NOT NULL,
    CONSTRAINT part_numbers_affected_pk PRIMARY KEY (system_document_id)
);



CREATE TABLE "company_system_document"
(
    company_id                     UUID NOT NULL CONSTRAINT company_system_document_company_id_fk REFERENCES salesforce_company,
    system_document_id            UUID NOT NULL CONSTRAINT company_system_document_system_document_id_fk REFERENCES system_document,
    CONSTRAINT company_system_document_pk PRIMARY KEY (company_id, system_document_id)
);
