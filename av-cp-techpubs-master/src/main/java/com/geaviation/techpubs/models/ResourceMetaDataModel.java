package com.geaviation.techpubs.models;

import static com.geaviation.techpubs.services.util.AppConstants.PROGRAM;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement(name = "documentresponse")
public class ResourceMetaDataModel extends Response {

    private String fileExtension;
    private String fileName;
    private String title;
    private String program;
    private String manual;
    private String contentid;

    @XmlElement(name = "fileName")
    @JsonProperty("fileName")
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @XmlElement(name = "fileExtension")
    @JsonProperty("fileExtension")
    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    @XmlElement(name = "title")
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @XmlElement(name = PROGRAM)
    @JsonProperty(PROGRAM)
    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    @XmlElement(name = "manual")
    @JsonProperty("manual")
    public String getManual() {
        return manual;
    }

    public void setManual(String manual) {
        this.manual = manual;
    }

    @XmlElement(name = "contentid")
    @JsonProperty("contentid")
    public String getContentid() {
        return contentid;
    }

    public void setContentid(String contentid) {
        this.contentid = contentid;
    }
}
