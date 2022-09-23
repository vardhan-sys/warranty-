package com.geaviation.techpubs.models.techlib.dto;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "bookcase")
@SecondaryTable(name = "bookcase_version", pkJoinColumns = @PrimaryKeyJoinColumn(name = "bookcase_id"))
@NamedQuery(name = "BookcaseBookcaseVersionDto.findAllWithStatus", query = "SELECT b FROM BookcaseBookcaseVersionDto b WHERE b.bookcaseVersionStatusCode = :status")
public class BookcaseBookcaseVersionDto {

  @Id
  @JsonIgnore
  @Column(name = "id")
  private UUID id;

  @Column(name = "bookcase_key")
  private String bookcaseKey;

  @JsonIgnore
  @Column(name = "foldername")
  private String foldername;

  @JsonIgnore
  @Column(name = "sb_model")
  private String sbModel;

  @JsonIgnore
  @Column(name = "info")
  private String info;

  @Column(name = "bookcase_version", table = "bookcase_version")
  private String bookcaseVersion;

  @JsonIgnore
  @Column(name = "offline_filename", table = "bookcase_version")
  private String offlineFilename;

  @JsonIgnore
  @Column(name = "offline_filepath", table = "bookcase_version")
  private String offlineFilepath;

  @Column(name = "bookcase_version_status_code", table = "bookcase_version")
  private String bookcaseVersionStatusCode;

  @JsonIgnore
  @Column(name = "version_timestamp", table = "bookcase_version")
  private Timestamp versionTimestamp;

  @JsonIgnore
  @Column(name = "created_by", table = "bookcase_version")
  private String createdBy;

  @Column(name = "creation_date", table = "bookcase_version")
  private Date creationDate;

  @JsonIgnore
  @Column(name = "last_updated_by", table = "bookcase_version")
  private String lastUpdatedBy;

  @Column(name = "last_updated_date", table = "bookcase_version")
  private Date lastUpdatedDate;

  @JsonIgnore
  @Column(name = "title", table = "bookcase_version")
  private String title;

  public BookcaseBookcaseVersionDto() {
  }

  public BookcaseBookcaseVersionDto(String bookcaseKey, String bookcaseVersion,
      String bookcaseVersionStatusCode, Date creationDate, Date lastUpdatedDate) {
    this.bookcaseKey = bookcaseKey;
    this.bookcaseVersion = bookcaseVersion;
    this.bookcaseVersionStatusCode = bookcaseVersionStatusCode;
    this.creationDate = creationDate;
    this.lastUpdatedDate = lastUpdatedDate;
  }

  public String getBookcaseKey() {
    return bookcaseKey;
  }

  public void setBookcaseKey(String bookcaseKey) {
    this.bookcaseKey = bookcaseKey;
  }

  public String getBookcaseVersion() {
    return bookcaseVersion;
  }

  public void setBookcaseVersion(String bookcaseVersion) {
    this.bookcaseVersion = bookcaseVersion;
  }

  public String getBookcaseVersionStatusCode() {
    return bookcaseVersionStatusCode;
  }

  public void setBookcaseVersionStatusCode(String bookcaseVersionStatusCode) {
    this.bookcaseVersionStatusCode = bookcaseVersionStatusCode;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public Date getLastUpdatedDate() {
    return lastUpdatedDate;
  }

  public void setLastUpdatedDate(Date lastUpdatedDate) {
    this.lastUpdatedDate = lastUpdatedDate;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getFoldername() {
    return foldername;
  }

  public void setFoldername(String foldername) {
    this.foldername = foldername;
  }

  public String getSbModel() {
    return sbModel;
  }

  public void setSbModel(String sbModel) {
    this.sbModel = sbModel;
  }

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  public String getOfflineFilename() {
    return offlineFilename;
  }

  public void setOfflineFilename(String offlineFilename) {
    this.offlineFilename = offlineFilename;
  }

  public String getOfflineFilepath() {
    return offlineFilepath;
  }

  public void setOfflineFilepath(String offlineFilepath) {
    this.offlineFilepath = offlineFilepath;
  }

  public Timestamp getVersionTimestamp() {
    return versionTimestamp;
  }

  public void setVersionTimestamp(Timestamp versionTimestamp) {
    this.versionTimestamp = versionTimestamp;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getLastUpdatedBy() {
    return lastUpdatedBy;
  }

  public void setLastUpdatedBy(String lastUpdatedBy) {
    this.lastUpdatedBy = lastUpdatedBy;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
