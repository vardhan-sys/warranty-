package com.geaviation.techpubs.models.techlib.dto;

public class PageblkLookupDto {
    private String bookcaseKey;
    private String bookcaseVersion;
    private String bookKey;
    private String onlineFilename;
    private String target;

    public PageblkLookupDto() {
    }

    public PageblkLookupDto(String bookcaseKey, String bookcaseVersion, String bookKey, String onlineFilename, String target) {
        this.bookcaseKey = bookcaseKey;
        this.bookcaseVersion = bookcaseVersion;
        this.bookKey = bookKey;
        this.onlineFilename = onlineFilename;
        this.target = target;
    }

    public String getBookcaseKey() {
        return bookcaseKey;
    }

    public void setBookcaseKey(String bookcaseKey) {
        this.bookcaseKey = bookcaseKey;
    }

    public String getBookcaseVersion() {
        return bookcaseVersion;
    }

    public void setBookcaseVersion(String bookcaseVersion) {
        this.bookcaseVersion = bookcaseVersion;
    }

    public String getBookKey() {
        return bookKey;
    }

    public void setBookKey(String bookKey) {
        this.bookKey = bookKey;
    }

    public String getOnlineFilename() {
        return onlineFilename;
    }

    public void setOnlineFilename(String onlineFilename) {
        this.onlineFilename = onlineFilename;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
