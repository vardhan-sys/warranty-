package com.geaviation.techpubs.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "contentresponse")
public class BookcaseContentModel extends Response {
    private List<BookcaseContentDAO> bookcaseContentDAOS;
    private int iDisplayLength;
    private int iDisplayStart;
    private int iTotalDsiplayRecords;
    private int iTotalRecords;
    private String sEcho;

    public BookcaseContentModel(List<BookcaseContentDAO> bookcaseContentDAOS){
        this.bookcaseContentDAOS = bookcaseContentDAOS;
    }

    public BookcaseContentModel(){}

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

    @XmlElementWrapper(name = "objects")
    @JsonProperty("objects")
    public List<BookcaseContentDAO> getBookcaseContentDAOS() {
        return bookcaseContentDAOS;
    }

    public void setBookcaseITems(List<BookcaseContentDAO> bookcaseContentDAOS){
        this.bookcaseContentDAOS = bookcaseContentDAOS;
    }

}
