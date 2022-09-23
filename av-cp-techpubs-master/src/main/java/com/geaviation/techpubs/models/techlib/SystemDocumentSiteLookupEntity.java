package com.geaviation.techpubs.models.techlib;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Table(name = "system_document_site_lookup", schema = "techlib")
@Entity
public class SystemDocumentSiteLookupEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "value", nullable = false)
    private String value;

    @JsonIgnore
    @Column(name = "last_updated_by", nullable = false)
    private String lastUpdatedBy;

    @JsonIgnore
    @Column(name = "last_updated_date", nullable = false)
    private Timestamp lastUpdatedDate;

    @JsonIgnore
    @OneToMany(mappedBy = "systemDocumentSiteLookupEntity", cascade = CascadeType.ALL)
    private Set<SystemDocumentEntity> systemDocumentEntity = new HashSet<SystemDocumentEntity>();


    public SystemDocumentSiteLookupEntity() {
    }


    public SystemDocumentSiteLookupEntity(UUID id, String value, String lastUpdatedBy, Timestamp lastUpdatedDate, Set<SystemDocumentEntity> systemDocumentEntity) {
        this.id = id;
        this.value = value;
        this.lastUpdatedBy = lastUpdatedBy;
        this.lastUpdatedDate = lastUpdatedDate;
        this.systemDocumentEntity = systemDocumentEntity;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Timestamp getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Timestamp lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }
}
