package com.geaviation.techpubs.models;

import javax.xml.bind.annotation.XmlTransient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SbData implements Comparable<SbData> {

  private static final Logger log = LogManager.getLogger(SbData.class);


  private String onlineFilename;
  private String revisionNumber;

  public SbData() {
  }

  public SbData(String onlineFilename, String revisionNumber) {
    this.onlineFilename = onlineFilename;
    this.revisionNumber = revisionNumber;
  }

  @JsonProperty("onlineFilename")
  @XmlTransient
  public String getOnlineFilename() {
    return onlineFilename;
  }

  public void setOnlineFilename(String onlineFilename) {
    this.onlineFilename = onlineFilename;
  }

  @JsonProperty("revisionNumber")
  @XmlTransient
  public String getRevisionNumber() {
    return revisionNumber;
  }

  public void setRevisionNumber(String revisionNumber) {
    this.revisionNumber = revisionNumber;
  }

  @Override
  public int compareTo(SbData o) {
    try {
      int objectRevisionNumberA = Integer.parseInt(o.revisionNumber);
      int objectRevisionNumberB = Integer.parseInt(this.revisionNumber);
      return (objectRevisionNumberA - objectRevisionNumberB);
    } catch (NumberFormatException e) {
      log.error("Failed to parse revision number in custom compareTo for getSbResource", e);
      return -1;
    }
  }
}
