package com.geaviation.techpubs.models.techlib.dto;

import com.geaviation.techpubs.services.util.AppConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.geaviation.techpubs.services.util.AppConstants.*;

/**
 * DTO for a status of a version of a bookcase
 */
public class BookcaseVersionDto {
  private String version;
  private String status;
  private String releaseDate;

  private static final Logger log = LogManager.getLogger(BookcaseVersionDto.class);

  public BookcaseVersionDto() { }

  public BookcaseVersionDto(String version, String status) {
    this.version = version;
    this.status = status;
  }

  public BookcaseVersionDto(String version, String status, String releaseDate) {
    this.version = version;
    this.status = status;
    this.releaseDate = releaseDate;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getReleaseDate() { return releaseDate; }

  public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

  public boolean hasValidStatus() {
    boolean validStatus = true;
    if (!OFFLINE.equalsIgnoreCase(this.status)
        && !ONLINE.equalsIgnoreCase(this.status)
        && !ARCHIVED.equalsIgnoreCase(this.status)
        && !SUSPENDED.equalsIgnoreCase(this.status)) {
      log.info("Invalid bookcase status for version " + this.getVersion()
          + ". Found status: " + this.status);
      validStatus = false;
    }
    return validStatus;
  }

  /**
   * Since this class will be created from an API call, then the status will be what to change to.
   * This takes the status and makes the change more readable.
   *
   * @return String corresponding to action taken for the DB change
   */
  public String getAuditAction() {
    return AUDIT_ACTION.valueOf(this.status.toUpperCase()).getAction();
  }

  private enum AUDIT_ACTION {
    OFFLINE(AppConstants.BOOKCASE_SET_OFFLINE),
    ONLINE(AppConstants.BOOKCASE_SET_ONLINE),
    SUSPENDED(AppConstants.BOOKCASE_SET_SUSPENDED),
    ARCHIVED(AppConstants.BOOKCASE_SET_ARCHIVED);

    private String auditAction;

    @SuppressWarnings("squid:UnusedPrivateMethod")
    AUDIT_ACTION(String auditAction) {
      this.auditAction = auditAction;
    }

    public String getAction() {
      return auditAction;
    }
  }
}
