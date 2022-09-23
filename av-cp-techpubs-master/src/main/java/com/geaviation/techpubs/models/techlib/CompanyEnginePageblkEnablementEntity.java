package com.geaviation.techpubs.models.techlib;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "company_engine_pageblk_enablement", schema = "techlib")
@IdClass(CompanyEnginePageblkEnablementEntityPK.class)
public class CompanyEnginePageblkEnablementEntity {

    @Id
    @Column(name = "icao_code", nullable = false, length = -1)
    private String icaoCode;

    @Id
    @Column(name = "engine_model", nullable = false, length = -1)
    private String engineModel;

    @Id
    @Column(name = "pageblk_key", nullable = false)
    private String pageblkKey;

    @Id
    @Column(name = "section_id", nullable = false)
    private UUID sectionId;

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

    public CompanyEnginePageblkEnablementEntity() {}

    public CompanyEnginePageblkEnablementEntity(String icaoCode, String engineModel, String pageblkKey, UUID sectionId) {
        this.icaoCode = icaoCode;
        this.engineModel = engineModel;
        this.pageblkKey = pageblkKey;
        this.sectionId = sectionId;
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

    public String getPageblkKey() {
        return pageblkKey;
    }

    public void setPageblkKey(String pageblkKey) {
        this.pageblkKey = pageblkKey;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyEnginePageblkEnablementEntity that = (CompanyEnginePageblkEnablementEntity) o;
        return Objects.equals(icaoCode, that.icaoCode) &&
                Objects.equals(engineModel, that.engineModel) &&
                Objects.equals(pageblkKey, that.pageblkKey) &&
                Objects.equals(sectionId, that.sectionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(icaoCode, engineModel, pageblkKey, sectionId, createdBy, createdAt, lastUpdatedBy, lastUpdatedAt);
    }
}
