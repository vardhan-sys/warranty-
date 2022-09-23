package com.geaviation.techpubs.models;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "tr")
public class DocumentItemTRModel extends DocumentItemTDModel {

    private static final long serialVersionUID = 1L;
    private String revisionRate;

    public DocumentItemTRModel() {
        super.setType("TR");
    }

    public String getRevisionRate() {
        return (this.revisionRate != null ? this.revisionRate : "");
    }

    public void setRevisionRate(String revisionRate) {
        this.revisionRate = revisionRate;
    }
}
