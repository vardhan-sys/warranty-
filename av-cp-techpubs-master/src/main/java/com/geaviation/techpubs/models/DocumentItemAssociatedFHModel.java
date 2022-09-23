package com.geaviation.techpubs.models;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlType(name = "associatedfh")
public class DocumentItemAssociatedFHModel extends DocumentItemModel {

    private static final long serialVersionUID = 1L;
    private int seqId;
    private String model;
    private String section;
    private String monthQuarter;
    private String monthQuarterDisplay;
    private int mnthQtrNumber;
    private int yearNum;
    private String releaseDate;
    private String fileName;
    private String documentsUri;
    private String fileType;
    private String contentType;
    private int displayOrder;
    private String engineFamily;

    @XmlTransient
    @JsonIgnore
    public int getSeqId() {
        return this.seqId;
    }

    public void setSeqId(int seqId) {
        this.seqId = seqId;
    }

    @XmlTransient
    @JsonIgnore
    public String getMonthQuarter() {
        return this.monthQuarter;
    }

    public void setMonthQuarter(String monthQuarter) {
        this.monthQuarter = monthQuarter;
    }

    @XmlTransient
    @JsonIgnore
    public String getMonthQuarterDisplay() {
        return this.monthQuarterDisplay;
    }

    public void setMonthQuarterDisplay(String monthQuarterDisplay) {
        this.monthQuarterDisplay = monthQuarterDisplay;
    }

    @XmlTransient
    @JsonIgnore
    public int getMnthQtrNumber() {
        return this.mnthQtrNumber;
    }

    public void setMnthQtrNumber(int mnthQtrNumber) {
        this.mnthQtrNumber = mnthQtrNumber;
    }

    @XmlTransient
    @JsonIgnore
    public int getYearNum() {
        return this.yearNum;
    }

    public void setYearNum(int yearNum) {
        this.yearNum = yearNum;
    }

    public String getReleaseDate() {
        return this.releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSection() {
        return this.section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    @XmlTransient
    @JsonIgnore
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @XmlTransient
    @JsonIgnore
    public String getDocumentsUri() {
        return documentsUri;
    }

    public void setDocumentsUri(String documentsUri) {
        this.documentsUri = documentsUri;
    }

    public String getFileType() {
        return this.fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @XmlTransient
    @JsonIgnore
    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @XmlTransient
    @JsonIgnore
    public String getEngineFamily() {
        return this.engineFamily;
    }

    public void setEngineFamily(String engineFamily) {
        this.engineFamily = engineFamily;
    }

    public boolean isPrintable() {
        return true;
    }

    @XmlTransient
    @JsonIgnore
    @Override
    public String getProgram() {
        return "";
    }

    @XmlTransient
    @JsonIgnore
    @Override
    public String getProgramdocnbr() {
        return "";
    }

    @XmlTransient
    @JsonIgnore
    @Override
    public String getProgramtitle() {
        return "";
    }

    @XmlTransient
    @JsonIgnore
    public int getDisplayOrder() {
        return this.displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
