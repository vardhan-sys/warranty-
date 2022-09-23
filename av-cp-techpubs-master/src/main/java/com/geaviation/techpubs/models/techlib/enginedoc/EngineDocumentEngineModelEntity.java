package com.geaviation.techpubs.models.techlib.enginedoc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geaviation.techpubs.models.techlib.EngineModelEntity;
import com.geaviation.techpubs.models.techlib.EngineModelProgramEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Table(name = "engine_document_engine_model", schema = "techlib")
@Entity
@IdClass(EngineDocumentEngineModelEntityPK.class)
public class EngineDocumentEngineModelEntity implements Serializable {

    @Id
    @Column(name = "engine_model", nullable = false)
    private String engineModel;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "engine_model", referencedColumnName = "model", nullable = false, insertable = false, updatable = false)
    private EngineModelEntity engineModelEntity;

    @Id
    @Column(name = "engine_document_id", nullable = false)
    private UUID engineDocumentId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "engine_document_id", referencedColumnName = "Id", nullable = false, insertable = false, updatable = false)
    private EngineDocumentEntity engineDocumentEntity;


    public EngineDocumentEngineModelEntity() {
    }


    public String getEngineModel() {
        return engineModel;
    }

    public void setEngineModel(String engineModel) {
        this.engineModel = engineModel;
    }

    public EngineModelEntity getEngineModelEntity() {
        return engineModelEntity;
    }

    public void setEngineModelEntity(EngineModelEntity engineModelEntity) {
        this.engineModelEntity = engineModelEntity;
    }

    public UUID getEngineDocumentId() {
        return engineDocumentId;
    }

    public void setEngineDocumentId(UUID engineDocumentId) {
        this.engineDocumentId = engineDocumentId;
    }

    public EngineDocumentEntity getEngineDocumentEntity() {
        return engineDocumentEntity;
    }

    public void setEngineDocumentEntity(EngineDocumentEntity engineDocumentEntity) {
        this.engineDocumentEntity = engineDocumentEntity;
    }

    public EngineDocumentEngineModelEntity(String engineModel, EngineModelEntity engineModelEntity, UUID engineDocumentId, EngineDocumentEntity engineDocumentEntity) {
        this.engineModel = engineModel;
        this.engineModelEntity = engineModelEntity;
        this.engineDocumentId = engineDocumentId;
        this.engineDocumentEntity = engineDocumentEntity;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EngineDocumentEngineModelEntity that = (EngineDocumentEngineModelEntity) o;
        return Objects.equals(engineModel, that.engineModel) &&
                Objects.equals(engineDocumentId, that.engineDocumentId) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(engineModel, engineDocumentId);
    }
}
