CREATE TABLE "archival_documents" (
    id             UUID NOT NULL DEFAULT public.gen_random_uuid() PRIMARY KEY,
    type           VARCHAR,
    title          VARCHAR,
    issue_number   VARCHAR,
    effective_date DATE,
    file_name      VARCHAR,
    s3_file_path   VARCHAR
);

CREATE TABLE "archival_engine_model_lookup" (
    id     UUID NOT NULL DEFAULT public.gen_random_uuid() PRIMARY KEY,
    family VARCHAR,
    model  VARCHAR,
    CONSTRAINT archival_eml_family_model UNIQUE (family, model)
);

CREATE TABLE "archival_documents_eml" (
    id                              UUID NOT NULL DEFAULT public.gen_random_uuid() PRIMARY KEY,
    archival_documents_id           UUID NOT NULL CONSTRAINT archival_documents_id_fk REFERENCES archival_documents,
    archival_engine_model_lookup_id UUID NOT NULL CONSTRAINT archival_eml_id_fk REFERENCES archival_engine_model_lookup
);

CREATE TABLE "archival_company" (
    id        UUID NOT NULL DEFAULT public.gen_random_uuid() PRIMARY KEY,
    icao_code VARCHAR NOT NULL
);

create table "archival_company_eml" (
	id                              UUID NOT NULL DEFAULT public.gen_random_uuid() PRIMARY KEY,
    archival_company_id             UUID NOT NULL CONSTRAINT archival_company_id_fk REFERENCES archival_company,
    archival_engine_model_lookup_id UUID NOT NULL CONSTRAINT archival_eml_id_fk REFERENCES archival_engine_model_lookup
);

INSERT INTO archival_documents (type, title, issue_number, effective_date, file_name, s3_file_path)
VALUES ('SB ALERTS', 'SB for CJ610_A-106A1', '123', '1972-02-11', 'SBCF-A72-106A1.pdf', 's3:/archival_documents/CJ610_A/SB_ALERTS');

INSERT INTO archival_documents (type, title, issue_number, effective_date, file_name, s3_file_path)
VALUES ('SB', 'CJ610 SB Index - Cj610 Service Bulletin Index', 'SB Index', '2015-05-21', 'SB-Cj610.pdf', 's3:/archival_documents/CJ610_A/SB');

INSERT INTO archival_engine_model_lookup (family, model)
VALUES ('CF700', 'CF700-2C');

INSERT INTO archival_engine_model_lookup (family, model)
VALUES ('CF700', 'CF700-2D');

INSERT INTO archival_engine_model_lookup (family, model)
VALUES ('CF700', 'CF700-2D-2');

INSERT INTO archival_engine_model_lookup (family, model)
VALUES ('CJ610', 'CJ610-1');

INSERT INTO archival_engine_model_lookup (family, model)
VALUES ('CJ610', 'CJ610-4');

INSERT INTO archival_engine_model_lookup (family, model)
VALUES ('CJ610', 'CJ610-5');

INSERT INTO archival_engine_model_lookup (family, model)
VALUES ('CJ610', 'CJ610-6');

INSERT INTO archival_engine_model_lookup (family, model)
VALUES ('CJ610', 'CJ610-8');

INSERT INTO archival_engine_model_lookup (family, model)
VALUES ('CJ610', 'CJ610-8A');

INSERT INTO archival_engine_model_lookup (family, model)
VALUES ('CJ610', 'CJ610-9');

INSERT INTO archival_documents_eml (archival_documents_id, archival_engine_model_lookup_id)
VALUES ((SELECT id FROM archival_documents WHERE title = 'SB for CJ610_A-106A1'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CJ610-1'));

INSERT INTO archival_documents_eml (archival_documents_id, archival_engine_model_lookup_id)
VALUES ((SELECT id FROM archival_documents WHERE title = 'CJ610 SB Index - Cj610 Service Bulletin Index'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CJ610-5'));

INSERT INTO archival_documents_eml (archival_documents_id, archival_engine_model_lookup_id)
VALUES ((SELECT id FROM archival_documents WHERE title = 'CJ610 SB Index - Cj610 Service Bulletin Index'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CJ610-1'));

insert into archival_company (icao_code)
values ('DEL');

insert into archival_company_eml (archival_company_id, archival_engine_model_lookup_id)
values ((select id from archival_company where icao_code = 'DEL'), (select id from archival_engine_model_lookup where model = 'CJ610-1'));

insert into archival_company_eml (archival_company_id, archival_engine_model_lookup_id)
values ((select id from archival_company where icao_code = 'DEL'), (select id from archival_engine_model_lookup where model = 'CJ610-5'));

