package com.geaviation.techpubs.models.techlib.dto;

import com.geaviation.techpubs.services.excel.annotation.Excel;
import com.geaviation.techpubs.services.excel.annotation.ExcelColumn;
import com.geaviation.techpubs.services.util.AppConstants;

import java.util.Date;

@Excel(fileName = "system_documents", orderMatters = true)
public class SystemDocumentExcelDownloadDTO {

    @ExcelColumn(name = AppConstants.DOCUMENT_TITLE, order = 4)
    private String documentTitle;

    @ExcelColumn(name = AppConstants.DOCUMENT_SITE, order = 3)
    private String documentSite;

    @ExcelColumn(name = AppConstants.DOCUMENT_TYPE, order = 1)
    private String documentType;

    @ExcelColumn(name = AppConstants.DOCUMENT_NUMBER, order = 2)
    private String documentNumber;

    @ExcelColumn(name = AppConstants.REVISION, order = 6)
    private String documentRevision;

    @ExcelColumn(name = AppConstants.PART_NUMBERS, order = 8)
    private String partsAffected;

    @ExcelColumn(name = AppConstants.AIRCRAFT_PLATFORM, order = 10)
    private String aircraftPlatforms;

    @ExcelColumn(name = AppConstants.SPECIFIC_CUSTOMERS, order = 7)
    private String specificCustomers;

    @ExcelColumn(name = AppConstants.CUSTOMER_SPECIFIC, order = 11)
    private Boolean companySpecific;

    @ExcelColumn(name = AppConstants.REVISION_DATE, order = 5)
    private Date documentRevisionDate;

    @ExcelColumn(name = AppConstants.DISTRIBUTION_DATE, order = 9)
    private Date documentDistributionDate;

    @ExcelColumn(name = AppConstants.POWER_DOCUMENT, order = 12)
    private Boolean powerDocument;

    public SystemDocumentExcelDownloadDTO() {
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

    public String getDocumentRevision() {
        return documentRevision;
    }

    public void setDocumentRevision(String documentRevision) {
        this.documentRevision = documentRevision;
    }

    public String getPartsAffected() {
        return partsAffected;
    }

    public void setPartsAffected(String partsAffected) {
        this.partsAffected = partsAffected;
    }

    public String getAircraftPlatforms() {
        return aircraftPlatforms;
    }

    public void setAircraftPlatforms(String aircraftPlatforms) {
        this.aircraftPlatforms = aircraftPlatforms;
    }

    public String getSpecificCustomers() {
        return specificCustomers;
    }

    public void setSpecificCustomers(String specificCustomers) {
        this.specificCustomers = specificCustomers;
    }

    public Boolean getCompanySpecific() {
        return companySpecific;
    }

    public void setCompanySpecific(Boolean companySpecific) {
        this.companySpecific = companySpecific;
    }

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

    public Boolean getPowerDocument() { return powerDocument; }

    public void setPowerDocument(Boolean powerDocument) { this.powerDocument = powerDocument; }
}
