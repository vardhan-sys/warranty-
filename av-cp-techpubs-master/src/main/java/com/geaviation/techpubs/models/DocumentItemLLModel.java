package com.geaviation.techpubs.models;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlType(name = "ll")
public class DocumentItemLLModel extends DocumentItemModel {

    private static final long serialVersionUID = 1L;

    public DocumentItemLLModel() {
        super.setType("LL");
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
