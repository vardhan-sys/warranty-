package com.geaviation.techpubs.models.techlib;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "bookcase_version", schema = "techlib")
public class BookcaseVersionEntity {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "title", nullable = false, length = -1)
  private String title;

  @Column(name = "bookcase_version", nullable = false, length = -1)
  private String bookcaseVersion;

  @Column(name = "offline_filename", length = -1)
  private String offlineFilename;

  @Column(name = "offline_filepath", length = -1)
  private String offlineFilepath;

  @Column(name = "bookcase_version_status_code", length = -1)
  private String bookcaseVersionStatus;

  @Column(name = "version_timestamp")
  private Timestamp versionTimestamp;

  @JsonIgnore
  @Column(name = "created_by", length = -1)
  private String createdBy;

  @JsonIgnore
  @Column(name = "creation_date")
  private Timestamp creationDate;

  @JsonIgnore
  @Column(name = "last_updated_by", length = -1)
  private String lastUpdatedBy;

  @JsonIgnore
  @Column(name = "last_updated_date")
  private Timestamp lastUpdatedDate;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
  @JoinColumn(name = "bookcase_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
  private BookcaseEntity bookcaseId;

  public BookcaseVersionEntity() {
  }

  public BookcaseVersionEntity(String title, String bookcaseVersion) {
    this.title = title;
    this.bookcaseVersion = bookcaseVersion;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getBookcaseVersion() {
    return bookcaseVersion;
  }

  public void setBookcaseVersion(String bookcaseVersion) {
    this.bookcaseVersion = bookcaseVersion;
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

  public String getBookcaseVersionStatus() {
    return bookcaseVersionStatus;
  }

  public void setBookcaseVersionStatus(String bookcaseVersionStatus) {
    this.bookcaseVersionStatus = bookcaseVersionStatus;
  }

  public BookcaseEntity getBookcase() {
    return bookcaseId;
  }

  public void setBookcase(BookcaseEntity bookcaseId) {
    this.bookcaseId = bookcaseId;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public Timestamp getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Timestamp creationDate) {
    this.creationDate = creationDate;
  }

  public String getLastUpdatedBy() {
    return lastUpdatedBy;
  }

  public void setLastUpdatedBy(String lastUpdatedBy) {
    this.lastUpdatedBy = lastUpdatedBy;
  }

  public Timestamp getLastUpdatedDate() {
    return lastUpdatedDate;
  }

  public void setLastUpdatedDate(Timestamp lastUpdatedDate) {
    this.lastUpdatedDate = lastUpdatedDate;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookcaseVersionEntity that = (BookcaseVersionEntity) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(title, that.title) &&
        Objects.equals(bookcaseVersion, that.bookcaseVersion) &&
        Objects.equals(offlineFilename, that.offlineFilename) &&
        Objects.equals(offlineFilepath, that.offlineFilepath) &&
        Objects.equals(versionTimestamp, that.versionTimestamp) &&
        Objects.equals(createdBy, that.createdBy) &&
        Objects.equals(creationDate, that.creationDate) &&
        Objects.equals(lastUpdatedBy, that.lastUpdatedBy) &&
        Objects.equals(lastUpdatedDate, that.lastUpdatedDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, bookcaseVersion, offlineFilename, offlineFilepath,
        versionTimestamp, createdBy, creationDate, lastUpdatedBy, lastUpdatedDate);
  }
}
