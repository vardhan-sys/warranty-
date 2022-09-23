package com.geaviation.techpubs.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@XmlRootElement
public class BookcaseContentDAO extends Response{
    private String title;
    private String revisionDate = "";
    private String bookcaseKey;
    private String bookcaseTitle;
    private String type;
    private String version;

    private DateFormat inputDateFormat;
    private DateFormat outputDateFormat;

    private static final Logger log = LogManager.getLogger(BookcaseContentDAO.class);

    public BookcaseContentDAO() {
        outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        inputDateFormat = new SimpleDateFormat("yyyyMMdd");
    }

    public BookcaseContentDAO(String title, Date revisionDate, String bookcaseKey, String bookcaseTitle, String type, String version) {
        this();
        this.title = title;
        if (revisionDate != null) {
            this.revisionDate = outputDateFormat.format(revisionDate);
        }
        this.bookcaseKey = bookcaseKey;
        this.bookcaseTitle = bookcaseTitle;
        this.type = type;
        this.version = version;
    }

    @JsonProperty("type")
    @XmlElement(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("title")
    @XmlElement(name = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("revisionDate")
    @XmlElement(name = "revisionDate")
    public String getRevisiondate() {
        return revisionDate;
    }

    public void setRevisiondate(String revisionDate) {
        try {
            if (revisionDate != null) {
                Date date = inputDateFormat.parse(revisionDate);
                this.revisionDate = outputDateFormat.format(date);
            }
        } catch (ParseException e) {
            log.error(e.getClass() + " thrown while attempting to parse date: " + revisionDate);
        }
    }

    @JsonProperty("bookcaseKey")
    @XmlElement(name = "bookcaseKey")
    public String getBookcaseKey() {
        return bookcaseKey;
    }

    public void setBookcaseKey(String bookcaseKey) {
        this.bookcaseKey = bookcaseKey;
    }

    @JsonProperty("bookcaseTitle")
    @XmlElement(name = "bookcaseTitle")
    public String getBookcasetitle() {
        return bookcaseTitle;
    }

    public void setBookcasetitle(String bookcaseTitle) {
        this.bookcaseTitle = bookcaseTitle;
    }

    @JsonProperty("version")
    @XmlElement(name = "version")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
