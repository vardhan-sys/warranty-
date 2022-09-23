package com.geaviation.techpubs.models;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlType(name = "associatedsm")
public class DocumentItemAssociatedSMModel extends DocumentItemModel {

    private static final long serialVersionUID = 1L;
    private String documentsUri;
    private String contentType;
    private String releaseDate;
    private String model;

    @XmlTransient
    @JsonIgnore
    public String getDocumentsUri() {
        return documentsUri;
    }

    public void setDocumentsUri(String documentsUri) {
        this.documentsUri = documentsUri;
    }

    @XmlTransient
    @JsonIgnore
    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
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
}
