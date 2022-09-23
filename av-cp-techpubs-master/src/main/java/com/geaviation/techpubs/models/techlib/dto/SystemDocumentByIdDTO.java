package com.geaviation.techpubs.models.techlib.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public class SystemDocumentByIdDTO {

    private String systemDocumentId;
    private String documentTitle;
    private String documentSite;
    private String documentSiteID;
    private String documentType;
    private String documentTypeID;
    private String documentNumber;
    private String documentRevision;
    private String fileName;
    private List<String> partsAffected;
    private List<AirframeDto> aircraftPlatforms;
    private List<SalesforceCompanyDto> specificCustomers;
    private List<SalesforceCompanyDto> companyPaidSubscription;
    private Boolean emailNotification;
    private Boolean companySpecific;
    private Boolean powerDocument;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date documentRevisionDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date documentDistributionDate;

    public SystemDocumentByIdDTO() {
    }

    public SystemDocumentByIdDTO(String systemDocumentId, String documentTitle, String documentSite,
                                 String documentSiteID, String documentType, String documentTypeID, String documentNumber,
                                 String documentRevision, String fileName, List<String> partsAffected,
                                 List<AirframeDto> aircraftPlatforms, List<SalesforceCompanyDto> specificCustomers,
                                 List<SalesforceCompanyDto> companyPaidSubscription, Boolean emailNotification,
                                 Boolean companySpecific, Date documentRevisionDate, Date documentDistributionDate,
                                 Boolean powerDocument) {
        this.systemDocumentId = systemDocumentId;
        this.documentTitle = documentTitle;
        this.documentSite = documentSite;
        this.documentSiteID = documentSiteID;
        this.documentType = documentType;
        this.documentTypeID = documentTypeID;
        this.documentNumber = documentNumber;
        this.documentRevision = documentRevision;
        this.fileName = fileName;
        this.partsAffected = partsAffected;
        this.aircraftPlatforms = aircraftPlatforms;
        this.specificCustomers = specificCustomers;
        this.emailNotification = emailNotification;
        this.companySpecific = companySpecific;
        this.companyPaidSubscription = companyPaidSubscription;
        this.documentRevisionDate = documentRevisionDate;
        this.documentDistributionDate = documentDistributionDate;
        this.powerDocument = powerDocument;
    }

    public String getSystemDocumentId() {
        return systemDocumentId;
    }

    public void setSystemDocumentId(String systemDocumentId) {
        this.systemDocumentId = systemDocumentId;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getDocumentSite() {
        return documentSite;
    }

    public void setDocumentSite(String documentSite) {
        this.documentSite = documentSite;
    }

    public String getDocumentSiteID() {
        return documentSiteID;
    }

    public void setDocumentSiteID(String documentSiteID) {
        this.documentSiteID = documentSiteID;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentTypeID() {
        return documentTypeID;
    }

    public void setDocumentTypeID(String documentTypeID) {
        this.documentTypeID = documentTypeID;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentRevision() {
        return documentRevision;
    }

    public void setDocumentRevision(String documentRevision) {
        this.documentRevision = documentRevision;
    }

    public List<String> getPartsAffected() {
        return partsAffected;
    }

    public void setPartsAffected(List<String> partsAffected) {
        this.partsAffected = partsAffected;
    }

    public List<AirframeDto> getAircraftPlatforms() {
        return aircraftPlatforms;
    }

    public void setAircraftPlatforms(List<AirframeDto> aircraftPlatforms) {
        this.aircraftPlatforms = aircraftPlatforms;
    }

    public List<SalesforceCompanyDto> getSpecificCustomers() {
        return specificCustomers;
    }

    public void setSpecificCustomers(List<SalesforceCompanyDto> specificCustomers) {
        this.specificCustomers = specificCustomers;
    }

    public Boolean getEmailNotification() {
        return emailNotification;
    }

    public void setEmailNotification(Boolean emailNotification) {
        this.emailNotification = emailNotification;
    }

    public Boolean getCompanySpecific() {
        return companySpecific;
    }

    public void setCompanySpecific(Boolean companySpecific) {
        this.companySpecific = companySpecific;
    }

    public List<SalesforceCompanyDto> getCompanyPaidSubscription() { return companyPaidSubscription; }

    public void setCompanyPaidSubscription(List<SalesforceCompanyDto> companyPaidSubscription) { this.companyPaidSubscription = companyPaidSubscription; }

    public Date getDocumentRevisionDate() {
        return documentRevisionDate;
    }

    public void setDocumentRevisionDate(Date documentRevisionDate) {
        this.documentRevisionDate = documentRevisionDate;
    }

    public Date getDocumentDistributionDate() {
        return documentDistributionDate;
    }

    public void setDocumentDistributionDate(Date documentDistributionDate) {
        this.documentDistributionDate = documentDistributionDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Boolean getPowerDocument() { return powerDocument; }

    public void setPowerDocument(Boolean powerDocument) { this.powerDocument = powerDocument; }
}
