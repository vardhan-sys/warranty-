package com.geaviation.techpubs.models.techlib.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public class AddSystemDocumentsDto {

    private String documentTitle;
    private String documentSite;
    private String documentType;
    private String documentNumber;
    private String documentRevision;
    private MultipartFile documentUploadFile;
    private List<String> partsAffected;
    private List<String> aircraftPlatforms;
    private List<String> specificCustomers;
    private Boolean emailNotification;
    private Boolean companySpecific;
    private List<String> paidSubscriptions;
    private Boolean powerDocument;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String documentRevisionDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String documentDistributionDate;


    public AddSystemDocumentsDto() {
    }


	public AddSystemDocumentsDto(String documentTitle, String documentSite, String documentType, String documentNumber,
			String documentRevision, MultipartFile documentUploadFile, List<String> partsAffected,
			List<String> aircraftPlatforms, List<String> specificCustomers, Boolean emailNotification,
			Boolean companySpecific, String documentRevisionDate, String documentDistributionDate,
			List<String> paidSubscriptions, Boolean powerDocument) {
		this.documentTitle = documentTitle;
		this.documentSite = documentSite;
		this.documentType = documentType;
		this.documentNumber = documentNumber;
		this.documentRevision = documentRevision;
		this.documentUploadFile = documentUploadFile;
		this.partsAffected = partsAffected;
		this.aircraftPlatforms = aircraftPlatforms;
		this.specificCustomers = specificCustomers;
		this.emailNotification = emailNotification;
		this.companySpecific = companySpecific;
		this.documentRevisionDate = documentRevisionDate;
		this.documentDistributionDate = documentDistributionDate;
		this.paidSubscriptions = paidSubscriptions;
		this.powerDocument = powerDocument;
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

    public MultipartFile getDocumentUploadFile() {
        return documentUploadFile;
    }

    public void setDocumentUploadFile(MultipartFile documentUploadFile) {
        this.documentUploadFile = documentUploadFile;
    }

    public List<String> getPartsAffected() {
        return partsAffected;
    }

    public void setPartsAffected(List<String> partsAffected) {
        this.partsAffected = partsAffected;
    }

    public List<String> getAircraftPlatforms() {
        return aircraftPlatforms;
    }

    public void setAircraftPlatforms(List<String> aircraftPlatforms) {
        this.aircraftPlatforms = aircraftPlatforms;
    }

    public List<String> getSpecificCustomers() {
        return specificCustomers;
    }

    public void setSpecificCustomers(List<String> specificCustomers) {
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

    public String getDocumentRevisionDate() {
        return documentRevisionDate;
    }

    public void setDocumentRevisionDate(String documentRevisionDate) {
        this.documentRevisionDate = documentRevisionDate;
    }

    public String getDocumentDistributionDate() {
        return documentDistributionDate;
    }

    public void setDocumentDistributionDate(String documentDistributionDate) {
        this.documentDistributionDate = documentDistributionDate;
    }

    public List<String> getPaidSubscriptions() {
	return paidSubscriptions;
    }

    public void setPaidSubscriptions(List<String> paidSubscriptions) {
	this.paidSubscriptions = paidSubscriptions;
    }
	
    public Boolean getPowerDocument() {
	return powerDocument;
    }

    public void setPowerDocument(Boolean powerDocument) {
	this.powerDocument = powerDocument;
    }
    
}
