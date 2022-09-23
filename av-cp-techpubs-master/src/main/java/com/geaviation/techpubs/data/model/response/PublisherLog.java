package com.geaviation.techpubs.data.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geaviation.techpubs.services.excel.annotation.Excel;
import com.geaviation.techpubs.services.excel.annotation.ExcelColumn;
import java.io.Serializable;

@Excel(fileName = "docadmin_publisher_audit", orderMatters = true)
public class PublisherLog implements AuditLog, Serializable {

    @ExcelColumn(name = "action", order = 1)
    private String action;

    @ExcelColumn(name = "appId", order = 2)
    private String appId;

    @ExcelColumn(name = "bookcaseKey", order = 3)
    private String bookcaseKey;

    @ExcelColumn(name = "bookcaseVersion", order = 4)
    private String bookcaseVersion;

    @ExcelColumn(name = "book", order = 5)
    private String book;

    @ExcelColumn(name = "typeCode", order = 6)
    private String typeCode;

    @ExcelColumn(name = "fileName", order = 7)
    private String fileName;

    @ExcelColumn(name = "key", order = 8)
    private String key;

    @ExcelColumn(name = "category", order = 9)
    private String category;

    @ExcelColumn(name = "modified_by", order = 10)
    private String modifiedBy;

    @ExcelColumn(name = "modified_date", order = 11)
    private String modifiedDate;

    @ExcelColumn(name = "release_date", order = 12)
    private String releaseDate;

    private String UUID;

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    @JsonIgnore
    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }


    public String getBookcaseKey() { return bookcaseKey; }

    public void setBookcaseKey(String bookcaseKey) { this.bookcaseKey = bookcaseKey; }

    public String getModifiedBy() { return modifiedBy; }

    public void setModifiedBy(String modifiedBy) { this.modifiedBy = modifiedBy; }

    public String getModifiedDate() { return modifiedDate; }

    public void setModifiedDate(String modifiedDate) { this.modifiedDate = modifiedDate; }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @JsonIgnore
    public String getUUID() { return UUID; }

    public void setUUID(String UUID) { this.UUID = UUID; }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getBookcaseVersion() {
        return bookcaseVersion;
    }

    public void setBookcaseVersion(String bookcaseVersion) {
        this.bookcaseVersion = bookcaseVersion;
    }
}

