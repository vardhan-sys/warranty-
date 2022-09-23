package com.geaviation.techpubs.models.download;

public class PdfDownloadFuture {

  private String fileName;
  private byte[] pdfByte;

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public byte[] getPdfByte() {
    return pdfByte;
  }

  public void setPdfByte(byte[] pdfByte) {
    this.pdfByte = pdfByte;
  }

}