package com.geaviation.techpubs.models.techlib;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

@Table(name = "publication_access_level_document_types", schema = "techlib")
@Entity
@IdClass(PublicationAccessLevelDocumentTypeEntityPK.class)
public class PublicationAccessLevelDocumentTypeEntity implements Serializable {

    @Id
    @Column(name = "publication_access_level_id", nullable = false)
    private UUID publicationAccessLevelId;

    @Id
    @Column(name = "system_document_type_id", nullable = false)
    private UUID systemDocumentTypeId;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "publication_access_level_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private PublicationAccessLevelLookupEntity publicationAccessLevelLookupEntity;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "system_document_type_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private SystemDocumentTypeLookupEntity systemDocumentTypeLookupEntity;


    public UUID getPublicationAccessLevelId() {
        return publicationAccessLevelId;
    }

    public void setPublicationAccessLevelId(UUID publicationAccessLevelId) {
        this.publicationAccessLevelId = publicationAccessLevelId;
    }

    public UUID getSystemDocumentTypeId() {
        return systemDocumentTypeId;
    }

    public void setSystemDocumentTypeId(UUID systemDocumentTypeId) {
        this.systemDocumentTypeId = systemDocumentTypeId;
    }

    public PublicationAccessLevelLookupEntity getPublicationAccessLevelLookupEntity() {
        return publicationAccessLevelLookupEntity;
    }

    public void setPublicationAccessLevelLookupEntity(PublicationAccessLevelLookupEntity publicationAccessLevelLookupEntity) {
        this.publicationAccessLevelLookupEntity = publicationAccessLevelLookupEntity;
    }

    public SystemDocumentTypeLookupEntity getSystemDocumentTypeLookupEntity() {
        return systemDocumentTypeLookupEntity;
    }

    public void setSystemDocumentTypeLookupEntity(SystemDocumentTypeLookupEntity systemDocumentTypeLookupEntity) {
        this.systemDocumentTypeLookupEntity = systemDocumentTypeLookupEntity;
    }
}
