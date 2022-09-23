package com.geaviation.techpubs.models.techlib.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

public class ReachNotificationDto {
    private String docType;
    private String title;
    private String engModel;
    private String family;
    private String partName;
    private String casNumber;
    private String revisionNum="";
    private Date revisionDate;
    private Date update=new Date();
    private String fileName;



    public ReachNotificationDto(String docType, String title, String engModel,
                                String family, String partName, String casNumber, Date revisionDate, String fileName){

        this.docType = docType;
        this.title = title;
        this.engModel = engModel;
        this.family = family;
        this.partName = partName;
        this.casNumber = casNumber;
        this.revisionDate = revisionDate;
        this.fileName = fileName;
    }

    @XmlElement(name = "docType")
    @JsonProperty("docType")
    public String getDocType() {
        return docType;
    }
    public void setDocType(String docType) {
        this.docType = docType;
    }

    @XmlElement(name = "title")
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    @XmlElement(name = "engineModel")
    @JsonProperty("engineModel")
    public String getEngModel() {
        return engModel;
    }
    public void setEngModel(String engModel) {
        this.engModel = engModel;
    }

    @XmlElement(name = "family")
    @JsonProperty("family")
    public String getFamily() { return family; }
    public void setFamily(String family) {
        this.family = family;
    }

    @XmlElement(name = "partName")
    @JsonProperty("partName")
    public String getPartName() {
        return partName;
    }
    public void setPartName(String partName) {
        this.partName = partName;
    }

    @XmlElement(name = "revision")
    @JsonProperty("revision")
    public String getRevisionNum() {
        return revisionNum;
    }
    public void setRevision(String revision) {
        this.revisionNum = revisionNum;
    }

    @XmlElement(name = "revisionDate")
    @JsonProperty("revisionDate")
    @JsonFormat(pattern="dd-MMM-yy")
    public Date getRevisionDate() {
        return revisionDate;
    }

    @JsonFormat(pattern="dd-MMM-yy")
    public Date getUpdate() {
        return update;
    }
    public void setUpdate(Date update) {
        this.update = update;
    }

    @XmlElement(name = "casNumber")
    @JsonProperty("casNumber")
    public String getCasNumber() { return casNumber; }
    public void setCasNumber(String casNumber) { this.casNumber = casNumber; }

    @XmlElement(name = "fileName")
    @JsonProperty("fileName")
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
}
