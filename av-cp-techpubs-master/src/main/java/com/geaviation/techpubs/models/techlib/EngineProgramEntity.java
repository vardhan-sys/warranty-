package com.geaviation.techpubs.models.techlib;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "engine_program", schema = "techlib")
public class EngineProgramEntity {
    @Id
    @Column(name = "bookcase_key", nullable = false, length = -1)
    private String bookcaseKey;

    @Column(name = "program", nullable = false, length = -1)
    private String program;

    @Column(name = "offline_filename", nullable = false, length = -1)
    private String offlineFilename;

    @JsonIgnore
    @Column(name = "created_by", length = -1)
    private String createdBy;

    @JsonIgnore
    @Column(name = "created_at")
    private Timestamp createdAt;

    @JsonIgnore
    @Column(name = "last_updated_by", length = -1)
    private String lastUpdatedBy;

    @JsonIgnore
    @Column(name = "last_updated_at")
    private Timestamp lastUpdatedAt;
    
    public String getBookcaseKey() {
        return bookcaseKey;
    }

    public void setBookcaseKey(String bookcaseKey) {
        this.bookcaseKey = bookcaseKey;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getOfflineFilename() { return offlineFilename; }

    public void setOfflineFilename(String offlineFilename) { this.offlineFilename = offlineFilename; }

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
        EngineProgramEntity that = (EngineProgramEntity) o;
        return Objects.equals(bookcaseKey, that.bookcaseKey) &&
                Objects.equals(program, that.program) &&
                Objects.equals(offlineFilename, that.offlineFilename) &&
                Objects.equals(createdBy, that.createdBy) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(lastUpdatedBy, that.lastUpdatedBy) &&
                Objects.equals(lastUpdatedAt, that.lastUpdatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookcaseKey, program, offlineFilename, createdBy, createdAt, lastUpdatedBy, lastUpdatedAt);
    }
}
