package com.geaviation.techpubs.models.techlib.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.UUID;

public class SystemDocumentDTO {
    private String systemDocumentId;
    private String documentType;
    private String documentTypeId;
    private String documentNumber;
    private String documentSite;
    private String documentSiteId;
    private String documentTitle;
    private String documentRevision;
    private Boolean powerDocument;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date documentDistributionDate;

    public SystemDocumentDTO(UUID systemDocumentId, String documentType, UUID documentTypeId,String documentNumber,
                             String documentSite, UUID documentSiteId, String documentTitle, String documentRevision,
                             Date documentDistributionDate, Boolean powerDocument) {
        this.systemDocumentId = systemDocumentId.toString();
        this.documentType = documentType;
        this.documentTypeId = String.valueOf(documentTypeId);
        this.documentNumber = documentNumber;
        this.documentSite = documentSite;
        this.documentSiteId = String.valueOf(documentSiteId);
        this.documentTitle = documentTitle;
        this.documentRevision = documentRevision;
        this.documentDistributionDate = documentDistributionDate;
        this.powerDocument = powerDocument;
    }

    public String getSystemDocumentId() {
        return systemDocumentId;
    }

    public void setSystemDocumentId(String systemDocumentId) {
        this.systemDocumentId = systemDocumentId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentSite() {
        return documentSite;
    }

    public void setDocumentSite(String documentSite) {
        this.documentSite = documentSite;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getDocumentTypeId() {return documentTypeId;}

    public void setDocumentTypeId(String documentTypeId) {this.documentTypeId = documentTypeId;}

    public String getDocumentSiteId() {return documentSiteId;}

    public void setDocumentSiteId(String documentSiteId) {this.documentSiteId = documentSiteId;}

    public String getDocumentRevision() {
        return documentRevision;
    }

    public void setDocumentRevision(String documentRevision) {
        this.documentRevision = documentRevision;
    }

    public Date getDocumentDistributionDate() {
        return documentDistributionDate;
    }

    public void setDocumentDistributionDate(Date documentDistributionDate) {
        this.documentDistributionDate = documentDistributionDate;
    }

    public Boolean getPowerDocument() { return powerDocument; }

    public void setPowerDocument(Boolean powerDocument) { this.powerDocument = powerDocument; }
}