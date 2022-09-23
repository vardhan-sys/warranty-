CREATE TABLE "engine_document_type_lookup" (
    id     UUID NOT NULL DEFAULT public.gen_random_uuid() PRIMARY KEY,
    value VARCHAR NOT NULL
);


INSERT INTO engine_document_type_lookup (value)
VALUES ('REACH');

CREATE TABLE "engine_document" (
	id  UUID NOT NULL DEFAULT public.gen_random_uuid() PRIMARY KEY,
	engine_document_type_id  UUID NOT NULL CONSTRAINT engine_document_type_id_fk  references  engine_document_type_lookup,
	document_title  VARCHAR,
	part_name  VARCHAR,
	email_notification  BOOLEAN,
	deleted  BOOLEAN NOT NULL DEFAULT FALSE
);


