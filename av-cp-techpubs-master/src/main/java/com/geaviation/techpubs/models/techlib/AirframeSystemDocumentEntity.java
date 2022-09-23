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

@Table(name = "airframe_system_document", schema = "techlib")
@Entity
@IdClass(AirframeSystemDocumentEntityPK.class)
public class AirframeSystemDocumentEntity implements Serializable {

    @Id
    @Column(name = "airframe_id", nullable = false)
    private UUID airframeId;

    @Id
    @Column(name = "system_document_id", nullable = false)
    private UUID systemDocumentId;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "airframe_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private AirframeLookupEntity airframeLookupEntity;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "system_document_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private SystemDocumentEntity systemDocumentEntity;

    public AirframeSystemDocumentEntity() {
    }


    public AirframeSystemDocumentEntity(UUID airframeId, UUID systemDocumentId, AirframeLookupEntity airframeLookupEntity, SystemDocumentEntity systemDocumentEntity) {
        this.airframeId = airframeId;
        this.systemDocumentId = systemDocumentId;
        this.airframeLookupEntity = airframeLookupEntity;
        this.systemDocumentEntity = systemDocumentEntity;
    }

    public UUID getAirframeId() {
        return airframeId;
    }

    public void setAirframeId(UUID airframeId) {
        this.airframeId = airframeId;
    }

    public UUID getSystemDocumentId() {
        return systemDocumentId;
    }

    public void setSystemDocumentId(UUID systemDocumentId) {
        this.systemDocumentId = systemDocumentId;
    }

    public AirframeLookupEntity getAirframeLookupEntity() {
        return airframeLookupEntity;
    }

    public void setAirframeLookupEntity(AirframeLookupEntity airframeLookupEntity) {
        this.airframeLookupEntity = airframeLookupEntity;
    }

    public SystemDocumentEntity getSystemDocumentEntity() {
        return systemDocumentEntity;
    }

    public void setSystemDocumentEntity(SystemDocumentEntity systemDocumentEntity) {
        this.systemDocumentEntity = systemDocumentEntity;
    }

}
