package com.geaviation.techpubs.models;

public class ArchivalPdfS3DataDAO {
    private String fileName;
    private String s3Path;

    public ArchivalPdfS3DataDAO(String fileName, String s3Path) {
        this.fileName = fileName;
        this.s3Path = s3Path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getS3Path() {
        return s3Path;
    }

    public void setS3Path(String s3Path) {
        this.s3Path = s3Path;
    }
}
