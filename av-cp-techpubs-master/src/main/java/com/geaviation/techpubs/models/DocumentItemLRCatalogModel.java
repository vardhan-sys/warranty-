package com.geaviation.techpubs.models;

import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class DocumentItemLRCatalogModel extends DocumentItemSMMDownloadModel {

    private static final long serialVersionUID = 1L;
    private String catalogkey;
    private String pubcwcdate;


    public String getCatalogkey() {
        return this.catalogkey;
    }

    public void setCatalogkey(String catalogkey) {
        this.catalogkey = catalogkey;
    }

    @XmlTransient
    @JsonIgnore
    public String getPubcwcdate() {
        return (this.pubcwcdate != null ? this.pubcwcdate : "");
    }

    public void setPubcwcdate(String pubcwcdate) {
        this.pubcwcdate = pubcwcdate;
    }
}
