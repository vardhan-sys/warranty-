package com.geaviation.techpubs.models.reviewer;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NotifTr {
    private String bookcaseTitle;
    private String ataNbr;
    private String trNbr;
    private String fileName;
    private String bookType;
    private String revisionDate;
    private String pageblkKey;
    private String title;
    private String bookcaseKey;
    private String bookKey;
    private String type;

    /**
     *
     * @param bookcaseTitle
     * @param metadata
     * @param fileName
     * @param bookType
     * @param revisionDate
     * @param pageblkKey
     * @param title
     * @param bookcaseKey
     * @param bookKey
     * @param type
     */
    public NotifTr(String bookcaseTitle, Object metadata, String fileName, String bookType,
                   Date revisionDate, String pageblkKey, String title, String bookcaseKey, String bookKey, String type) {
        JSONObject jsonMetadata= new JSONObject(String.valueOf(metadata));

        this.bookcaseTitle = bookcaseTitle;
        if (jsonMetadata.has("atanbr")) {
            this.ataNbr = jsonMetadata.getString("atanbr");
        } else {
            this.ataNbr = "";
        }
        if (jsonMetadata.has("trnbr")) {
            this.trNbr = jsonMetadata.getString("trnbr");
        } else {
            this.trNbr = "";
        }
        this.fileName = fileName;
        this.bookType = bookType;
        if (revisionDate != null) {
            this.revisionDate = new SimpleDateFormat("yyyy-MM-dd").format(revisionDate);
        }
        this.pageblkKey = pageblkKey;
        this.title = title;
        this.bookcaseKey = bookcaseKey;
        this.bookKey = bookKey;
        this.type = type;
    }

    public String getBookcaseTitle() {
        return bookcaseTitle;
    }

    public void setBookcaseTitle(String bookcaseTitle) {
        this.bookcaseTitle = bookcaseTitle;
    }

    public String getAtaNbr() {
        return ataNbr;
    }

    public void setAtaNbr(String ataNbr) {
        this.ataNbr = ataNbr;
    }

    public String getTrNbr() {
        return trNbr;
    }

    public void setTrNbr(String trNbr) {
        this.trNbr = trNbr;
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

    public String getRevisionDate() {
        return revisionDate;
    }

    public void setRevisionDate(String revisionDate) {
        this.revisionDate = revisionDate;
    }

    public String getPageblkKey() {
        return pageblkKey;
    }

    public void setPageblkKey(String pageblkKey) {
        this.pageblkKey = pageblkKey;
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
}
