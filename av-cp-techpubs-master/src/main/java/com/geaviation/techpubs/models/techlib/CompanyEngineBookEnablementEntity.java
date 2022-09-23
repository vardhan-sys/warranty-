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
@Table(name = "company_engine_book_enablement", schema = "techlib")
@IdClass(CompanyEngineBookEnablementEntityPK.class)
public class CompanyEngineBookEnablementEntity {

    @Id
    @Column(name = "icao_code", nullable = false, length = -1)
    private String icaoCode;

    @Id
    @Column(name = "engine_model", nullable = false, length = -1)
    private String engineModel;

    @Id
    @Column(name = "book_id", nullable = false, length = -1)
    private UUID bookId;

    @JsonIgnore
    @Column(name = "created_by", length = -1)
    private String createdBy;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_at", length = -1)
    private Timestamp createdAt;

    @JsonIgnore
    @Column(name = "last_updated_by", length = -1)
    private String lastUpdatedBy;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "last_updated_at")
    private Timestamp lastUpdatedAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "book_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private BookEntity bookByBookId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "icao_code", referencedColumnName = "icao_code", nullable = false, insertable = false, updatable = false)
    private CompanyEntity companyByIcaoCode;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "engine_model", referencedColumnName = "model", nullable = false, insertable = false, updatable = false)
    private EngineModelEntity modelByEngineModel;

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

    public UUID getBookId() {
        return bookId;
    }

    public void setBookId(UUID bookId) {
        this.bookId = bookId;
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

    public BookEntity getBookByBookId() {
        return bookByBookId;
    }

    public void setBookByBookId(BookEntity bookByBookId) {
        this.bookByBookId = bookByBookId;
    }

    public CompanyEntity getCompanyByIcaoCode() {
        return companyByIcaoCode;
    }

    public void setCompanyByIcaoCode(
        CompanyEntity companyByCompanyName) {
        this.companyByIcaoCode = companyByCompanyName;
    }

    public EngineModelEntity getModelByEngineModel() {
        return modelByEngineModel;
    }

    public void setModelByEngineModel(
        EngineModelEntity modelByEngineModel) {
        this.modelByEngineModel = modelByEngineModel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyEngineBookEnablementEntity that = (CompanyEngineBookEnablementEntity) o;
        return Objects.equals(icaoCode, that.icaoCode) &&
                Objects.equals(engineModel, that.engineModel) &&
                Objects.equals(bookId, that.bookId) &&
                Objects.equals(createdBy, that.createdBy) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(lastUpdatedBy, that.lastUpdatedBy) &&
                Objects.equals(lastUpdatedAt, that.lastUpdatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(icaoCode, engineModel, bookId, createdBy, createdAt, lastUpdatedBy, lastUpdatedAt);
    }
}
