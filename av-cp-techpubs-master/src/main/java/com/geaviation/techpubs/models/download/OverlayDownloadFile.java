package com.geaviation.techpubs.models.download;

public class OverlayDownloadFile {
    private String manual;
    private String name;

    public OverlayDownloadFile(String manual, String name) {
        this.manual = manual;
        this.name = name;
    }

    public String getManual() {
        return manual;
    }

    public void setManual(String manual) {
        this.manual = manual;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return manual + ":" + name;
    }
}
