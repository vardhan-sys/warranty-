package com.geaviation.techpubs.service.impl;

import static com.geaviation.techpubs.services.util.AppConstants.PROGRAM;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import com.geaviation.techpubs.data.api.IProgramData;
import com.geaviation.techpubs.data.api.IResourceData;
import com.geaviation.techpubs.data.api.techlib.IBookcaseVersionData;
import com.geaviation.techpubs.data.api.techlib.IPageBlkData;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.exceptions.TechpubsException.TechpubsAppError;
import com.geaviation.techpubs.models.ProgramItemModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.services.api.IProgramApp;
import com.geaviation.techpubs.services.impl.ManualAppSvcImpl;
import com.geaviation.techpubs.services.util.TechpubsAppUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.dom4j.DocumentException;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;



public class ManualAppSvcImplTest {

  public static class GetHTMLResourceTDWithPageblkTypeTest{

    public static String bandwidth = "low";
    public static boolean multibrowserDocumentRequired = false;


    @Mock
    IProgramApp iProgramAppMock;

    @Mock
    IResourceData iResourceData;

    @Mock
    IProgramData iprogramDataMock;

    @Mock
    IBookcaseVersionData iBookcaseVersionDataMock;

    @Mock
    TechpubsAppUtil techpubsAppUtilMock;

    @Mock
    IPageBlkData iPageBlkDataMock;



    @InjectMocks
    ManualAppSvcImpl manualApp;
    private static final String bookcase1Key = "bookcase1Key";
    private static final String bookcase2Key = "bookcase2Key";
    private static final String filenameByBandwidthValue = "bandwidthString";
    private static final byte[] returnValue = "Return value".getBytes();

    @Before
    public void setup () throws TechpubsException, IOException, DocumentException {
      manualApp = new ManualAppSvcImpl();
      MockitoAnnotations.initMocks(this);

      ProgramItemModel mockProgramItem = new ProgramItemModel();
      mockProgramItem.setProgramOnlineVersion("some version");
      when(iProgramAppMock.getAuthorizedPrograms(isA(String.class), isA(String.class), isA(
          SubSystem.class))).thenReturn(new ArrayList<>(Arrays.asList(bookcase1Key)));
      when(iprogramDataMock.getProgramItem(isA(String.class), isA(SubSystem.class)))
          .thenReturn(mockProgramItem);
      when(iBookcaseVersionDataMock.findOnlineBookcaseVersion(isA(String.class)))
          .thenReturn("some version");
      when(techpubsAppUtilMock.getCurrentIcaoCode(isA(String.class), isA(String.class)))
          .thenReturn("ICAO");
      when(iPageBlkDataMock.pageblkIsEnabledForIcao(isA(String.class), isA(String.class), isA(String.class), isA(String.class), isA(String.class)))
          .thenReturn(true);
      when(iResourceData.getHTMLResourceTD(isA(ProgramItemModel.class), isA(String.class),
           isA(String.class), isA(String.class), isA(boolean.class), isA(boolean.class)))
          .thenReturn(returnValue);
      when(iResourceData.getHTMLResourceTD(isA(ProgramItemModel.class), isA(String.class),
          isA(String.class), isA(String.class), isA(String.class), isA(boolean.class)))
          .thenReturn(returnValue);
      when(iResourceData.getFilenameByBandwidthValue(isA(String.class), isA(ProgramItemModel.class),
          isA(String.class), isA(String.class), isA(boolean.class)))
          .thenReturn(filenameByBandwidthValue);
    }


    @Test
    public void whenSSOIDIsNullShouldThrowTechpubsException () {
      Throwable e = Assertions.assertThrows(TechpubsException.class, () -> manualApp
          .getHTMLResourceTD(null, "portalId", PROGRAM, "manual", "filename", bandwidth, multibrowserDocumentRequired));
      assertTrue(((TechpubsException) e).getTechpubsAppError()
          .equals(TechpubsException.TechpubsAppError.NOT_AUTHORIZED));
    }

    @Test
    public void whenSSOIDIsEmptpyShouldThrowTechpubsException () {
      Throwable e = Assertions.assertThrows(TechpubsException.class, () -> manualApp
          .getHTMLResourceTD("", "portalId", PROGRAM, "manual", "filename",
             bandwidth, multibrowserDocumentRequired));

      assertTrue(((TechpubsException) e).getTechpubsAppError()
          .equals(TechpubsException.TechpubsAppError.NOT_AUTHORIZED));
    }

    @Test
    public void whenPortalIDIsNullShouldThrowTechpubsException () {
      Throwable e = Assertions.assertThrows(TechpubsException.class, () -> manualApp
          .getHTMLResourceTD("sso", null, PROGRAM, "manual", "filename",
              bandwidth, multibrowserDocumentRequired));
      assertTrue(((TechpubsException) e).getTechpubsAppError()
          .equals(TechpubsException.TechpubsAppError.NOT_AUTHORIZED));
    }

    @Test
    public void whenPortalIDIsEmptyShouldThrowTechpubsException () {
      Throwable e = Assertions.assertThrows(TechpubsException.class, () -> manualApp
          .getHTMLResourceTD("sso", "", PROGRAM, "manual", "filename",
              bandwidth, multibrowserDocumentRequired));
      assertTrue(((TechpubsException) e).getTechpubsAppError()
          .equals(TechpubsException.TechpubsAppError.NOT_AUTHORIZED));
    }

    @Test
    public void whenProgramIsNotAuthorizedShouldThrowTechpubsException () throws TechpubsException {
      when(iProgramAppMock
          .getAuthorizedPrograms(isA(String.class), isA(String.class), isA(SubSystem.class)))
          .thenReturn(new ArrayList<>(Arrays.asList(bookcase1Key)));
      Throwable e = Assertions.assertThrows(TechpubsException.class, () -> manualApp
          .getHTMLResourceTD("sso", "portalId", bookcase2Key, "manual",
              "filename", bandwidth, multibrowserDocumentRequired));
      assertTrue(((TechpubsException) e).getTechpubsAppError()
          .equals(TechpubsAppError.NO_PROGRAMS_AVAILABLE));
    }

    @Test
    public void whenAByteArrayIsRetrievedForTheRequestedFileThenShouldReturnTheSameByteArray ()
        throws TechpubsException {
      byte[] result = manualApp
          .getHTMLResourceTD("sso", "portalId", bookcase1Key, "manual",
              "filename", bandwidth, multibrowserDocumentRequired);
      assertEquals(returnValue, result);
    }

    @Test
    public void whenNullIsRetrievedInsteadOfTheRequestedFileThenShouldReturnNull ()
        throws TechpubsException {
      when(iResourceData.getHTMLResourceTD(isA(ProgramItemModel.class), isA(String.class),
          isA(String.class), isA(String.class), isA(boolean.class),
          isA(boolean.class))).thenReturn(null);
      byte[] result = manualApp
          .getHTMLResourceTD("sso", "portalId", bookcase1Key, "manual",
              "filename", bandwidth, multibrowserDocumentRequired);
      assertEquals(null, result);
    }

    @Test

    public void whenAnExceptionIsThrownWhileRetrievingTheRequestedFileThenShouldThrowTechpubsExceptionWithInternalAppError()
        throws TechpubsException {
      when(iResourceData.getHTMLResourceTD(isA(ProgramItemModel.class), isA(String.class),
           isA(String.class), isA(String.class), isA(boolean.class),
          isA(boolean.class))).thenThrow(new NullPointerException());

      Throwable e = Assertions.assertThrows(TechpubsException.class, () -> manualApp
          .getHTMLResourceTD("sso", "portalId", bookcase1Key, "manual",
              "filename", bandwidth, multibrowserDocumentRequired));

      assertTrue(((TechpubsException) e).getTechpubsAppError()
          .equals(TechpubsAppError.INTERNAL_ERROR));
    }

    @Test
    public void whenAnExceptionIsThrownWhileRetrievingTheProgramItemThenShouldThrowTechpubsExceptionWithInternalAppError
        ()
        throws TechpubsException, IOException, DocumentException {
      when(iprogramDataMock.getProgramItem(isA(String.class), isA(SubSystem.class)))
          .thenThrow(new NullPointerException());
      Throwable e = Assertions.assertThrows(TechpubsException.class, () -> manualApp
          .getHTMLResourceTD("sso", "portalId", bookcase1Key, "manual",
              "filename", bandwidth, multibrowserDocumentRequired));
      assertTrue(((TechpubsException) e).getTechpubsAppError()
          .equals(TechpubsAppError.INTERNAL_ERROR));
    }

    @Test
    public void whenPageblkTypeIsSMMAndAnExceptionIsThrownWhileRetrievingTheUsersIcaoThenShouldThrowTechpubsExceptionWithInternalAppError ()
        throws TechpubsException {
      when(techpubsAppUtilMock.getCurrentIcaoCode(isA(String.class), isA(String.class)))
          .thenThrow(new NullPointerException());
      Throwable e = Assertions.assertThrows(TechpubsException.class, () -> manualApp
          .getHTMLResourceTD("sso", "portalId", bookcase1Key, "manual",
              "filename", bandwidth, multibrowserDocumentRequired));
      assertTrue(((TechpubsException) e).getTechpubsAppError()
          .equals(TechpubsAppError.INTERNAL_ERROR));
    }

    @Test
    public void whenPageblkTypeIsSMMAndAnExceptionIsThrownWhileDeterminingIfUserIsEnabledToViewTheFileThenShouldThrowTechpubsExceptionWithInternalAppError ()
        throws TechpubsException {
      when(iPageBlkDataMock.pageblkIsEnabledForIcao(isA(String.class), isA(String.class), isA(String.class), isA(String.class), isA(String.class)))
          .thenThrow(new NullPointerException());
      Throwable e = Assertions.assertThrows(TechpubsException.class, () -> manualApp
          .getHTMLResourceTD("sso", "portalId", bookcase1Key, "manual",
              "filename", bandwidth, multibrowserDocumentRequired));
      assertTrue(((TechpubsException) e).getTechpubsAppError()
          .equals(TechpubsAppError.INTERNAL_ERROR));
    }

    @Test
    public void whenPortalIDIsNullAndSbIsntValidShouldThrowTechpubsException()
        throws TechpubsException {
      byte[] result = manualApp
          .getSbResource("sso", null, "manual", bookcase1Key,
              "72-0155", "bandwidth", false);
      assertEquals(null, result);
    }

    @Test
    public void whenPortalIDIsEmptyAndSbIsntValidShouldReturnNull() throws TechpubsException {
      byte[] result = manualApp
          .getSbResource("sso", "", "manual", bookcase1Key,
              "72-0155", "bandwidth", false);
      assertEquals(null, result);
    }

    @Test
    public void whenSbNumberIsNullShouldReturnNull() throws TechpubsException {
      byte[] result = manualApp
          .getSbResource("sso", "portalId", "manual", bookcase1Key,
              null, "bandwidth", false);
      assertEquals(null, result);
    }

    @Test
    public void whenValidateUserAndReturnFileIsGoodThenReturnValidData() throws TechpubsException {
      byte[] result = manualApp
          .validateUserAndReturnFile("ssoId", "portalId", bookcase1Key, "manual",
              "genx-1b-sb-72-0154-r02.htm", new ProgramItemModel(),
              "bandwidth", false);
      assertEquals(returnValue, result);
    }

  }
}
