package com.geaviation.techpubs.models;

import com.geaviation.techpubs.data.util.DataConstants;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

public class BookDAO extends BookcaseContentDAO {

    private String revisionNumber;
    private String key;

    public BookDAO() {}

    public BookDAO(String key, String title, Date revisionDate, String revisionNum, String bookcaseKey, String bookcaseTitle, String version) {
        super(title, revisionDate, bookcaseKey, bookcaseTitle, DataConstants.BOOK_TYPE, version);
        this.revisionNumber = revisionNum;
        this.key = key;
    }

    @JsonProperty("bookKey")
    @XmlElement(name = "bookKey")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty("revisionNumber")
    @XmlElement(name = "revisionNumber")
    public String getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(String revisionNumber) {
        this.revisionNumber = revisionNumber;
    }
}
