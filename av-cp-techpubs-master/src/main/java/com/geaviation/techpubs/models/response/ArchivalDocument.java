package com.geaviation.techpubs.models.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public class ArchivalDocument {
    private String id;
    private String type;
    private String title;

    @JsonFormat(pattern = "yyyy/MM/dd")
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private LocalDate effectiveDate;

    private String issueNumber;
    private String filePath;
    private String fileName;
    private List<EngineModel> engineModels;

    public ArchivalDocument() {
    }

    public ArchivalDocument(String id, String type, String title, LocalDate effectiveDate, String issueNumber, String filePath, String fileName, List<EngineModel> engines) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.effectiveDate = effectiveDate;
        this.issueNumber = issueNumber;
        this.filePath = filePath;
        this.fileName = fileName;
        this.engineModels = engines;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(String issueNumber) {
        this.issueNumber = issueNumber;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<EngineModel> getEngineModels() {
        return engineModels;
    }

    public void setEngineModels(List<EngineModel> engineModels) {
        this.engineModels = engineModels;
    }
}
