package com.geaviation.techpubs.models.techlib.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

public class NotificationDto {
    private String engineName;
    private String engModel;
    private String manualType;
    private String docNo;
    private Date issueDate;
    private String title;
    private String bookcaseKey;
    private Date update1;
    private Date update;
    private String docNum;

    public NotificationDto(String engineName, String engModel,
                           String manualType, String docNo, Date issueDate,
                           String title, String bookcaseKey, Date update1, Date update, String docNum){

        this.docNum = docNum;
        this.engineName = engineName;
        this.engModel = engModel;
        this.manualType = manualType;
        this.docNo = docNo;
        this.issueDate =issueDate;
        this.title = title;
        this.bookcaseKey = bookcaseKey;
        this.update1 = update1;
        this.update =update;
    }

    @XmlElement(name = "objectId")
    @JsonProperty("objectId")
    public String getBookcaseKey() {
        return bookcaseKey;
    }

    public void setBookcaseKey(String bookcaseKey) {
        this.bookcaseKey = bookcaseKey;
    }

    @XmlElement(name = "engineName")
    @JsonProperty("engineName")
    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    @XmlElement(name = "engineModel")
    @JsonProperty("engineModel")
    public String getEngModel() {
        return engModel;
    }

    public void setEngModel(String engModel) {
        this.engModel = engModel;
    }

    @XmlElement(name = "manualType")
    @JsonProperty("manualType")
    public String getManualType() {
        return manualType;
    }

    public void setManualType(String manualType) {
        this.manualType = manualType;
    }

    @XmlElement(name = "revision")
    @JsonProperty("revision")
    public String getDocNo() {
        return docNo;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    @XmlElement(name = "revisionDate")
    @JsonProperty("revisionDate")
    @JsonFormat(pattern="dd-MMM-yy")
    public Date getIssueDate() {
        return issueDate;
    }

    public Date getUpdate1() {
        return update1;
    }

    public void setUpdate1(Date update1) {
        this.update1 = update1;
    }

    public Date getUpdate() {
        return update;
    }

    @XmlElement(name = "title")
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public void setUpdate(Date update) {
        this.update = update;
    }

    @XmlElement(name = "bcDocNum")
    @JsonProperty("bcDocNum")
    public String getDocNum() {
        return docNum;
    }

    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }
}
