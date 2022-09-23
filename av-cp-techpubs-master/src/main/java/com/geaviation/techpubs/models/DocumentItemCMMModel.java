package com.geaviation.techpubs.models;

import java.util.List;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlType(name = "cmm")
public class DocumentItemCMMModel extends DocumentItemModel {

    private static final long serialVersionUID = 1L;

    private String releaseDate;
    private String model;
    private String publication;
    private String parentId;
    private List<String[]> parts;

    public DocumentItemCMMModel() {
        super.setType("CMM");
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPublication() {
        return this.publication;
    }

    public void setPublication(String publication) {
        this.publication = publication;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    // following getters are here for backwards compatibility reasons
    @Override
    public String getProgramtitle() {
        return this.model;
    }

    @Override
    public String getProgram() {
        return this.model;
    }

    @Override
    public String getProgramdocnbr() {
        return this.model;
    }

    @XmlTransient
    @JsonIgnore
    public List<String[]> getParts() {
        return parts;
    }

    public void setParts(List<String[]> parts) {
        this.parts = parts;
    }

    @XmlTransient
    @JsonIgnore
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
