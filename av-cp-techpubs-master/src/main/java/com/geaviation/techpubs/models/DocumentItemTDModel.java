package com.geaviation.techpubs.models;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlRootElement
@XmlSeeAlso({DocumentItemSBModel.class, DocumentItemICModel.class, DocumentItemTRModel.class,
    DocumentItemSourceCatalogModel.class})
public class DocumentItemTDModel extends DocumentItemModel {

    private static final long serialVersionUID = 1L;
    private String toctitle;
    private String revisionDate;
    private String fileType;
    private String filename = null;
    private String mfilename = null;
    private ManualItemModel manualItem;

    public String getToctitle() {
        return (this.toctitle != null ? this.toctitle : "");
    }

    public void setToctitle(String toctitle) {
        this.toctitle = toctitle;
    }

    public String getRevisionDate() {
        return (this.revisionDate != null ? this.revisionDate : "");
    }

    public void setRevisionDate(String revisionDate) {
        this.revisionDate = revisionDate;
    }

    public String getFileType() {
        return this.fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @XmlTransient
    @JsonIgnore
    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @XmlTransient
    @JsonIgnore
    public String getMfilename() {
        return (this.mfilename != null ? this.mfilename : getFilename());
    }

    public void setMfilename(String mfilename) {
        this.mfilename = mfilename;
    }

    @XmlTransient
    @JsonIgnore
    public ManualItemModel getManualItem() {
        return this.manualItem;
    }

    public void setManualItem(ManualItemModel manualItem) {
        this.manualItem = manualItem;
    }

    @XmlTransient
    @JsonIgnore
    public String getManualtitle() {
        return (this.manualItem != null ? this.manualItem.getTitle() : null);
    }

    @XmlTransient
    @JsonIgnore
    public String getManualDocnbr() {
        return (this.manualItem != null ? this.manualItem.getManualDocnbr() : null);
    }

    public boolean isPrintable() {
        return (this.manualItem != null ? this.manualItem.isPrintable() : false);
    }

    @Override
    public String getMultibrowser() {
        return (this.manualItem == null ? "N" : this.manualItem.getMultibrowser());
    }

}
