package com.geaviation.techpubs.models.techlib.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;


public class EngineDocumentAddReachDTO {
    private String documentTitle;
    private String partName;
    private Boolean emailNotification;
    private MultipartFile documentUploadFile;
    private List<String> engineModels;
    private List<String> partNumbers;
    private List<String> casNumbers;
    private String issueDate;


    public EngineDocumentAddReachDTO() {
    }

    public EngineDocumentAddReachDTO(String documentTitle, String partName, Boolean emailNotification,
                                     MultipartFile documentUploadFile, List<String> engineModels,
                                     List<String> partNumbers, List<String> casNumbers, String issueDate) {
        this.documentTitle = documentTitle;
        this.partName = partName;
        this.emailNotification = emailNotification;
        this.documentUploadFile = documentUploadFile;
        this.engineModels = engineModels;
        this.partNumbers = partNumbers;
        this.casNumbers = casNumbers;
        this.issueDate = issueDate;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }


    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
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

    public MultipartFile getDocumentUploadFile() {
        return documentUploadFile;
    }

    public void setDocumentUploadFile(MultipartFile documentUploadFile) {
        this.documentUploadFile = documentUploadFile;
    }


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

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }
}
