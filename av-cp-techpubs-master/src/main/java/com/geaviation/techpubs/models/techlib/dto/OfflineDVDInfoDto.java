package com.geaviation.techpubs.models.techlib.dto;

import java.util.Date;

public class OfflineDVDInfoDto {

    private String offlineFilename;
    private String version;
    private Date releaseDate;

    public OfflineDVDInfoDto() { }

    public OfflineDVDInfoDto(String offlineFilename, String version, Date releaseDate) {
        this.offlineFilename = offlineFilename;
        this.version = version;
        this.releaseDate = releaseDate;
    }

    public String getOfflineFilename() { return offlineFilename; }

    public void setOfflineFilename(String offlineFilename) {
        this.offlineFilename = offlineFilename; }

    public String getVersion() { return version; }

    public void setVersion(String version) { this.version = version; }

    public Date getReleaseDate() { return releaseDate; }

    public void setReleaseDate(Date releaseDate) { this.releaseDate = releaseDate; }
}
