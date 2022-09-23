package com.geaviation.techpubs.models.techlib.enginedoc;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

public class EngineDocumentEngineModelEntityPK implements Serializable {

    @Id
    @Column(name = "engine_model", nullable = false)
    private String engineModel;

    @Id
    @Column(name = "engine_document_id", nullable = false)
    private UUID engineDocumentId;


    public EngineDocumentEngineModelEntityPK() {
    }

    public String getEngineModel() { return engineModel; }

    public void setEngineModel(String engineModel) {
        this.engineModel = engineModel;
    }

    public UUID getEngineDocumentId() {
        return engineDocumentId;
    }

    public void setEngineDocumentId(UUID engineDocumentId) {
        this.engineDocumentId = engineDocumentId;
    }
}
