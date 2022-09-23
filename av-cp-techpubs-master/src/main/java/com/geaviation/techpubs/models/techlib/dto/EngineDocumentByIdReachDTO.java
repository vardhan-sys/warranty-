package com.geaviation.techpubs.models.techlib.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;


public class EngineDocumentByIdReachDTO {
    private String documentId;
    private String documentTitle;
    private String documentType;
    private String partName;
    private Boolean emailNotification;
    private String fileName;
    private List<String> engineModels;
    private List<String> partNumbers;
    private List<String> casNumbers;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date issueDate;


    public EngineDocumentByIdReachDTO() {
    }

    public EngineDocumentByIdReachDTO(String documentId, String documentType, String documentTitle, String partName,
                                      Boolean emailNotification, String filename,
                                      List<String> engineModels, List<String> partNumbers, List<String> casNumbers,
                                      Date issueDate) {
        this.documentId = documentId;
        this.documentType = documentType;
        this.documentTitle = documentTitle;
        this.partName = partName;
        this.emailNotification = emailNotification;
        this.fileName = filename;
        this.engineModels = engineModels;
        this.partNumbers = partNumbers;
        this.casNumbers = casNumbers;
        this.issueDate = issueDate;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public Boolean getEmailNotification() {
        return emailNotification;
    }

    public void setEmailNotification(Boolean emailNotification) {
        this.emailNotification = emailNotification;
    }

    public String getFileName() { return fileName; }

    public void setFileName(String fileName) { this.fileName = fileName; }

    public List<String> getEngineModels() {
        return engineModels;
    }

    public void setEngineModels(List<String> engineModels) {
        this.engineModels = engineModels;
    }

    public List<String> getPartNumbers() {
        return partNumbers;
    }

    public void setPartNumbers(List<String> partNumbers) {
        this.partNumbers = partNumbers;
    }

    public List<String> getCasNumbers() {
        return casNumbers;
    }

    public void setCasNumbers(List<String> casNumbers) {
        this.casNumbers = casNumbers;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }
}

