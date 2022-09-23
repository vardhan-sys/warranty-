package com.geaviation.techpubs.models.techlib;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "salesforce_company_lookup", schema = "techlib")
public class SalesforceCompanyLookupEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "salesforce_id", nullable = false, unique = true)
    private String salesforceId;

    @Column(name = "company_name", nullable = false, unique = true)
    private String companyName;

    @Column(name = "icao_code")
    private String icaoCode;

    @Column(name = "duns_number")
    private String dunsNumber;

    @Column(name = "last_update_date")
    private LocalDate lastUpdateDate;

    @Column(name="enabled", nullable = true)
    private boolean enabled;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST}, mappedBy = "salesforceCompanyLookupEntity")
    private Set<SalesforceCompanyAirframeEntitlementEntity> salesforceCompanyAirframeEntitlementEntity = new HashSet<SalesforceCompanyAirframeEntitlementEntity>();

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "company_system_document",
            joinColumns = {@JoinColumn(name = "system_document_id")},
            inverseJoinColumns = {@JoinColumn(name = "company_id")})
    private Set<SystemDocumentEntity> systemDocuments;
    
    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "company_paid_subscription_document",
            joinColumns = {@JoinColumn(name = "system_document_id")},
            inverseJoinColumns = {@JoinColumn(name = "company_id")})
    private Set<SystemDocumentEntity> companyPaidSubscriptionDocument;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSalesforceId() {
        return salesforceId;
    }

    public void setSalesforceId(String salesforceId) {
        this.salesforceId = salesforceId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getIcaoCode() {
        return icaoCode;
    }

    public void setIcaoCode(String icaoCode) {
        this.icaoCode = icaoCode;
    }

    public String getDunsNumber() {
        return dunsNumber;
    }

    public void setDunsNumber(String dunsNumber) {
        this.dunsNumber = dunsNumber;
    }

    public LocalDate getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(LocalDate lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<SalesforceCompanyAirframeEntitlementEntity> getSalesforceCompanyAirframeEntitlementEntity() {
        return salesforceCompanyAirframeEntitlementEntity;
    }

    public void setSalesforceCompanyAirframeEntitlementEntity(Set<SalesforceCompanyAirframeEntitlementEntity> salesforceCompanyAirframeEntitlementEntity) {
        this.salesforceCompanyAirframeEntitlementEntity = salesforceCompanyAirframeEntitlementEntity;
    }

    public Set<SystemDocumentEntity> getSystemDocuments() {
        return systemDocuments;
    }

    public void setSystemDocuments(Set<SystemDocumentEntity> systemDocuments) {
        this.systemDocuments = systemDocuments;
    }

	public Set<SystemDocumentEntity> getCompanyPaidSubscriptionDocument() {
		return companyPaidSubscriptionDocument;
	}

	public void setCompanyPaidSubscriptionDocument(Set<SystemDocumentEntity> companyPaidSubscriptionDocument) {
		this.companyPaidSubscriptionDocument = companyPaidSubscriptionDocument;
	}
    
    
    
    
}