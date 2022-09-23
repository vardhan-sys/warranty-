package com.geaviation.techpubs.models.techlib;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "archival_company", schema = "techlib")
public class ArchivalCompanyEntity {

    @Id
    @Column(name = "id", nullable = false)
    @JsonIgnore
    private UUID id;

    @Column(name = "icao_code")
    private String icaoCode;

    @ManyToMany(mappedBy = "companies")
    @JsonIgnore
    private List<ArchivalEngineModelEntity> entitlements;

    public ArchivalCompanyEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getIcaoCode() {
        return icaoCode;
    }

    public void setIcaoCode(String icaoCode) {
        this.icaoCode = icaoCode;
    }

    public List<ArchivalEngineModelEntity> getEntitlements() {
        return entitlements;
    }

    public void setEntitlements(List<ArchivalEngineModelEntity> entitlements) {
        this.entitlements = entitlements;
    }
}
