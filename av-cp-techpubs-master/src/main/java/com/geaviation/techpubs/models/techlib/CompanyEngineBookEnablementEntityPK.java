package com.geaviation.techpubs.models.techlib;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Id;

public class CompanyEngineBookEnablementEntityPK implements Serializable {

    @Id
    @Column(name = "icao_code", nullable = false, length = -1)
    private String icaoCode;

    @Id
    @Column(name = "engine_model", nullable = false, length = -1)
    private String engineModel;

    @Id
    @Column(name = "book_id", nullable = false)
    private UUID bookId;

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

    public UUID getBookId() {
        return bookId;
    }

    public void setBookId(UUID bookId) {
        this.bookId = bookId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyEngineBookEnablementEntityPK that = (CompanyEngineBookEnablementEntityPK) o;
        return Objects.equals(icaoCode, that.icaoCode) &&
                Objects.equals(engineModel, that.engineModel) &&
                Objects.equals(bookId, that.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(icaoCode, engineModel, bookId);
    }
}
