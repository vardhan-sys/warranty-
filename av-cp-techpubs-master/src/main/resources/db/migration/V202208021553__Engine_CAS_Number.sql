CREATE TABLE IF NOT EXISTS "engine_cas_number" (
id UUID NOT NULL DEFAULT public.gen_random_uuid() PRIMARY KEY,
engine_document_id UUID NOT NULL CONSTRAINT engine_document_id_fk REFERENCES engine_document(id),
cas_number VARCHAR NOT NULL,
CONSTRAINT engine_cas_number_unique UNIQUE (engine_document_id,cas_number)
);