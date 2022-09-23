package com.geaviation.techpubs.models.reviewer;

import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NotifIc {
    private String ataNumber;
    private String fileName;
    private String bookType;
    private String bookKey;
    private String title;
    private String bookcaseKey;
    private String bookcaseTitle;
    private String revisionDate;

    /**
     *
     * @param metadata
     * @param fileName
     * @param bookType
     * @param bookKey
     * @param title
     * @param bookcaseKey
     * @param bookcaseTitle
     * @param revisionDate
     */
    public NotifIc(Object metadata, String fileName, String bookType, String bookKey, String title, String bookcaseKey, String bookcaseTitle, Date revisionDate) {
        JSONObject jsonMetadata = new JSONObject(String.valueOf(metadata));

        if (jsonMetadata.has("atanbr")) {
            this.ataNumber = jsonMetadata.getString("atanbr");
        } else {
            this.ataNumber = "";
        }
        this.fileName = fileName;
        this.bookType = bookType;
        this.bookKey = bookKey;
        this.title = title;
        this.bookcaseKey = bookcaseKey;
        this.bookcaseTitle = bookcaseTitle;
        if (revisionDate != null) {
            this.revisionDate = new SimpleDateFormat("yyyy-MM-dd").format(revisionDate);
        }
    }

    public String getAtaNumber() {
        return ataNumber;
    }

    public void setAtaNumber(String ataNumber) {
        this.ataNumber = ataNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public String getBookKey() {
        return bookKey;
    }

    public void setBookKey(String bookKey) {
        this.bookKey = bookKey;
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

    public String getRevisionDate() {
        return revisionDate;
    }

    public void setRevisionDate(String revisionDate) {
        this.revisionDate = revisionDate;
    }

    public String getBookcaseTitle() {
        return bookcaseTitle;
    }

    public void setBookcaseTitle(String bookcaseTitle) {
        this.bookcaseTitle = bookcaseTitle;
    }
}
