package com.geaviation.techpubs.models.techlib;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "salesforce_company_airframe_entitlement", schema = "techlib")
public class SalesforceCompanyAirframeEntitlementEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "airframe_id", nullable = false, insertable = false, updatable = false)
    private AirframeLookupEntity airframeLookup;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "salesforce_company_id", nullable = false, insertable = false, updatable = false)
    private SalesforceCompanyLookupEntity salesforceCompanyLookupEntity;

    @Column(name = "agreement_type", nullable = false)
    private String agreementType;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "publication_access_level_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private PublicationAccessLevelLookupEntity publicationAccessLevelLookupEntity;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "entitlement_status", nullable = false)
    private String entitlementStatus;

    @Column(name = "publication_access_level")
    private String publicationAccessLevel;

    @Column(name = "publications")
    private Boolean publications;

    @Column(name = "original_insert_date")
    private LocalDate originalInsertDate;

    @Column(name = "entitlement_id")
    private String entitlementId;

    @Column(name = "last_modified_date")
    private LocalDate lastModifiedDate;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public AirframeLookupEntity getAirframeLookup() {
        return airframeLookup;
    }

    public void setAirframeLookup(AirframeLookupEntity airframeLookup) {
        this.airframeLookup = airframeLookup;
    }

    public SalesforceCompanyLookupEntity getSalesforceCompanyLookupEntity() {
        return salesforceCompanyLookupEntity;
    }

    public void setSalesforceCompanyLookupEntity(SalesforceCompanyLookupEntity salesforceCompanyLookupEntity) {
        this.salesforceCompanyLookupEntity = salesforceCompanyLookupEntity;
    }

    public String getAgreementType() {
        return agreementType;
    }

    public void setAgreementType(String agreementType) {
        this.agreementType = agreementType;
    }

    public PublicationAccessLevelLookupEntity getPublicationAccessLevelLookupEntity() {
        return publicationAccessLevelLookupEntity;
    }

    public void setPublicationAccessLevelLookupEntity(PublicationAccessLevelLookupEntity publicationAccessLevelLookupEntity) {
        this.publicationAccessLevelLookupEntity = publicationAccessLevelLookupEntity;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getEntitlementStatus() {
        return entitlementStatus;
    }

    public void setEntitlementStatus(String entitlementStatus) {
        this.entitlementStatus = entitlementStatus;
    }

    public String getPublicationAccessLevel() {
        return publicationAccessLevel;
    }

    public void setPublicationAccessLevel(String publicationAccessLevel) {
        this.publicationAccessLevel = publicationAccessLevel;
    }

    public Boolean getPublications() {
        return publications;
    }

    public void setPublications(Boolean publications) {
        this.publications = publications;
    }

    public LocalDate getOriginalInsertDate() {
        return originalInsertDate;
    }

    public void setOriginalInsertDate(LocalDate originalInsertDate) {
        this.originalInsertDate = originalInsertDate;
    }

    public String getEntitlementId() {return entitlementId;}

    public void setEntitlementId(String entitlementId) {this.entitlementId = entitlementId;}

    public LocalDate getLastModifiedDate() {return lastModifiedDate;}

    public void setLastModifiedDate(LocalDate lastModifiedDate) {this.lastModifiedDate = lastModifiedDate;}
}