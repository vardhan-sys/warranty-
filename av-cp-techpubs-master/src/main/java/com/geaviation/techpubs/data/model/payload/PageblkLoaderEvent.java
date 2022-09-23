package com.geaviation.techpubs.data.model.payload;

public class PageblkLoaderEvent {

    private String bookcase;
    private String book;
    private String bookType;
    private String version;
    private String fileName;

    public String getBookcase() {
        return bookcase;
    }

    public void setBookcase(String bookcase) {
        this.bookcase = bookcase;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "{" +
                "\"bookcase\": \"" + this.bookcase + "\"," +
                "\"book\": \"" + this.book + "\"," +
                "\"bookType\": \"" + this.bookType + "\"," +
                "\"version\": \"" + this.version + "\"," +
                "\"fileName\": \"" + this.fileName + "\"" +
                "}";
    }
}
