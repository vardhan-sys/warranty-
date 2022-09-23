package com.geaviation.techpubs.models.techlib.dto;

import com.geaviation.techpubs.services.excel.annotation.Excel;
import com.geaviation.techpubs.services.excel.annotation.ExcelColumn;
import com.geaviation.techpubs.services.util.AppConstants;

@Excel(fileName = "publisher", orderMatters = true)
public class PublisherSumTableDTO {

  @ExcelColumn(name = AppConstants.BOOKCASE, order = 1)
  private String bookcase;

  @ExcelColumn(name = AppConstants.ENGINE_FAMILY, order = 2)
  private String engineFamily;

  @ExcelColumn(name = AppConstants.ENGINE_MODEL, order = 3)
  private String engineModel;

  @ExcelColumn(name= AppConstants.GEK, order = 4)
  private String gek;

  @ExcelColumn(name = AppConstants.CURRENT_VERSION, order = 5)
  private String currentVersion;

  public PublisherSumTableDTO() { }

  public PublisherSumTableDTO(String bookcase, String engineFamily, String engineModel, String gek, String currentVersion) {
    this.bookcase = bookcase;
    this.engineFamily = engineFamily;
    this.engineModel = engineModel;
    this.gek = gek;
    this.currentVersion = currentVersion;
  }

  public String getBookcase() { return bookcase; }

  public void setBookcase(String bookcase) { this.bookcase = bookcase; }

  public String getEngineFamily() { return engineFamily; }

  public void setEngineFamily(String engineFamily) { this.engineFamily = engineFamily; }

  public String getEngineModel() { return engineModel; }

  public void setEngineModel(String engineModel) { this.engineModel = engineModel; }

  public String getGek() { return gek; }

  public void setGek(String gek) { this.gek = gek; }

  public String getCurrentVersion() { return currentVersion; }

  public void setCurrentVersion(String currentVersion) { this.currentVersion = currentVersion; }
}
