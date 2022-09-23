package com.geaviation.techpubs.models;

import com.geaviation.techpubs.data.util.DataUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

public class PageblkDAO extends BookcaseContentDAO {
    private String key;
    private String fileType = "";
    private String resourceUri;
    private String bookKey;
    private String fileName;
    private String metadata;

    public PageblkDAO() {
    }

    public PageblkDAO(String bookKey, String fileName, String key, String title, Date revisionDate,
                      String bookcaseKey, String bookcaseTitle, String type, Object metadata, String version) {
        super(title, revisionDate, bookcaseKey, bookcaseTitle, type, version);
        this.key = key;
        this.fileType = DataUtil.getFileType(fileName);
        this.resourceUri = DataUtil.createFileResourceUri(bookcaseKey, bookKey, fileName, version);
        this.bookKey = bookKey;

        this.fileName = fileName;
        this.metadata = String.valueOf(metadata);
    }

    @JsonProperty("bookKey")
    @XmlElement(name = "bookKey")
    public String getBookKey() {
        return bookKey;
    }

    public void setBookKey(String bookKey) {
        this.bookKey = bookKey;
    }

    @JsonProperty("fileName")
    @XmlElement(name = "fileName")
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @JsonProperty("fileType")
    @XmlElement(name = "fileType")
    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @JsonProperty("resourceUri")
    @XmlElement(name = "resourceUri")
    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }

    @XmlTransient
    @JsonIgnore
    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    @JsonProperty("id")
    @XmlElement(name = "id")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
