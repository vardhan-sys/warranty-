
CREATE TABLE "engine_document_engine_model" (
engine_model VARCHAR NOT NULL CONSTRAINT engine_document_engine_model_engine_model_fk REFERENCES engine_model(model),
engine_document_id UUID NOT NULL CONSTRAINT engine_document_engine_model_engine_document_id_fk REFERENCES engine_document(id),
CONSTRAINT engine_document_engine_model_pk PRIMARY KEY (engine_model, engine_document_id)
);
