package com.geaviation.techpubs.models.techlib;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "publication_access_level_lookup", schema = "techlib")
public class PublicationAccessLevelLookupEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "value", nullable = false, unique = true)
    private String value;

    @Column(name = "last_updated_by", nullable = false)
    private String lastUpdatedBy;

    @Column(name = "last_updated_date", nullable = false)
    private LocalDate lastUpdatedDate;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST}, mappedBy = "publicationAccessLevelLookupEntity")
    private Set<PublicationAccessLevelDocumentTypeEntity> publicationAccessLevelDocumentTypeEntities = new HashSet<PublicationAccessLevelDocumentTypeEntity>();

    @JsonIgnore
    @OneToOne(mappedBy = "publicationAccessLevelLookupEntity")
    private SalesforceCompanyAirframeEntitlementEntity salesforceCompanyAirframeEntitlementEntity;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public LocalDate getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(LocalDate lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Set<PublicationAccessLevelDocumentTypeEntity> getAgreementSubtypeDocumentTypeEntity() {
        return publicationAccessLevelDocumentTypeEntities;
    }

    public void setAgreementSubtypeDocumentTypeEntity(Set<PublicationAccessLevelDocumentTypeEntity> agreementSubtypeDocumentTypeEntity) {
        this.publicationAccessLevelDocumentTypeEntities = agreementSubtypeDocumentTypeEntity;
    }

    public SalesforceCompanyAirframeEntitlementEntity getSalesforceCompanyAirframeEntitlementEntity() {
        return salesforceCompanyAirframeEntitlementEntity;
    }

    public void setSalesforceCompanyAirframeEntitlementEntity(SalesforceCompanyAirframeEntitlementEntity salesforceCompanyAirframeEntitlementEntity) {
        this.salesforceCompanyAirframeEntitlementEntity = salesforceCompanyAirframeEntitlementEntity;
    }
}
