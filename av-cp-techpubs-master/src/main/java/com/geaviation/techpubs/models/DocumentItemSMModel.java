package com.geaviation.techpubs.models;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "sm")
public class DocumentItemSMModel extends DocumentItemModel {

    private static final long serialVersionUID = 1L;
    private String model;

    public DocumentItemSMModel() {
        super.setType("SM");
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
