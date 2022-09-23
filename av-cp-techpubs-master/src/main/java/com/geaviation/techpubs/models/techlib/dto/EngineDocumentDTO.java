package com.geaviation.techpubs.models.techlib.dto;

import java.util.List;

public class EngineDocumentDTO {

    private String id;
    private String documentType;
    private String documentTitle;
    private List<String> engineModels;
    private String lastUpdatedDate;
    private String issueDate;

    public EngineDocumentDTO(String id, String documentType, String documentTitle, List<String> engineModels,
                             String lastUpdatedDate, String issueDate) {
        super();
        this.id = id;
        this.documentType = documentType;
        this.documentTitle = documentTitle;
        this.engineModels = engineModels;
        this.lastUpdatedDate = lastUpdatedDate;
        this.issueDate = issueDate;
    }

    public EngineDocumentDTO() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public List<String> getEngineModels() {
        return engineModels;
    }

    public void setEngineModels(List<String> engineModels) {
        this.engineModels = engineModels;
    }

    public String getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(String lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }
}