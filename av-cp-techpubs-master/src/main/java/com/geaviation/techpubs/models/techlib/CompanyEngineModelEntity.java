package com.geaviation.techpubs.models.techlib;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "company_engine_model", schema = "techlib")
@IdClass(CompanyEngineModelEntityPK.class)
public class CompanyEngineModelEntity {

    @Id
    private String icaoCode;

    @Id
    private String engineModel;

    @JsonIgnore
    @Column(name = "created_by", length = -1)
    private String createdBy;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "creation_date")
    private Timestamp creationDate;

    @JsonIgnore
    @Column(name = "last_updated_by", length = -1)
    private String lastUpdatedBy;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "last_updated_date")
    private Timestamp lastUpdatedDate;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "icao_code", referencedColumnName = "icao_code", nullable = false, insertable = false, updatable = false)
    private CompanyEntity companyByIcaoCode;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "engine_model", referencedColumnName = "model", nullable = false, insertable = false, updatable = false)
    private EngineModelEntity modelByEngineModel;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumns(
        {@JoinColumn(name = "icao_code", referencedColumnName = "icao_code", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "engine_model", referencedColumnName = "engine_model", nullable = false, insertable = false, updatable = false)})
    private Set<CompanyEnginePageblkEnablementEntity> companyEnginePageblkEnablementEntities;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumns(
        {@JoinColumn(name = "icao_code", referencedColumnName = "icao_code", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "engine_model", referencedColumnName = "engine_model", nullable = false, insertable = false, updatable = false)})
    private Set<CompanyEngineBookEnablementEntity> companyEngineBookEnablementEntities;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumns(
        {@JoinColumn(name = "icao_code", referencedColumnName = "icao_code", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "engine_model", referencedColumnName = "engine_model", nullable = false, insertable = false, updatable = false)})
    private Set<CompanyEngineTechlvEnablementEntity> companyEngineTechlvEnablementEntities;

    @Transient
    private Boolean previouslyEnabled = Boolean.FALSE;

    public CompanyEngineModelEntity() {}

    public String getIcaoCode() {
        return icaoCode;
    }

    public void setIcaoCode(String companyName) {
        this.icaoCode = companyName;
    }

    public String getEngineModel() {
        return engineModel;
    }

    public void setEngineModel(String engineModel) {
        this.engineModel = engineModel;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
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

    public CompanyEntity getCompanyByIcaoCode() {
        return companyByIcaoCode;
    }

    public void setCompanyByIcaoCode(CompanyEntity companyByCompanyName) { this.companyByIcaoCode = companyByCompanyName; }

    public EngineModelEntity getModelByEngineModel() {
        return modelByEngineModel;
    }

    public void setModelByEngineModel(EngineModelEntity modelByEngineModel) { this.modelByEngineModel = modelByEngineModel; }

    public Boolean isPreviouslyEnabled() {
        return previouslyEnabled;
    }

    public void setPreviouslyEnabled(Boolean previouslyEnabled) {
        this.previouslyEnabled = previouslyEnabled;
    }

    public Set<CompanyEnginePageblkEnablementEntity> getCompanyEnginePageblkEnablementEntities() {
        return companyEnginePageblkEnablementEntities;
    }

    public void setCompanyEnginePageblkEnablementEntities(
        Set<CompanyEnginePageblkEnablementEntity> companyEnginePageblkEnablementEntities) {
        this.companyEnginePageblkEnablementEntities = companyEnginePageblkEnablementEntities;
    }

    public Set<CompanyEngineBookEnablementEntity> getCompanyEngineBookEnablementEntities() {
        return companyEngineBookEnablementEntities;
    }

    public void setCompanyEngineBookEnablementEntities(
        Set<CompanyEngineBookEnablementEntity> companyEngineBookEnablementEntities) {
        this.companyEngineBookEnablementEntities = companyEngineBookEnablementEntities;
    }

    public Set<CompanyEngineTechlvEnablementEntity> getCompanyEngineTechlvEnablementEntities() {
        return companyEngineTechlvEnablementEntities;
    }

    public void setCompanyEngineTechlvEnablementEntities(
        Set<CompanyEngineTechlvEnablementEntity> companyEngineTechlvEnablementEntities) {
        this.companyEngineTechlvEnablementEntities = companyEngineTechlvEnablementEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyEngineModelEntity that = (CompanyEngineModelEntity) o;
        return Objects.equals(icaoCode, that.icaoCode) &&
                Objects.equals(engineModel, that.engineModel) &&
                Objects.equals(createdBy, that.createdBy) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(lastUpdatedBy, that.lastUpdatedBy) &&
                Objects.equals(lastUpdatedDate, that.lastUpdatedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(icaoCode, engineModel, createdBy, creationDate, lastUpdatedBy, lastUpdatedDate);
    }
}
