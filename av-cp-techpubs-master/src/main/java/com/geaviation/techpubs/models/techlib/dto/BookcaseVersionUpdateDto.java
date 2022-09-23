package com.geaviation.techpubs.models.techlib.dto;

import java.util.List;

/**
 * DTO for bookcase version statuses updated from the endpoint
 */
public class BookcaseVersionUpdateDto {

  private List<BookcaseVersionDto> bookcaseVersions;

  public BookcaseVersionUpdateDto() {
  }

  public BookcaseVersionUpdateDto(List<BookcaseVersionDto> bookcaseVersions) {
    this.bookcaseVersions = bookcaseVersions;
  }

  public List<BookcaseVersionDto> getBookcaseVersions() {
    return bookcaseVersions;
  }

  public void setBookcaseVersions(
      List<BookcaseVersionDto> bookcaseVersions) {
    this.bookcaseVersions = bookcaseVersions;
  }
}
