package com.geaviation.techpubs.models.techlib;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

public class PublicationAccessLevelDocumentTypeEntityPK implements Serializable {

    @Id
    @Column(name = "publication_access_level_id", nullable = false)
    private UUID publicationAccessLevelId;

    @Id
    @Column(name = "system_document_type_id", nullable = false)
    private UUID systemDocumentTypeId;

    public PublicationAccessLevelDocumentTypeEntityPK() {
    }

    public UUID getAgreementSubtypeId() {
        return publicationAccessLevelId;
    }

    public void setAgreementSubtypeId(UUID agreementSubtypeId) {
        this.publicationAccessLevelId = agreementSubtypeId;
    }

    public UUID getSystemDocumentTypeId() {
        return systemDocumentTypeId;
    }

    public void setSystemDocumentTypeId(UUID systemDocumentTypeId) {
        this.systemDocumentTypeId = systemDocumentTypeId;
    }

}
