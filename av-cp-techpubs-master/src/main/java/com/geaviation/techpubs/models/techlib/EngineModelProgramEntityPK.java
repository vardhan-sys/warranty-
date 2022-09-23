package com.geaviation.techpubs.models.techlib;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Id;

public class EngineModelProgramEntityPK implements Serializable {
    @Id
    @Column(name = "engine_model", nullable = false, length = -1)
    private String engineModel;

    @Id
    @Column(name = "bookcase_key", nullable = false, length = -1)
    private String bookcaseKey;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EngineModelProgramEntityPK that = (EngineModelProgramEntityPK) o;
        return Objects.equals(engineModel, that.engineModel) &&
                Objects.equals(bookcaseKey, that.bookcaseKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(engineModel, bookcaseKey);
    }
}
