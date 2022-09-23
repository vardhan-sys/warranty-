package com.geaviation.techpubs.models.download;

public class PdfDocumentDownloadRequest {

  private String bookcase;
  private String version;
  private String book;
  private String filename;


  public String getBookcase() {
    return bookcase;
  }

  public void setBookcase(String bookcase) {
    this.bookcase = bookcase;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getBook() {
    return book;
  }

  public void setBook(String book) {
    this.book = book;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

}