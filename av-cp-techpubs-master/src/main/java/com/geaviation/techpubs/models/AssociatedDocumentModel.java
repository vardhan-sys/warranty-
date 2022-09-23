package com.geaviation.techpubs.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "associateddocuments")
public class AssociatedDocumentModel extends DocumentModel {

    private static final long serialVersionUID = 1218919241981840176L;
    private String title = "";
    private String type = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
