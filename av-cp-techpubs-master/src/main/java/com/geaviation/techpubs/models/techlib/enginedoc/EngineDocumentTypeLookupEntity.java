package com.geaviation.techpubs.models.techlib.enginedoc;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "engine_document_type_lookup", schema = "techlib")
public class EngineDocumentTypeLookupEntity {
    @Id
    @Column(name = "id", nullable = false, length = -1)
    private UUID id;

    @Column(name = "value", nullable = false, length = -1)
    private String value;

    @JsonIgnore
    @OneToMany(mappedBy = "engineDocumentTypeLookupEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<EngineDocumentEntity> engineDocumentEntity = new HashSet<EngineDocumentEntity>();


    public EngineDocumentTypeLookupEntity() {
    }

    public EngineDocumentTypeLookupEntity(UUID id, String value, Set<EngineDocumentEntity> engineDocumentEntity) {
        this.id = id;
        this.value = value;
        this.engineDocumentEntity = engineDocumentEntity;
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

    public Set<EngineDocumentEntity> getEngineDocumentEntity() {
        return engineDocumentEntity;
    }

    public void setEngineDocumentEntity(Set<EngineDocumentEntity> engineDocumentEntity) {
        this.engineDocumentEntity = engineDocumentEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EngineDocumentTypeLookupEntity that = (EngineDocumentTypeLookupEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value);
    }

}
