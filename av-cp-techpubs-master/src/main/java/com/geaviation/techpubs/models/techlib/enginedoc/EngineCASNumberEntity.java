package com.geaviation.techpubs.models.techlib.enginedoc;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "engine_cas_number", schema = "techlib")
public class EngineCASNumberEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "engine_document_id", nullable = false, insertable = false, updatable = false)
    private UUID engineDocumentId;

    @Column(name = "cas_number", nullable = false)
    private String casNumber;

    //Many-to-One Relationship with Engine Document Entity
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "engine_document_id", referencedColumnName = "id", nullable = false)
    private EngineDocumentEntity engineDocumentEntity;


    public EngineCASNumberEntity() {
    }

    public EngineCASNumberEntity(String casNumber, EngineDocumentEntity engineDocumentEntity) {
        this.casNumber = casNumber;
        this.engineDocumentEntity = engineDocumentEntity;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCasNumber() {
        return casNumber;
    }

    public void setCasNumber(String casNumber) {
        this.casNumber = casNumber;
    }

    public UUID getEngineDocumentId() { return engineDocumentId; }

    public void setEngineDocumentId(UUID engineDocumentId) { this.engineDocumentId = engineDocumentId; }

    public EngineDocumentEntity getEngineDocumentEntity() {
        return engineDocumentEntity;
    }

    public void setEngineDocumentEntity(EngineDocumentEntity engineDocumentEntity) {
        this.engineDocumentEntity = engineDocumentEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EngineCASNumberEntity that = (EngineCASNumberEntity) o;
        return casNumber.equals(that.casNumber) && engineDocumentEntity.getId().equals(that.engineDocumentEntity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(casNumber, engineDocumentEntity.getId());
    }
}