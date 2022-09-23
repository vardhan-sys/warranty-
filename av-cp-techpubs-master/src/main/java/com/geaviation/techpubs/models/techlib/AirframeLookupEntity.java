package com.geaviation.techpubs.models.techlib;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "airframe_lookup", schema = "techlib")
public class AirframeLookupEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "airframe", nullable = false)
    private String airframe;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "airframe_system_document",
            joinColumns = {@JoinColumn(name = "airframe_id")},
            inverseJoinColumns = {@JoinColumn(name = "system_document_id")})
    private Set<SystemDocumentEntity> systemDocuments;

    @JsonIgnore
    @OneToMany(mappedBy = "airframeLookup")
    private Set<SalesforceCompanyAirframeEntitlementEntity> salesforceCompanyAirframeEntitlementEntity = new HashSet<SalesforceCompanyAirframeEntitlementEntity>();

    public AirframeLookupEntity() {
    }

    public AirframeLookupEntity(UUID id, String airframe, Set<SystemDocumentEntity> systemDocumentEntities, Set<SalesforceCompanyAirframeEntitlementEntity> salesforceCompanyAirframeEntitlementEntity) {
        this.id = id;
        this.airframe = airframe;
        this.systemDocuments = systemDocumentEntities;
        this.salesforceCompanyAirframeEntitlementEntity = salesforceCompanyAirframeEntitlementEntity;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAirframe() {
        return airframe;
    }

    public void setAirframe(String airframe) {
        this.airframe = airframe;
    }

    public Set<SystemDocumentEntity> getSystemDocuments() {
        return systemDocuments;
    }

    public void setSystemDocuments(Set<SystemDocumentEntity> systemDocuments) {
        this.systemDocuments = systemDocuments;
    }

    public Set<SalesforceCompanyAirframeEntitlementEntity> getSalesforceCompanyAirframeEntitlementEntity() {
        return salesforceCompanyAirframeEntitlementEntity;
    }

    public void setSalesforceCompanyAirframeEntitlementEntity(Set<SalesforceCompanyAirframeEntitlementEntity> salesforceCompanyAirframeEntitlementEntity) {
        this.salesforceCompanyAirframeEntitlementEntity = salesforceCompanyAirframeEntitlementEntity;
    }
}