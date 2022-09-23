package com.geaviation.techpubs.models;

import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class DocumentItemSBCatalogModel extends DocumentItemSBModel {

    private static final long serialVersionUID = 1L;
    private String downloadtype;
    private String catalogkey;
    private String sourcefilename;
    private String dvdfilename;

    public String getDownloadtype() {
        return downloadtype;
    }

    public void setDownloadtype(String downloadtype) {
        this.downloadtype = downloadtype;
    }

    public String getCatalogkey() {
        return this.catalogkey;
    }

    public void setCatalogkey(String catalogkey) {
        this.catalogkey = catalogkey;
    }

    @XmlTransient
    @JsonIgnore
    public String getSourcefilename() {
        return this.sourcefilename;
    }

    public void setSourcefilename(String sourcefilename) {
        this.sourcefilename = sourcefilename;
    }

    @XmlTransient
    @JsonIgnore
    public String getDvdfilename() {
        return this.dvdfilename;
    }

    public void setDvdfilename(String dvdfilename) {
        this.dvdfilename = dvdfilename;
    }

    public String getDownloadfilename() {
        return this.getManualDocnbr() + ":" + ("dvd".equalsIgnoreCase(downloadtype)
            ? this.dvdfilename
            : ("source".equalsIgnoreCase(downloadtype) ? this.sourcefilename : null));
    }
}
