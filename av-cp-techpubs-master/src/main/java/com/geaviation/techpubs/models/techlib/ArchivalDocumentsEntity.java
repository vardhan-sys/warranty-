package com.geaviation.techpubs.models.techlib;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "archival_documents", schema = "techlib")
public class ArchivalDocumentsEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "type")
    private String type;

    @Column(name = "title")
    private String title;

    @Column(name = "issue_number")
    private String issueNumber;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "s3_file_path")
    private String filePath;

    @ManyToMany
    @JoinTable(name = "archival_documents_eml",
            joinColumns = {@JoinColumn(name = "archival_documents_id")},
            inverseJoinColumns = {@JoinColumn(name = "archival_engine_model_lookup_id")})
    private List<ArchivalEngineModelEntity> engineModels;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public String getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(String issueNumber) {
        this.issueNumber = issueNumber;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<ArchivalEngineModelEntity> getEngineModels() {
        return engineModels;
    }

    public void setEngineModels(List<ArchivalEngineModelEntity> engineModels) {
        this.engineModels = engineModels;
    }
}
