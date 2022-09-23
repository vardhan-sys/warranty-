package com.geaviation.techpubs.models;


import java.util.Date;
import java.util.UUID;
import javax.xml.bind.annotation.XmlElement;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PageblkDetailsDAO extends PageblkDAO {

  private String bookTitle;
  private String fileExtension;
  private boolean isMultiMatch;
  @JsonIgnore
  private UUID pageblkId;
  @JsonIgnore
  private String revision;
  private boolean approvedForPublish;

  public PageblkDetailsDAO() {
    super();
  }

  public PageblkDetailsDAO
      (String bookKey, String bookTitle, String fileName, String key, String title,
          Date revisionDate, String bookcaseKey, String bookcaseTitle, String type, Object metadata,
          String version) {
    super(bookKey, fileName, key, title, revisionDate, bookcaseKey, bookcaseTitle, type, metadata,
        version);
    this.bookTitle = bookTitle;
  }

  public PageblkDetailsDAO
      (String bookKey, String bookTitle, String fileName, String key, String title,
          String bookcaseKey, String bookcaseTitle, String type, String version) {
    this(bookKey, bookTitle, fileName, key, title, null, bookcaseKey,
        bookcaseTitle, type, null, version);
  }

  public PageblkDetailsDAO
          (String bookKey, String key, String bookcaseKey, String type
                  , boolean approvedForPublish, String fileName, String version) {
    this(bookKey, null, fileName, key, null, null, bookcaseKey, null, type, null,
            version);
    this.approvedForPublish = approvedForPublish;
  }

  public PageblkDetailsDAO
          (String bookKey, String bookTitle, String fileName, UUID pageblkId, boolean approvedForPublish, String key, String title,
           Date revisionDate, String bookcaseKey, String bookcaseTitle, String type, Object metadata,
           String version, String revision) {
    this(bookKey, bookTitle, fileName, key, title, revisionDate, bookcaseKey, bookcaseTitle, type, metadata,
            version);
    this.pageblkId = pageblkId;
    this.approvedForPublish = approvedForPublish;
    this.revision = revision;

  }

  public PageblkDetailsDAO( String key, String revision) {
    this(null, null, null, key, null, null, null, null, null, null,
            null);
    this.revision = revision;

  }
  public String getBookTitle() {
    return bookTitle;
  }

  public void setBookTitle(String bookTitle) {
    this.bookTitle = bookTitle;
  }
  
  public String getFileExtension() {
    return fileExtension;
  }

  public void setFileExtension(String fileExtension) {
    this.fileExtension = fileExtension;
  }

  @JsonProperty("multiMatch")
  @XmlElement(name = "multiMatch")
  public boolean isMultiMatch() {
    return isMultiMatch;
  }

  public void setMultiMatch(boolean multiMatch) {
    isMultiMatch = multiMatch;
  }

  public UUID getPageblkId() {
    return pageblkId;
  }

  public void setPageblkId(UUID pageblkId) {
    this.pageblkId = pageblkId;
  }

  public String getRevision() {
    return revision;
  }

  public void setRevision(String revision) {
    this.revision = revision;
  }


  public boolean isApprovedForPublish() {
    return approvedForPublish;
  }

  public void setApprovedForPublish(boolean approvedForPublish) {
    this.approvedForPublish = approvedForPublish;
  }
}
