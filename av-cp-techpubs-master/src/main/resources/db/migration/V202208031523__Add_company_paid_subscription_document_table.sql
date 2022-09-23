CREATE TABLE "company_paid_subscription_document" (
company_id UUID NOT NULL CONSTRAINT salesforce_company_lookup_id_fk REFERENCES salesforce_company_lookup(id),
system_document_id UUID NOT NULL CONSTRAINT system_document_id_fk REFERENCES system_document(id),
CONSTRAINT company_paid_subscription_document_pk PRIMARY KEY (company_id, system_document_id)
);
