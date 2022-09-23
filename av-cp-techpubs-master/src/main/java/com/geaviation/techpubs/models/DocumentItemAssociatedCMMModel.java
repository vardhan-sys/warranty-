package com.geaviation.techpubs.models;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlType(name = "associatedcmm")
public class DocumentItemAssociatedCMMModel extends DocumentItemCMMModel {

    private static final long serialVersionUID = 1L;

    private String documentUri;
    private String fileName;
    private String fileType;

    @XmlTransient
    @JsonIgnore

    public String getDocumentUri() {
        return documentUri;
    }

    public void setDocumentUri(String documentUri) {
        this.documentUri = documentUri;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @XmlTransient
    @JsonIgnore
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

}
