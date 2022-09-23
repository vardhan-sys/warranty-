package com.geaviation.techpubs.models;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlType(name = "wspg")
public class DocumentItemWSPGModel extends DocumentItemModel {

    private static final long serialVersionUID = 1L;
    private String contentType;
    private String releaseDate;
    private String model;

    public DocumentItemWSPGModel() {
        super.setType("WSPG");
    }

    @XmlTransient
    @JsonIgnore
    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
