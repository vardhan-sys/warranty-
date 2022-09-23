package com.geaviation.techpubs.model.techlib.dto;


import static org.assertj.core.api.Assertions.assertThat;

import com.geaviation.techpubs.models.techlib.dto.BookcaseVersionDto;
import com.geaviation.techpubs.services.util.AppConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class BookcaseVersionDtoTest {

  private static final String BAD_STATUS = "signed author edition";

  @Test
  public void checkValidOfflineTest() {
    BookcaseVersionDto bookcaseVersionDTO = new BookcaseVersionDto("1.0", "offline");
    assertThat(bookcaseVersionDTO.hasValidStatus()).isTrue();
    assertThat(bookcaseVersionDTO.getAuditAction()).isEqualTo(AppConstants.BOOKCASE_SET_OFFLINE);
  }

  @Test
  public void checkValidOnlineTest() {
    BookcaseVersionDto bookcaseVersionDTO = new BookcaseVersionDto("1.0", "online");
    assertThat(bookcaseVersionDTO.hasValidStatus()).isTrue();
    assertThat(bookcaseVersionDTO.getAuditAction()).isEqualTo(AppConstants.BOOKCASE_SET_ONLINE);
  }

  @Test
  public void checkValidSuspendedTest() {
    BookcaseVersionDto bookcaseVersionDTO = new BookcaseVersionDto("1.0", "suspended");
    assertThat(bookcaseVersionDTO.hasValidStatus()).isTrue();
    assertThat(bookcaseVersionDTO.getAuditAction()).isEqualTo(AppConstants.BOOKCASE_SET_SUSPENDED);
  }

  @Test
  public void checkValidArchivedTest() {
    BookcaseVersionDto bookcaseVersionDTO = new BookcaseVersionDto("1.0", "archived");
    assertThat(bookcaseVersionDTO.hasValidStatus()).isTrue();
    assertThat(bookcaseVersionDTO.getAuditAction()).isEqualTo(AppConstants.BOOKCASE_SET_ARCHIVED);
  }

  @Test
  public void checkValidOnlineAndReleaseDateChangedTest() {
    BookcaseVersionDto bookcaseVersionDTO = new BookcaseVersionDto("1.0", "online","2019-10-15");
    assertThat(bookcaseVersionDTO.hasValidStatus()).isTrue();
    assertThat(bookcaseVersionDTO.getAuditAction()).isEqualTo(AppConstants.BOOKCASE_SET_ONLINE);
  }

  @Test
  public void checkInvalidTest() {
    BookcaseVersionDto bookcaseVersionDTO = new BookcaseVersionDto("1.0", BAD_STATUS);
    assertThat(bookcaseVersionDTO.hasValidStatus()).isFalse();
  }

  @Test(expected = IllegalArgumentException.class)
  public void checkNoActionTest() {
    // this should break and throw an exception
    BookcaseVersionDto bookcaseVersionDTO = new BookcaseVersionDto("1.0", BAD_STATUS);
    assertThat(bookcaseVersionDTO.getAuditAction()).isEqualTo("Set to " + BAD_STATUS);
  }
}
