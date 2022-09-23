package com.geaviation.techpubs.models;

import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class DocumentItemSMMDownloadModel extends DocumentItemTDModel {
  private String downloadtype;
  private String onlineFileName;
  private String sourceFileName;
  private String dvdFileName;
  private boolean isSMMEnabledPageblk;

  public DocumentItemSMMDownloadModel(){}

  public DocumentItemSMMDownloadModel(String filename, String manualNum){
    setFilename(filename);

    ManualItemModel manualItem = new ManualItemModel();
    manualItem.setManualdocnbr(manualNum);
    setManualItem(manualItem);

  }

  public String getDownloadtype() {
    return downloadtype;
  }

  public void setDownloadtype(String downloadtype) {
    this.downloadtype = downloadtype;
  }

  @XmlTransient
  @JsonIgnore
  public String getOnlineFileName() {
    return onlineFileName;
  }

  public void setOnlineFileName(String onlineFileName) {
    this.onlineFileName = onlineFileName;
  }

  @XmlTransient
  @JsonIgnore
  public boolean isSMMEnabledPageblk() {
    return isSMMEnabledPageblk;
  }

  public void setSMMEnabledPageblk(boolean SMMEnabledPageblk) {
    isSMMEnabledPageblk = SMMEnabledPageblk;
  }

  @XmlTransient
  @JsonIgnore
  public String getSourcefilename() {
    return sourceFileName;
  }

  public void setSourceFileName(String sourceFileName) {
    this.sourceFileName = sourceFileName;
  }

  @XmlTransient
  @JsonIgnore
  public String getDvdFileName() {
    return dvdFileName;
  }

  public void setDvdFileName(String dvdFileName) {
    this.dvdFileName = dvdFileName;
  }

  public String getDownloadfilename() {
    return this.getManualDocnbr() + ":" + ("dvd".equalsIgnoreCase(downloadtype)
        ? getDvdFileName()
        : ("source".equalsIgnoreCase(downloadtype) ? getSourcefilename() : null));
  }
}
