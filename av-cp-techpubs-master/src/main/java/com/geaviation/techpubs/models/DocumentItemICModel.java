package com.geaviation.techpubs.models;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ic")
public class DocumentItemICModel extends DocumentItemSMMDownloadModel {

    private static final long serialVersionUID = 1L;
    private String releaseDate;

    public DocumentItemICModel() {
        super.setType("IC");
    }

    public String getReleaseDate() {
        return (this.releaseDate != null ? this.releaseDate : "");
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
