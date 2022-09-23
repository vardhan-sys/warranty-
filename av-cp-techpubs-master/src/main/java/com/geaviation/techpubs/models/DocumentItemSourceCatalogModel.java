package com.geaviation.techpubs.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DocumentItemSourceCatalogModel extends DocumentItemTDModel {

    private static final long serialVersionUID = 1L;
    private String downloadtype;
    private String catalogkey;
    private String sourcefilename;
    private String manualDocnbr;
    private String pubcwcdate;

    public String getReleaseDate() {
        return this.getRevisionDate();
    }

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

    @JsonProperty("downloadfilename")
    @XmlElement(name = "downloadfilename")
    public String getDownloadfilename() {
        return this.manualDocnbr + ":" + this.sourcefilename;
    }

    @XmlTransient
    @JsonIgnore
    @Override
    public String getManualDocnbr() {
        return manualDocnbr;
    }

    public void setManualdocnbr(String manualDocnbr) {
        this.manualDocnbr = manualDocnbr;
    }

    @XmlTransient
    @JsonIgnore
    @Override
    public String getResourceUri() {
        return null;
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
