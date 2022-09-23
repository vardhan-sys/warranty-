package com.geaviation.techpubs.models.techlib;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;


@Table(name = "cortona_lookup", schema = "techlib")
@Entity
public class CortonaLookupEntity {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "html_filename", nullable = false)
  private String htmlFileName;

  @Column(name = "cortona_filename", nullable = false)
  private String cortonaFilename;

  @Column(name = "bookcase", nullable = false)
  private String bookcase;

  @Column(name = "bookcase_version", nullable = false)
  private String bookcaseVersion;

  @Column(name = "book", nullable = false)
  private String book;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getHtmlFileName() {
    return htmlFileName;
  }

  public void setHtmlFileName(String htmlFileName) {
    this.htmlFileName = htmlFileName;
  }

  public String getCortonaFilename() {
    return cortonaFilename;
  }

  public void setCortonaFilename(String cortonaFilename) {
    this.cortonaFilename = cortonaFilename;
  }

  public void setBookcaseVersion(String bookcaseVersion) {
    this.bookcaseVersion = bookcaseVersion;
  }

  public String getBookcaseVersion() {
    return bookcaseVersion;
  }

  public void setBookcase(String bookcase) {
    this.bookcase = bookcase;
  }

  public String getBookcase() {
    return bookcase;
  }

  public String getBook() {
    return book;
  }

  public void setBook(String book) {
    this.book = book;
  }
}
