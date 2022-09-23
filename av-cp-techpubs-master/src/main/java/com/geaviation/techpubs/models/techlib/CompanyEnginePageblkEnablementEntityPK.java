package com.geaviation.techpubs.models.techlib;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class CompanyEnginePageblkEnablementEntityPK implements Serializable {

    @Id
    @Column(name = "icao_code", nullable = false, length = -1)
    private String icaoCode;

    @Id
    @Column(name = "engine_model", nullable = false, length = -1)
    private String engineModel;

    @Id
    @Column(name = "pageblk_key", nullable = false, length = -1)
    private String pageblkKey;

    @Id
    @Column(name = "section_id", nullable = false, length = -1)
    private UUID sectionId;

    public CompanyEnginePageblkEnablementEntityPK() {}

    public CompanyEnginePageblkEnablementEntityPK(String icaoCode, String engineModel, String pageblkKey, UUID sectionId) {
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

    public UUID getSectionKey() {
        return sectionId;
    }

    public void setSectionKey(UUID sectionKey) {
        this.sectionId = sectionKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyEnginePageblkEnablementEntityPK that = (CompanyEnginePageblkEnablementEntityPK) o;
        return Objects.equals(icaoCode, that.icaoCode) &&
                Objects.equals(engineModel, that.engineModel) &&
                Objects.equals(pageblkKey, that.pageblkKey) &&
                Objects.equals(sectionId, that.sectionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(icaoCode, engineModel, pageblkKey, sectionId);
    }
}
