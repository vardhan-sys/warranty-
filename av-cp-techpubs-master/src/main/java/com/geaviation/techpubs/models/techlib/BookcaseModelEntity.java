package com.geaviation.techpubs.models.techlib;

import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bookcase_model", schema = "techlib")
public class BookcaseModelEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "bookcase_id", nullable = false)
    private UUID bookcaseId;

    @Column(name = "engine_family", nullable = false, length = -1)
    private String engineFamily;

    @Column(name = "engine_model", nullable = false, length = -1)
    private String engineModel;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getBookcaseId() {
        return bookcaseId;
    }

    public void setBookcaseId(UUID bookcaseId) {
        this.bookcaseId = bookcaseId;
    }

    public String getEngineFamily() {
        return engineFamily;
    }

    public void setEngineFamily(String engineFamily) {
        this.engineFamily = engineFamily;
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
        BookcaseModelEntity that = (BookcaseModelEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(bookcaseId, that.bookcaseId) &&
                Objects.equals(engineFamily, that.engineFamily) &&
                Objects.equals(engineModel, that.engineModel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookcaseId, engineFamily, engineModel);
    }
}
