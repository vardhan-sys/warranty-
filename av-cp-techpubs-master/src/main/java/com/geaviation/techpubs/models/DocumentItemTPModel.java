package com.geaviation.techpubs.models;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlType(name = "tp")
public class DocumentItemTPModel extends DocumentItemModel {

    private static final long serialVersionUID = 1L;

    public DocumentItemTPModel() {
        super.setType("TP");
    }

    @XmlTransient
    @JsonIgnore
    public String getFileType() {
        return "";
    }

    @XmlTransient
    @JsonIgnore
    public boolean getPrintable() {
        return false;
    }
}
