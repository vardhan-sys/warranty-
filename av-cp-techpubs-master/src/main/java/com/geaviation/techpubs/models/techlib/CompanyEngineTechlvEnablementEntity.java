package com.geaviation.techpubs.models.techlib;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;
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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "company_engine_techlv_enablement", schema = "techlib")
@IdClass(CompanyEngineTechlvEnablementEntityPK.class)
public class CompanyEngineTechlvEnablementEntity {

    @Id
    @Column(name = "icao_code", nullable = false, length = -1)
    private String icaoCode;

    @Id
    @Column(name = "engine_model", nullable = false, length = -1)
    private String engineModel;

    @Id
    @Column(name = "techlv_id", nullable = false)
    private UUID technologyLevelId;

    @Id
    @Column(name = "bookcase_key", nullable = false)
    private String bookcaseKey;

    @JsonIgnore
    @Column(name = "created_by", length = -1)
    private String createdBy;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;

    @JsonIgnore
    @Column(name = "last_updated_by", length = -1)
    private String lastUpdatedBy;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "last_updated_at")
    private Timestamp lastUpdatedAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "techlv_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private TechnologyLevelEntity techLvById;

    public CompanyEngineTechlvEnablementEntity() {}

    public CompanyEngineTechlvEnablementEntity(String icaoCode, String engineModel, UUID technologyLevelId, String bookcaseKey) {
        this.icaoCode = icaoCode;
        this.engineModel = engineModel;
        this.technologyLevelId = technologyLevelId;
        this.bookcaseKey = bookcaseKey;
    }

    public String getIcaoCode() {
        return icaoCode;
    }

    public void setIcaoCode(String icaoCode) {
        this.icaoCode = icaoCode;
    }

    public String getEngineModel() {
        return engineModel;
    }

    public void setEngineModel(String engineModel) {
        this.engineModel = engineModel;
    }

    public UUID getTechnologyLevelId() {
        return technologyLevelId;
    }

    public void setTechnologyLevelId(UUID technologyLevelId) {
        this.technologyLevelId = technologyLevelId;
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

    public TechnologyLevelEntity getTechLvById() {
        return techLvById;
    }

    public void setTechLvById(TechnologyLevelEntity techLvById) {
        this.techLvById = techLvById;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyEngineTechlvEnablementEntity that = (CompanyEngineTechlvEnablementEntity) o;
        return Objects.equals(icaoCode, that.icaoCode) &&
                Objects.equals(engineModel, that.engineModel) &&
                Objects.equals(technologyLevelId, that.technologyLevelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(icaoCode, engineModel, technologyLevelId, createdBy, createdAt, lastUpdatedBy, lastUpdatedAt);
    }
}
