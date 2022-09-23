package com.geaviation.techpubs.models;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "sb")
public class DocumentItemSBModel extends DocumentItemTDModel {

    private static final long serialVersionUID = 1L;
    private String releaseDate;
    private String version;
    private String category;
    private boolean sbalert = false;

    public DocumentItemSBModel() {
        super.setType("SB");
    }

    public String getReleaseDate() {
        return (this.releaseDate != null ? this.releaseDate : "");
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCategory() {
        return (this.category != null ? this.category : "");
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean getSbalert() {
        return this.sbalert;
    }

    public void setSbalert(boolean sbalert) {
        this.sbalert = sbalert;
    }
}
