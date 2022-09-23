package com.geaviation.techpubs.models.techlib;

import java.sql.Timestamp;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "engine_model_program", schema = "techlib")
@IdClass(EngineModelProgramEntityPK.class)
public class EngineModelProgramEntity {

    @Id
    @Column(name = "engine_model", nullable = false, length = -1)
    private String engineModel;

    @Id
    @Column(name = "bookcase_key", nullable = false, length = -1)
    private String bookcaseKey;

    @JsonIgnore
    @Column(name = "created_by", length = -1)
    private String createdBy;

    @JsonIgnore
    @Column(name = "created_at")
    private Timestamp createdAt;

    @JsonIgnore
    @Column(name = "last_updated_by", length = -1)
    private String lastUpdatedBy;

    @JsonIgnore
    @Column(name = "last_updated_at")
    private Timestamp lastUpdatedAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "engine_model", referencedColumnName = "model", nullable = false, insertable = false, updatable = false)
    private EngineModelEntity engineModelByEngineModel;

    public String getEngineModel() {
        return engineModel;
    }

    public void setEngineModel(String engineModel) {
        this.engineModel = engineModel;
    }

    public String getBookcaseKey() {
        return bookcaseKey;
    }

    public void setBookcaseKey(String bookcaseKey) {
        this.bookcaseKey = bookcaseKey;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Timestamp getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(Timestamp lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public EngineModelEntity getEngineModelByEngineModel() {
        return engineModelByEngineModel;
    }

    public void setEngineModelByEngineModel(EngineModelEntity engineModelByEngineModel) {
        this.engineModelByEngineModel = engineModelByEngineModel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EngineModelProgramEntity that = (EngineModelProgramEntity) o;
        return Objects.equals(engineModel, that.engineModel) &&
                Objects.equals(bookcaseKey, that.bookcaseKey) &&
                Objects.equals(createdBy, that.createdBy) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(lastUpdatedBy, that.lastUpdatedBy) &&
                Objects.equals(lastUpdatedAt, that.lastUpdatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(engineModel, bookcaseKey, createdBy, createdAt, lastUpdatedBy, lastUpdatedAt);
    }
}
