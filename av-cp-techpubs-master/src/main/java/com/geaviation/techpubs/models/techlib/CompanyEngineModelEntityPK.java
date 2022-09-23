package com.geaviation.techpubs.models.techlib;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Id;

public class CompanyEngineModelEntityPK implements Serializable {

    @Id
    @Column(name = "icao_code", nullable = false, length = -1)
    private String icaoCode;

    @Id
    @Column(name = "engine_model", nullable = false, length = -1)
    private String engineModel;

    public CompanyEngineModelEntityPK() {}

    public CompanyEngineModelEntityPK(String icaoCode, String engineModel) {
        this.icaoCode = icaoCode;
        this.engineModel = engineModel;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyEngineModelEntityPK that = (CompanyEngineModelEntityPK) o;
        return Objects.equals(icaoCode, that.icaoCode) &&
                Objects.equals(engineModel, that.engineModel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(icaoCode, engineModel);
    }
}
