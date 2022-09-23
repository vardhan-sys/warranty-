package com.geaviation.techpubs.models.techlib;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Table(name = "system_document", schema = "techlib")
@Entity
public class SystemDocumentEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_document_type_id", referencedColumnName = "id")
    private SystemDocumentTypeLookupEntity systemDocumentTypeLookupEntity;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_document_site_id", referencedColumnName = "id")
    private SystemDocumentSiteLookupEntity systemDocumentSiteLookupEntity;


    @Column(name = "document_number", nullable = false)
    private String documentNumber;

    @ManyToMany
    @JoinTable(name = "company_system_document",
            joinColumns = {@JoinColumn(name = "system_document_id")},
            inverseJoinColumns = {@JoinColumn(name = "company_id")})
    private Set<SalesforceCompanyLookupEntity> specificCompanies;
    
    @ManyToMany
    @JoinTable(name = "company_paid_subscription_document",
            joinColumns = {@JoinColumn(name = "system_document_id")},
            inverseJoinColumns = {@JoinColumn(name = "company_id")})
    private Set<SalesforceCompanyLookupEntity> companyPaidSubscription;

    @JsonIgnore
    @OneToMany(mappedBy = "systemDocumentEntity", cascade = {CascadeType.ALL},
            orphanRemoval=true)
    private Set<PartNumbersAffectedEntity> partNumbersAffectedEntity = new HashSet<PartNumbersAffectedEntity>();

    @ManyToMany
    @JoinTable(name = "airframe_system_document",
        joinColumns = {@JoinColumn(name = "system_document_id")},
        inverseJoinColumns = {@JoinColumn(name = "airframe_id")})
    private Set<AirframeLookupEntity> airframes;

    @Column(name = "document_description", nullable = false)
    private String documentDescription;

    @Column(name = "revision_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date revisionDate;

    @Column(name = "revision")
    private String revision;

    @Column(name = "distribution_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date distributionDate;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "s3_file_path")
    private String s3FilePath;

    @Column(name = "company_specific", nullable = false)
    private Boolean companySpecific;

    @Column(name = "email_notification", nullable = false)
    private Boolean emailNotification;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @Column(name = "last_email_sent_date")
    private Date lastEmailSentDate;

    @Column (name = "power_document")
    private Boolean powerDocument;

    public SystemDocumentEntity() {
    }

    public SystemDocumentEntity(UUID id, SystemDocumentTypeLookupEntity systemDocumentTypeLookupEntity, SystemDocumentSiteLookupEntity systemDocumentSiteLookupEntity, String documentNumber, Set<SalesforceCompanyLookupEntity> salesforceCompanyLookupEntities, Set<PartNumbersAffectedEntity> partNumbersAffectedEntity, Set<AirframeLookupEntity> airframes, String documentDescription, Date revisionDate, String revision, Date distributionDate, String fileName, String s3FilePath, Boolean companySpecific, Boolean emailNotification, Date lastEmailSentDate, Boolean powerDocument) {
        this.id = id;
        this.systemDocumentTypeLookupEntity = systemDocumentTypeLookupEntity;
        this.systemDocumentSiteLookupEntity = systemDocumentSiteLookupEntity;
        this.documentNumber = documentNumber;
        this.specificCompanies = salesforceCompanyLookupEntities;
        this.partNumbersAffectedEntity = partNumbersAffectedEntity;
        this.airframes = airframes;
        this.documentDescription = documentDescription;
        this.revisionDate = revisionDate;
        this.revision = revision;
        this.distributionDate = distributionDate;
        this.fileName = fileName;
        this.s3FilePath = s3FilePath;
        this.companySpecific = companySpecific;
        this.emailNotification = emailNotification;
        this.lastEmailSentDate =lastEmailSentDate;
        this.powerDocument = powerDocument;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public SystemDocumentTypeLookupEntity getSystemDocumentTypeLookupEntity() {
        return systemDocumentTypeLookupEntity;
    }

    public void setSystemDocumentTypeLookupEntity(SystemDocumentTypeLookupEntity systemDocumentTypeLookupEntity) {
        this.systemDocumentTypeLookupEntity = systemDocumentTypeLookupEntity;
    }

    public SystemDocumentSiteLookupEntity getSystemDocumentSiteLookupEntity() {
        return systemDocumentSiteLookupEntity;
    }

    public void setSystemDocumentSiteLookupEntity(SystemDocumentSiteLookupEntity systemDocumentSiteLookupEntity) {
        this.systemDocumentSiteLookupEntity = systemDocumentSiteLookupEntity;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Set<SalesforceCompanyLookupEntity> getSpecificCompanies() {
        return specificCompanies;
    }

    public void setSpecificCompanies(Set<SalesforceCompanyLookupEntity> specificCompanies) {
        this.specificCompanies = specificCompanies;
    }

    public Set<PartNumbersAffectedEntity> getPartNumbersAffectedEntity() {
        return partNumbersAffectedEntity;
    }

    public void setPartNumbersAffectedEntity(Set<PartNumbersAffectedEntity> partNumbersAffectedEntity) {
        this.partNumbersAffectedEntity = partNumbersAffectedEntity;
    }

    public void setPartNumbers(List<String> partNumbers) {
        // Adding all the new ones if not already in list
        for (String partNumber : partNumbers) {
            PartNumbersAffectedEntity partNumbersAffectedEntity = this.partNumbersAffectedEntity.stream()
                    .filter(p -> p.getPartNumber().equals(partNumber))
                    .findFirst()
                    .orElse(null);
            if (partNumbersAffectedEntity == null) {
                this.partNumbersAffectedEntity.add(new PartNumbersAffectedEntity(partNumber, this));
            }
        }

        // Remove old ones not present in new list
        this.partNumbersAffectedEntity.removeIf(p -> !partNumbers.contains(p.getPartNumber()));
    }

    public Set<AirframeLookupEntity> getAirframes() {
        return airframes;
    }

    public void setAirframes(Set<AirframeLookupEntity> airframes) {
        this.airframes = airframes;
    }

    public String getDocumentDescription() {
        return documentDescription;
    }

    public void setDocumentDescription(String documentDescription) {
        this.documentDescription = documentDescription;
    }

    public Date getRevisionDate() {
        return revisionDate;
    }

    public void setRevisionDate(Date revisionDate) {
        this.revisionDate = revisionDate;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public Date getDistributionDate() {
        return distributionDate;
    }

    public void setDistributionDate(Date distributionDate) {
        this.distributionDate = distributionDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getS3FilePath() {
        return s3FilePath;
    }

    public void setS3FilePath(String s3FilePath) {
        this.s3FilePath = s3FilePath;
    }

    public Boolean getCompanySpecific() {
        return companySpecific;
    }

    public void setCompanySpecific(Boolean companySpecific) {
        this.companySpecific = companySpecific;
    }

    public Boolean getEmailNotification() {
        return emailNotification;
    }

    public void setEmailNotification(Boolean emailNotification) {
        this.emailNotification = emailNotification;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

	public Set<SalesforceCompanyLookupEntity> getCompanyPaidSubscription() {
		return companyPaidSubscription;
	}

	public void setCompanyPaidSubscription(Set<SalesforceCompanyLookupEntity> companyPaidSubscription) {
		this.companyPaidSubscription = companyPaidSubscription;
	}

    public Date getLastEmailSentDate() { return lastEmailSentDate; }

    public void setLastEmailSentDate(Date lastEmailSentDate) { this.lastEmailSentDate = lastEmailSentDate; }

    public Boolean getPowerDocument() { return powerDocument; }

    public void setPowerDocument(Boolean powerDocument) { this.powerDocument = powerDocument; }

}
