package com.geaviation.techpubs.models.techlib.dto;

public class PublisherBookcaseVersionStatusDto {

    private String bookcaseVersion;

    private String bookcaseVersionStatus;

    private Object releaseDate;

    public PublisherBookcaseVersionStatusDto() { }

    public PublisherBookcaseVersionStatusDto(String bookcaseVersion, String bookcaseVersionStatus, Object releaseDate) {
        this.bookcaseVersion = bookcaseVersion;
        this.bookcaseVersionStatus = bookcaseVersionStatus;
        this.releaseDate = releaseDate;
    }

    public String getBookcaseVersion() {
        return bookcaseVersion;
    }

    public void setBookcaseVersion(String bookcaseVersion) {
        this.bookcaseVersion = bookcaseVersion;
    }

    public String getBookcaseVersionStatus() {
        return bookcaseVersionStatus;
    }

    public void setBookcaseVersionStatus(String bookcaseVersionStatus) { this.bookcaseVersionStatus = bookcaseVersionStatus; }

    public Object getReleaseDate() { return releaseDate; }

    public void setReleaseDate(Object releaseDate) { this.releaseDate = releaseDate; }
}

