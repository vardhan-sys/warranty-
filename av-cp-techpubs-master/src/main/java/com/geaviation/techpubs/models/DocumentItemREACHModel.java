package com.geaviation.techpubs.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlType(name = "reach")
public class DocumentItemREACHModel extends DocumentItemModel {

    private static final long serialVersionUID = 1L;

    private String revisionDate;
    private String model;

    public DocumentItemREACHModel() {
        super.setType("REACH");
    }

    public String getRevisionDate() {
        return revisionDate;
    }

    public void setRevisionDate(String revisionDate) {
        this.revisionDate = revisionDate;
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

}
