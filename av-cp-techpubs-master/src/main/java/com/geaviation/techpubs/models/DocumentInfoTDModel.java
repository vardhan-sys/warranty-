package com.geaviation.techpubs.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DocumentInfoTDModel {

    private String program;
    private String programTitle;
    private String manual;
    private String file;
    private String title;
    private String revdate;
    private String key;
    private String parentid;
    private String type;
    private String category;
    private boolean sbalert = false;
    private String summaryUri = "";

    private List<RelatedDocumentInfoModel> relatedDocumentInfoList;

    @JsonProperty("pgm")
    public String getProgram() {
        return this.program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    @JsonProperty("programtitle")
    public String getProgramTitle() {
        return this.programTitle;
    }

    public void setProgramTitle(String programTitle) {
        this.programTitle = programTitle;
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

    @JsonIgnore
    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @JsonIgnore
    public String getRevdate() {
        return this.revdate;
    }

    public void setRevdate(String revdate) {
        this.revdate = revdate;
    }

    @JsonProperty("contentid")
    public String getParentid() {
        return this.parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @JsonProperty("relateddocs")
    public List<RelatedDocumentInfoModel> getDocumentItemList() {
        return relatedDocumentInfoList;
    }

    public void setDocumentItemList(List<RelatedDocumentInfoModel> relatedDocumentInfoList) {
        this.relatedDocumentInfoList = relatedDocumentInfoList;
    }

    public boolean getSbalert() {
        return this.sbalert;
    }

    public void setSbalert(boolean sbalert) {
        this.sbalert = sbalert;
    }

    @JsonProperty("summary_uri")
    public String getSummaryUri() {
        return this.summaryUri;
    }

    public void setSummaryUri(String summaryUri) {
        this.summaryUri = summaryUri;
    }

    // Compare Revision dates
    // revdate attribute values are expected to be in YYYYMMDD format.
    public int compareRevDate(String revdate) {
        if (this.getRevdate() == null || this.getRevdate().equals("")) {
            return 0;
        } else {
            return Integer.valueOf(this.getRevdate()).compareTo(Integer.valueOf(revdate));
        }
    }
}
