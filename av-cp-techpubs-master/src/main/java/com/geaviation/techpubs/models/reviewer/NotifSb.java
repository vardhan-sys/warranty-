package com.geaviation.techpubs.models.reviewer;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NotifSb {
    private String bookcaseTitle;
    private String sbNbr;
    private String sbType;
    private String fileName;
    private String revisionDate;
    private String revision;
    private String title;
    private String bookcaseKey;
    private String bookKey;
    private String type;
    private String version;
    private String category;


    /**
     *
     * @param bookcaseTitle
     * @param metadata
     * @param fileName
     * @param revisionDate
     * @param revision
     * @param title
     * @param bookcaseKey
     * @param bookKey
     * @param type
     */
    public NotifSb(String bookcaseTitle, Object metadata, String fileName, Date revisionDate, String revision,
                   String title, String bookcaseKey, String bookKey, String type, String version) {
        JSONObject jsonMetadata= new JSONObject(String.valueOf(metadata));

        this.bookcaseTitle = bookcaseTitle;
        this.sbNbr = jsonMetadata.getString("sbnbr");
        this.sbType = jsonMetadata.getString("type");
        this.fileName = fileName;
        if (revisionDate != null) {
            this.revisionDate = new SimpleDateFormat("yyyy-MM-dd").format(revisionDate);
        }
        this.revision = revision;
        this.title = title;
        this.bookcaseKey = bookcaseKey;
        this.bookKey = bookKey;
        this.type = type;
        this.version = version;
    }

    public String getBookcaseTitle() {
        return bookcaseTitle;
    }

    public void setBookcaseTitle(String bookcaseTitle) {
        this.bookcaseTitle = bookcaseTitle;
    }

    public String getSbNbr() {
        return sbNbr;
    }

    public void setSbNbr(String sbNbr) {
        this.sbNbr = sbNbr;
    }

    public String getSbType() {
        return sbType;
    }

    public void setSbType(String sbType) {
        this.sbType = sbType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getRevisionDate() {
        return revisionDate;
    }

    public void setRevisionDate(String revisionDate) {
        this.revisionDate = revisionDate;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBookcaseKey() {
        return bookcaseKey;
    }

    public void setBookcaseKey(String bookcaseKey) {
        this.bookcaseKey = bookcaseKey;
    }

    public String getBookKey() {
        return bookKey;
    }

    public void setBookKey(String bookKey) {
        this.bookKey = bookKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
