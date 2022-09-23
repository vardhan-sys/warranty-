package com.geaviation.techpubs.models.techlib.dto;

import com.geaviation.techpubs.services.excel.annotation.Excel;
import com.geaviation.techpubs.services.excel.annotation.ExcelColumn;
import com.geaviation.techpubs.services.util.AppConstants;
import java.util.UUID;

@Excel(fileName = "bookcase_version_status", orderMatters = true)
public class BookcaseVersionStatusDTO {

  @ExcelColumn(name = AppConstants.VERSION, order = 1)
  private String version;

  @ExcelColumn(name = AppConstants.VERSION_STATUS, order = 2)
  private String versionStatus;

  @ExcelColumn(name = AppConstants.VERSION_ID, order = 3)
  private UUID versionId;

  public BookcaseVersionStatusDTO() { }

  public BookcaseVersionStatusDTO(String version, String versionStatus, UUID versionId) {
    this.version = version;
    this.versionStatus = versionStatus;
    this.versionId = versionId;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getVersionStatus() {
    return versionStatus;
  }

  public void setVersionStatus(String versionStatus) {
    this.versionStatus = versionStatus;
  }

  public UUID getVersionId() {
    return versionId;
  }

  public void setVersionId(UUID versionId) {
    this.versionId = versionId;
  }
}
