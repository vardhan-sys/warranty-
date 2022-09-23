package com.geaviation.techpubs.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class DVDInfoModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;
    private String releaseDate;

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

}
