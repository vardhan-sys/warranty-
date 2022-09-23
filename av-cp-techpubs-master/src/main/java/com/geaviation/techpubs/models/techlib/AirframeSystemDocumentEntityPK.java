package com.geaviation.techpubs.models.techlib;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

public class AirframeSystemDocumentEntityPK implements Serializable {

    @Id
    @Column(name = "airframe_id", nullable = false)
    private UUID airframeId;

    @Id
    @Column(name = "system_document_id", nullable = false)
    private UUID systemDocumentId;

    public AirframeSystemDocumentEntityPK() {
    }


    public AirframeSystemDocumentEntityPK(UUID airframeId, UUID systemDocumentId) {
        this.airframeId = airframeId;
        this.systemDocumentId = systemDocumentId;
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

}
