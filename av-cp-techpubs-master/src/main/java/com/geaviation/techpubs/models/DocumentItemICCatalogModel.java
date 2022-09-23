package com.geaviation.techpubs.models;

import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class DocumentItemICCatalogModel extends DocumentItemICModel {

    private static final long serialVersionUID = 1L;

    private String catalogkey;
    private String revnbr;
    private String atanum;


    public String getCatalogkey() {
        return this.catalogkey;
    }

    public void setCatalogkey(String catalogkey) {
        this.catalogkey = catalogkey;
    }

    @XmlTransient
    @JsonIgnore
    public String getRevnbr() {
        return this.revnbr;
    }

    public void setRevnbr(String revnbr) {
        this.revnbr = revnbr;
    }

    @XmlTransient
    @JsonIgnore
    public String getAtanum() {
        return this.atanum;
    }

    public void setAtanum(String atanum) {
        this.atanum = atanum;
    }
}
