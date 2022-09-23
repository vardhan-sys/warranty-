package com.geaviation.techpubs.services.excel.model;

public class FileWithBytes {

    private byte[] contents;
    private String fileName;

    public FileWithBytes(byte[] contents, String fileName) {
        this.contents = contents;
        this.fileName = fileName;
    }

    public byte[] getContents() {
        return contents;
    }

    public String getFileName() {
        return fileName;
    }
}
