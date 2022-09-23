CREATE TABLE "engine_part_number_lookup"(
    id                UUID     NOT NULL DEFAULT public.gen_random_uuid() PRIMARY KEY,
    value             VARCHAR  NOT NULL CONSTRAINT engine_part_number_lookup_unique UNIQUE ,
    last_updated_by   VARCHAR,
    last_updated_date TIMESTAMP DEFAULT now()
);


CREATE TABLE "engine_document_part_number_affected" (
engine_document_id UUID NOT NULL CONSTRAINT engine_document_id_fk REFERENCES engine_document(id),
engine_part_number_id UUID NOT NULL CONSTRAINT engine_part_number_id_fk REFERENCES engine_part_number_lookup(id),
CONSTRAINT engine_document_part_number_affected_pk PRIMARY KEY (engine_document_id, engine_part_number_id)
);









