package com.geaviation.techpubs.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RelatedDocumentInfoModel {

    private String program;
    private String manual;
    private String file;
    private String title;
    private String type;
    private String revdate;

    @JsonProperty("pgm")
    public String getProgram() {
        return this.program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    @JsonProperty("man")
    public String getManual() {
        return this.manual;
    }

    public void setManual(String manual) {
        this.manual = manual;
    }

    @JsonProperty("title")
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("file")
    public String getFile() {
        return this.file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @JsonProperty("type")
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonIgnore
    public String getRevdate() {
        return this.revdate;
    }

    public void setRevdate(String revdate) {
        this.revdate = revdate;
    }

}
