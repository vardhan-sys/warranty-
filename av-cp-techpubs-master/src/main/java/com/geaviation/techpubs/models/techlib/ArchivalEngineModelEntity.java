package com.geaviation.techpubs.models.techlib;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "archival_engine_model_lookup", schema = "techlib")
public class ArchivalEngineModelEntity {

    @Id
    @Column(name = "id", nullable = false)
    @JsonIgnore
    private UUID id;

    @Column(name = "family")
    private String family;

    @Column(name = "model")
    private String model;

    @ManyToMany
    @JoinTable(name = "archival_company_eml",
        joinColumns = @JoinColumn(name = "archival_engine_model_lookup_id"),
        inverseJoinColumns = @JoinColumn(name = "archival_company_id"))
    @JsonIgnore
    private List<ArchivalCompanyEntity> companies;

    @ManyToMany(mappedBy = "engineModels")
    @JsonIgnore
    private List<ArchivalDocumentsEntity> documents;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<ArchivalCompanyEntity> getCompanies() {
        return companies;
    }

    public void setCompanies(List<ArchivalCompanyEntity> companies) {
        this.companies = companies;
    }
}
