package com.geaviation.techpubs.models;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
public class DVDInfoResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;
    private String releaseDate;
    private String geknumber;
    private String filesize;
    private String type;

    public DVDInfoResponse(String title, String releaseDate, String geknumber, String filesize,
        String type) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.geknumber = geknumber;
        this.filesize = filesize;
        this.type = type;
    }


    @XmlElement(name = "title")
    @JsonProperty("title")
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @XmlElement(name = "releaseDate")
    @JsonProperty("releaseDate")
    public String getReleaseDate() {
        return this.releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @XmlElement(name = "geknumber")
    @JsonProperty("geknumber")
    public String getGeknumber() {
        return this.geknumber;
    }

    public void setGeknumber(String geknumber) {
        this.geknumber = geknumber;
    }

    @XmlElement(name = "filesize")
    @JsonProperty("filesize")
    public String getFilesize() {
        return this.filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    @XmlElement(name = "type")
    @JsonProperty("type")
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
