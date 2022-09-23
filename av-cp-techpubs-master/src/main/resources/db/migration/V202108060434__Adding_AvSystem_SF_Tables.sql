CREATE TABLE "agreement_subtype_lookup"
(
    id                            UUID         DEFAULT public.gen_random_uuid() NOT NULL CONSTRAINT agreement_subtype_pkey PRIMARY KEY,
    value                         VARCHAR NOT NULL,
    last_updated_by               VARCHAR,
    last_updated_date             TIMESTAMP DEFAULT now()
);



CREATE TABLE "agreement_subtype_document_type"
(
    agreement_subtype_id         UUID         NOT NULL CONSTRAINT agreement_subtype_document_type_agreement_subtype_id_fk REFERENCES agreement_subtype_lookup,
    system_document_type_id      UUID         NOT NULL CONSTRAINT agreement_subtype_document_type_system_document_type_id_fk REFERENCES system_document_type_lookup,
    CONSTRAINT agreement_subtype_document_type_pk PRIMARY KEY (agreement_subtype_id, system_document_type_id)
);


CREATE TABLE "airframe_lookup"
(
    id                          UUID         DEFAULT public.gen_random_uuid() NOT NULL CONSTRAINT airframe_lookup_pkey PRIMARY KEY,
    airframe                    VARCHAR NOT NULL
);



CREATE TABLE "salesforce_company"
(
    id                          UUID         DEFAULT public.gen_random_uuid() NOT NULL CONSTRAINT salesforce_company_pkey PRIMARY KEY,
    salesforce_id               UUID NOT NULL
);



CREATE TABLE "salesforce_company_airframe_entitlement"
(
    id                           UUID         DEFAULT public.gen_random_uuid() NOT NULL CONSTRAINT salesforce_company_airframe_entitlement_id PRIMARY KEY,
	airframe_id                  UUID NOT NULL CONSTRAINT company_airframe_entitlement_airframe_id_fk REFERENCES airframe_lookup,
	company_id                   UUID NOT NULL CONSTRAINT company_airframe_entitlement_company_id_fk REFERENCES salesforce_company,
    agreement_type               VARCHAR,
	agreement_subtype_id         UUID NOT NULL CONSTRAINT agreement_subtype_id_fk REFERENCES agreement_subtype_lookup,
	start_date                   TIMESTAMP,
	end_date                     TIMESTAMP,
	entitlement_status           VARCHAR
);
