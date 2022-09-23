package com.geaviation.techpubs.service.impl;

import static com.geaviation.techpubs.data.util.DataConstants.IC;
import static com.geaviation.techpubs.services.util.AppConstants.DVD;
import static com.geaviation.techpubs.services.util.AppConstants.MANUAL;
import static com.geaviation.techpubs.services.util.AppConstants.SOURCE;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import com.geaviation.techpubs.data.api.IDocumentData;
import com.geaviation.techpubs.data.api.IProgramData;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.exceptions.TechpubsException.TechpubsAppError;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.ProgramItemModel;
import com.geaviation.techpubs.models.Property;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.services.impl.ProgramAppSvcImpl;
import com.geaviation.techpubs.services.util.AppConstants;
import com.geaviation.techpubs.services.util.TechpubsAppUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.map.HashedMap;
import org.dom4j.DocumentException;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ProgramAppSvcImplTest {

  public static final String PROGRAM = "program";
  @InjectMocks
  ProgramAppSvcImpl programAppSvc;

  @Mock
  IProgramData iProgramDataMock;

  @Mock
  IDocumentData iDocumentDataMock;

  @Mock
  TechpubsAppUtil techpubsAppUtilMock;

  Map<String, String> queryParams;


  @Before
  public void setup() throws TechpubsException, IOException, DocumentException {
    // Instantiate class we're testing and inject mocks
    this.programAppSvc = new ProgramAppSvcImpl();
    MockitoAnnotations.initMocks(this);

    queryParams = new HashedMap();
    queryParams.put(AppConstants.ICOLUMNS, "8");
    queryParams.put(AppConstants.IDISPLAYLENGTH, "10");
    queryParams.put(AppConstants.IDISPLAYSTART, "0");
    queryParams.put(AppConstants.SECHO, "4");
    queryParams.put(MANUAL, "Test");

    Property prop = new Property();
    prop.setPropName("org.groupname");
    prop.setPropValue("blarg");
    when(techpubsAppUtilMock.getProperty(isA(String.class), isA(String.class), isA(String.class))).thenReturn(new ArrayList(Arrays.asList(prop)));
    when(iProgramDataMock.getProgramsByRoles(isA(List.class), isA(SubSystem.class))).thenReturn(new ArrayList(Arrays.asList(PROGRAM)));
    when(iProgramDataMock.getProgramItem(isA(String.class), isA(SubSystem.class))).thenReturn(new ProgramItemModel());
    when(iDocumentDataMock.getCatalogFileDocuments(isA(ProgramItemModel.class), isA(String.class), isA(
        Map.class))).thenReturn(new ArrayList(Arrays.asList(new DocumentItemModel())));
    when(iDocumentDataMock.getCatalogDocuments((isA(ProgramItemModel.class)), isA(String.class), isA(
        Map.class))).thenReturn(new ArrayList(Arrays.asList(new DocumentItemModel())));

  }

  @Test
  public void whenSsoIdIsNullThenShouldThrowTechpubsExceptionWithNotAuthorizedAppError(){
    Throwable e =  Assertions.assertThrows(TechpubsException.class, () -> programAppSvc.getDownloadDocuments(null, "portalId",
       PROGRAM, "downloadType", "type", queryParams));
    assertEquals(((TechpubsException) e).getTechpubsAppError(), TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
  }

  @Test
  public void whenSsoIdIsEmptyThenShouldThrowTechpubsExceptionWithNotAuthorizedAppError(){
    Throwable e =  Assertions.assertThrows(TechpubsException.class, () -> programAppSvc.getDownloadDocuments("", "portalId",
        PROGRAM, "downloadType", "type", queryParams));
    assertEquals(((TechpubsException) e).getTechpubsAppError(), TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
  }

  @Test
  public void whenPortalIdIsEmptyThenShouldThrowTechpubsExceptionWithNotAuthorizedAppError(){
    Throwable e =  Assertions.assertThrows(TechpubsException.class, () -> programAppSvc.getDownloadDocuments("sso", "",
        PROGRAM, "downloadType", "type", queryParams));
    assertEquals(((TechpubsException) e).getTechpubsAppError(),TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
  }

  @Test
  public void whenPortalIdIsNullThenShouldThrowTechpubsExceptionWithNotAuthorizedAppError(){
    Throwable e =  Assertions.assertThrows(TechpubsException.class, () -> programAppSvc.getDownloadDocuments("sso", null,
        PROGRAM, "downloadType", "type", queryParams));
    assertEquals(((TechpubsException) e).getTechpubsAppError(), TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
  }

  @Test
  public void whenDownloadTypeIsDvdAndManualIsTypeThenShouldThrowTechPubsExceptionWithInternal_errorApp_error(){
    Throwable e =  Assertions.assertThrows(TechpubsException.class, () -> programAppSvc.getDownloadDocuments("sso", "portalId",
        PROGRAM, DVD, MANUAL, queryParams));
    assertEquals(((TechpubsException) e).getTechpubsAppError(), TechpubsAppError.INTERNAL_ERROR);
  }

  @Test
  public void whenExceptionThrownWhileGettingUserRolesThenShouldThrowTheSameException()
      throws TechpubsException {
    when(techpubsAppUtilMock.getProperty(isA(String.class), isA(String.class), isA(String.class))).thenThrow(new NullPointerException());
    Assertions.assertThrows(NullPointerException.class, () -> programAppSvc.getDownloadDocuments("sso", "portalId",
        PROGRAM, SOURCE, IC, queryParams));
  }

  @Test
  public void whenExceptionThrownWhileGettingProgramsByRolesThenShouldThrowTheSameException()
      throws TechpubsException {
    when(iProgramDataMock.getProgramsByRoles(isA(List.class), isA(SubSystem.class))).thenThrow(new NullPointerException());
    Assertions.assertThrows(NullPointerException.class, () -> programAppSvc.getDownloadDocuments("sso", "portalId",
        PROGRAM, SOURCE, IC,  queryParams));
  }

  @Test
  public void whenTheRequestedProgramIsNotInListOfAuthorizedProgramsThenThrowTechPubsExceptionWithNoProgramsAvailableAppError()
      throws TechpubsException {
    when(iProgramDataMock.getProgramsByRoles(isA(List.class), isA(SubSystem.class))).thenReturn(new ArrayList(Arrays.asList("random program")));
    Assertions.assertThrows(TechpubsException.class, () -> programAppSvc.getDownloadDocuments("sso", "portalId",
        PROGRAM, SOURCE, IC,  queryParams));

  }

  @Test
  public void whenGetProgramItemThenShouldThrowTheSameException()
      throws DocumentException, TechpubsException, IOException {
    when(iProgramDataMock.getProgramItem(isA(String.class), isA(SubSystem.class))).thenThrow(new NullPointerException());
    Assertions.assertThrows(NullPointerException.class, () -> programAppSvc.getDownloadDocuments("sso", "portalId",
        PROGRAM, SOURCE, IC, queryParams));
  }

  @Test
  public void whenDataTableParametersAreInvalidThenShouldThrowTechpubsExceptionWithInvalidParameterException()
      throws DocumentException, TechpubsException, IOException {
    when(iProgramDataMock.getProgramItem(isA(String.class), isA(SubSystem.class))).thenReturn(null);
    Throwable e =  Assertions.assertThrows(TechpubsException.class, () -> programAppSvc.getDownloadDocuments("sso", "portalId",
        PROGRAM, SOURCE, IC, queryParams));
    assertEquals(((TechpubsException) e).getTechpubsAppError(),TechpubsAppError.INTERNAL_ERROR);
  }

}
