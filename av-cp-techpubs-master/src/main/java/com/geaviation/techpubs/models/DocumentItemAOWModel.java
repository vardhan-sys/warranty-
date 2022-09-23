package com.geaviation.techpubs.models;

import java.util.List;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlType(name = "aow")
public class DocumentItemAOWModel extends DocumentItemModel {

    private static final long serialVersionUID = 1L;
    private String contentType;
    private String documentsUri;
    private String wireNumber;
    private String releaseDate;
    private String model;
    private List<DocumentItemAOWRef> refWireList;

    public DocumentItemAOWModel() {
        super.setType("AOW");
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

    @JsonProperty("refdocs")
    public List<DocumentItemAOWRef> getRefWireList() {
        return refWireList;
    }

    public void setRefWireList(List<DocumentItemAOWRef> refWireList) {
        this.refWireList = refWireList;
    }
}