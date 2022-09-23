package com.geaviation.techpubs.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement(name = "documentresponse")
public class DocumentDataTableModel extends DocumentModel {

    private static final long serialVersionUID = -674440379486419385L;
    private int iDisplayLength;
    private int iDisplayStart;
    private int iTotalDsiplayRecords;
    private int iTotalRecords;
    private String sEcho;

    @JsonProperty("iDisplayLength")
    @XmlElement(name = "iDisplayLength")
    public int getIDisplayLength() {
        return iDisplayLength;
    }

    public void setIDisplayLength(int iDisplayLength) {
        this.iDisplayLength = iDisplayLength;
    }

    @JsonProperty("iDisplayStart")
    @XmlElement(name = "iDisplayStart")
    public int getIDisplayStart() {
        return iDisplayStart;
    }

    public void setIDisplayStart(int iDisplayStart) {
        this.iDisplayStart = iDisplayStart;
    }

    @JsonProperty("iTotalDisplayRecords")
    @XmlElement(name = "iTotalDisplayRecords")
    public int getITotalDisplayRecords() {
        return iTotalDsiplayRecords;
    }

    public void setITotalDisplayRecords(int iTotalDsiplayRecords) {
        this.iTotalDsiplayRecords = iTotalDsiplayRecords;
    }

    @JsonProperty("iTotalRecords")
    @XmlElement(name = "iTotalRecords")
    public int getITotalRecords() {
        return iTotalRecords;
    }

    public void setITotalRecords(int iTotalRecords) {
        this.iTotalRecords = iTotalRecords;
    }

    @JsonProperty("sEcho")
    @XmlElement(name = "sEcho")
    public String getSEcho() {
        return sEcho;
    }

    public void setSEcho(String sEcho) {
        this.sEcho = sEcho;
    }
}
