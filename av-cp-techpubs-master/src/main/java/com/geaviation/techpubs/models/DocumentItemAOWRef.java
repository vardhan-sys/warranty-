package com.geaviation.techpubs.models;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class DocumentItemAOWRef implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String documentsUri;
    private String wireNumber;
    private String releaseDate;
    private String model;
    private String title;
    private String resourceUri;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlTransient
    @JsonIgnore
    public String getDocumentsUri() {
        return documentsUri;
    }

    public void setDocumentsUri(String documentsUri) {
        this.documentsUri = documentsUri;
    }

    public String getWireNumber() {
        return this.wireNumber;
    }

    public void setWireNumber(String wireNumber) {
        this.wireNumber = wireNumber;
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

    public String getTitle() {
        return (this.title != null ? this.title : "");
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getResourceUri() {
        return this.resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }
}
