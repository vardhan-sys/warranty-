package com.geaviation.techpubs.data.model.response;

import com.geaviation.techpubs.services.excel.annotation.Excel;
import com.geaviation.techpubs.services.excel.annotation.ExcelColumn;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

@Excel(fileName = "docadmin_company_audit", orderMatters = true)
public class CompanyLog implements AuditLog, Serializable {

    @ExcelColumn(name = "company", order = 1)
    private String company;

    @ExcelColumn(name = "action", order = 2)
    private String action;

    @ExcelColumn(name = "engine_model", order = 3)
    private String engineModel;

    @ExcelColumn(name = "document", order = 4)
    private String document;

    @ExcelColumn(name = "document_type", order = 5)
    private String documentType;

    @ExcelColumn(name = "bookcase_key", order = 6)
    private String bookcaseKey;

    @ExcelColumn(name = "book_key", order = 7)
    private String bookKey;

    @ExcelColumn(name = "pageblk_key", order = 8)
    private String pageblkKey;

    @ExcelColumn(name = "modified_by", order = 9)
    private String modifiedBy;

    @ExcelColumn(name = "modified_date", order = 10)
    private String modifiedDate;

    private String category;

    private String appId;

    private String UUID;

    public String getCompany() { return company; }

    public void setCompany(String company) { this.company = company; }

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public String getEngineModel() { return engineModel; }

    public void setEngineModel(String engineModel) { this.engineModel = engineModel; }

    public String getDocument() { return document; }

    public void setDocument(String document) { this.document = document; }

    public String getDocumentType() { return documentType; }

    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getBookcaseKey() { return bookcaseKey; }

    public void setBookcaseKey(String bookcaseKey) { this.bookcaseKey = bookcaseKey; }

    public String getBookKey() { return bookKey; }

    public void setBookKey(String bookKey) { this.bookKey = bookKey; }

    public String getPageblkKey() { return pageblkKey; }

    public void setPageblkKey(String pageblkKey) { this.pageblkKey = pageblkKey; }

    public String getModifiedBy() { return modifiedBy; }

    public void setModifiedBy(String modifiedBy) { this.modifiedBy = modifiedBy; }

    public String getModifiedDate() { return modifiedDate; }

    public void setModifiedDate(String modifiedDate) { this.modifiedDate = modifiedDate; }

    @JsonIgnore
    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    @JsonIgnore
    public String getAppId() { return appId; }

    public void setAppId(String appId) { this.appId = appId; }

    @JsonIgnore
    public String getUUID() { return UUID; }

    public void setUUID(String UUID) { this.UUID = UUID; }
}
