package com.geaviation.techpubs.models.techlib;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Id;

public class CompanyEngineTechlvEnablementEntityPK implements Serializable {

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
    @Column(name = "bookcase_key", nullable = false, length = -1)
    private String bookcaseKey;

    public CompanyEngineTechlvEnablementEntityPK() {}

    public CompanyEngineTechlvEnablementEntityPK(String icaoCode, String engineModel, UUID technologyLevelId, String bookcaseKey) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyEngineTechlvEnablementEntityPK that = (CompanyEngineTechlvEnablementEntityPK) o;
        return Objects.equals(icaoCode, that.icaoCode) &&
                Objects.equals(engineModel, that.engineModel) &&
                Objects.equals(technologyLevelId, that.technologyLevelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(icaoCode, engineModel, technologyLevelId);
    }
}
