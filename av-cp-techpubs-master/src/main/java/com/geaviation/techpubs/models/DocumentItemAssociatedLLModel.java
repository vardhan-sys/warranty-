package com.geaviation.techpubs.models;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlType(name = "associatedll")
public class DocumentItemAssociatedLLModel extends DocumentItemModel {

    private static final long serialVersionUID = 1L;

    private String documentsUri;
    private String fileType;
    private String fileTitle;
    private String contentType;
    private String pdfFileName;
    private String fileCategoryName;
    private int fileCategoryTypeId;
    private String uploadMonthName;
    private int uploadMonthNumber;
    private int uploadYearNumber;
    private String releaseDate;
    private String conferenceLocation;
    private String archiveInd;

    @XmlTransient
    @JsonIgnore

    public String getDocumentsUri() {
        return documentsUri;
    }

    public void setDocumentsUri(String documentsUri) {
        this.documentsUri = documentsUri;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
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
    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @XmlTransient
    @JsonIgnore
    public String getPdfFileName() {
        return this.pdfFileName;
    }

    public void setPdfFileName(String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }

    @XmlTransient
    @JsonIgnore
    public String getFileTitle() {
        return this.fileTitle;
    }

    public void setFileTitle(String fileTitle) {
        this.fileTitle = fileTitle;
    }

    public boolean isPrintable() {
        return true;
    }

    @XmlTransient
    @JsonIgnore
    public int getFileCategoryTypeId() {
        return this.fileCategoryTypeId;
    }

    public void setFileCategoryTypeId(int fileCategoryTypeId) {
        this.fileCategoryTypeId = fileCategoryTypeId;
    }

    @XmlTransient
    @JsonIgnore
    public String getFileCategoryName() {
        return this.fileCategoryName;
    }

    public void setFileCategoryName(String fileCategoryName) {
        this.fileCategoryName = fileCategoryName;
    }

    public String getUploadMonthName() {
        return this.uploadMonthName;
    }

    public void setUploadMonthName(String uploadMonthName) {
        this.uploadMonthName = uploadMonthName;
    }

    @XmlTransient
    @JsonIgnore
    public int getUploadMonthNumber() {
        return this.uploadMonthNumber;
    }

    public void setUploadMonthNumber(int uploadMonthNumber) {
        this.uploadMonthNumber = uploadMonthNumber;
    }

    public int getUploadYearNumber() {
        return this.uploadYearNumber;
    }

    public void setUploadYearNumber(int uploadYearNumber) {
        this.uploadYearNumber = uploadYearNumber;
    }

    public String getReleaseDate() {
        return this.releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getConferenceLocation() {
        return this.conferenceLocation;
    }

    public void setConferenceLocation(String conferenceLocation) {
        this.conferenceLocation = conferenceLocation;
    }

    @XmlTransient
    @JsonIgnore
    public String getArchiveInd() {
        return this.archiveInd;
    }

    public void setArchiveInd(String archiveInd) {
        this.archiveInd = archiveInd;
    }

}
