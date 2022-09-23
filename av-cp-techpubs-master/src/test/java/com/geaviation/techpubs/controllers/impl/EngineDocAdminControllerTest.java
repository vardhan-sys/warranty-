package com.geaviation.techpubs.controllers.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

import com.geaviation.techpubs.controllers.impl.admin.EngineDocAdminController;
import com.geaviation.techpubs.controllers.requests.SortBy;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.Response;
import com.geaviation.techpubs.models.techlib.dto.EngineDocumentAddReachDTO;
import com.geaviation.techpubs.models.techlib.dto.EngineDocumentDTO;
import com.geaviation.techpubs.models.techlib.enginedoc.EngineDocumentEntity;
import com.geaviation.techpubs.services.api.admin.IEngineDocAdminApp;
import com.geaviation.techpubs.services.impl.admin.AuthServiceImpl;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

public class EngineDocAdminControllerTest {

  @Mock
  private HttpServletRequest request;

  @Mock
  private AuthServiceImpl authServiceImpl;

  @Mock
  IEngineDocAdminApp mockIEngineDocAdminApp;

  @Mock
  EngineDocumentDTO engineDocumentDTO;

  @InjectMocks
  EngineDocAdminController engineDocAdminController;

  public static final String PORTAL = "CWC";
  public static final String SSOID = "sso-id";
  private static final String SM_SSOID = "sm_ssoid";
  private static final String PORTAL_ID = "portal_id";
  private static final String DOC_TYPE = "doc-type";
  private static final Integer PAGE = 0;
  private static final Integer SIZE = 10;
  private static final String SORT_BY = "sort-by";
  private static final String ID = "id";
  private static final String SEARCH_TERM = "search-term";



  EngineDocumentAddReachDTO engineDocumentReachDTO = new EngineDocumentAddReachDTO();

  Page<EngineDocumentDTO> engineDocs;


  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    when(request.getHeader(SM_SSOID)).thenReturn(SSOID);
    when(request.getHeader(PORTAL_ID)).thenReturn(PORTAL);

  }

  @Test
  public void getEngineDocTypeListReturns200() throws TechpubsException {
    doNothing().when(authServiceImpl)
        .checkResourceAccessForProduct(isA(String.class), isA(String.class),
            isA(HttpServletRequest.class), isA(String.class));
    ReflectionTestUtils.setField(engineDocAdminController, "sqlInjection", true);
    ReflectionTestUtils.setField(engineDocAdminController, "reachEndpointsActive", true);

    List<String> engineDocTypeList = new ArrayList<String>();
    when(mockIEngineDocAdminApp.getEngineDocumentTypes()).thenReturn(engineDocTypeList);
    ResponseEntity response = engineDocAdminController.getEngineDocumentTypes(SSOID, request);

    assertEquals(response.getStatusCodeValue(), 200);
  }

  @Test
  public void getEngineDocTypeListReturnsUnauthorizedStatus() throws TechpubsException {
    doThrow(TechpubsException.class).when(authServiceImpl)
        .checkResourceAccessForProduct(isNull(), isA(String.class),
            isA(HttpServletRequest.class), isA(String.class));
    ReflectionTestUtils.setField(engineDocAdminController, "reachEndpointsActive", true);

    List<String> engineDocTypeList = new ArrayList<String>();
    when(mockIEngineDocAdminApp.getEngineDocumentTypes()).thenReturn(engineDocTypeList);
    ResponseEntity response = engineDocAdminController.getEngineDocumentTypes(null, request);

    assertEquals(response.getStatusCodeValue(), 401);
  }

  @Test
  public void getEngineDocTypeListNotFoundIfFeatureFlagFalse() throws TechpubsException {
    doNothing().when(authServiceImpl)
        .checkResourceAccessForProduct(isNull(), isA(String.class),
            isA(HttpServletRequest.class), isA(String.class));
    ReflectionTestUtils.setField(engineDocAdminController, "reachEndpointsActive", false);

    List<String> engineDocTypeList = new ArrayList<String>();
    when(mockIEngineDocAdminApp.getEngineDocumentTypes()).thenReturn(engineDocTypeList);
    ResponseEntity response = engineDocAdminController.getEngineDocumentTypes(SSOID, request);

    assertEquals(response.getStatusCodeValue(), 404);
  }
  @Test
  public void addNewEngineDocumentIsSuccessful() throws TechpubsException{
    doNothing().when(authServiceImpl)
        .checkResourceAccessForProduct(isA(String.class), isA(String.class), isA(HttpServletRequest.class), isA(String.class));
    EngineDocumentEntity newEngineDocument = new EngineDocumentEntity();
    doReturn(newEngineDocument).when(mockIEngineDocAdminApp)
        .addEngineDocuments(isA(EngineDocumentAddReachDTO.class), isA(String.class), isA(String.class));
    ReflectionTestUtils.setField(engineDocAdminController, "sqlInjection", true);
    ReflectionTestUtils.setField(engineDocAdminController, "reachEndpointsActive", true);
    ResponseEntity response = engineDocAdminController
        .addNewDocument(SSOID, PORTAL_ID, DOC_TYPE, engineDocumentReachDTO, request);
    assertEquals(response.getStatusCodeValue(), 201);
  }

  @Test
  public void ifReachEndpointsNotActiveShouldReturn404ErrorWhenTryingToAddEngineDocuments() throws TechpubsException{
    doNothing().when(authServiceImpl)
        .checkResourceAccessForProduct(isA(String.class), isA(String.class), isA(HttpServletRequest.class), isA(String.class));
    EngineDocumentEntity newEngineDocument = new EngineDocumentEntity();
    doReturn(newEngineDocument).when(mockIEngineDocAdminApp)
        .addEngineDocuments(isA(EngineDocumentAddReachDTO.class), isA(String.class), isA(String.class));
    ReflectionTestUtils.setField(engineDocAdminController, "reachEndpointsActive", false);
    ResponseEntity response = engineDocAdminController
        .addNewDocument(SSOID, PORTAL_ID, DOC_TYPE, engineDocumentReachDTO, request);
    assertEquals(response.getStatusCodeValue(), 404);
    assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
  }

  @Test
  public void ifSSOIsNullReturnsUnauthorizedWhenTryingToAddEngineDocuments() throws TechpubsException{
    doThrow(TechpubsException.class).when(authServiceImpl)
        .checkResourceAccessForProduct(isNull(),isA(String.class), isA(HttpServletRequest.class), isA(String.class));
    EngineDocumentEntity newEngineDocument = new EngineDocumentEntity();
    doReturn(newEngineDocument).when(mockIEngineDocAdminApp)
        .addEngineDocuments(isA(EngineDocumentAddReachDTO.class), isA(String.class), isA(String.class));
    ReflectionTestUtils.setField(engineDocAdminController, "reachEndpointsActive", true);
    ResponseEntity response = engineDocAdminController
        .addNewDocument(null, PORTAL_ID, DOC_TYPE, engineDocumentReachDTO, request);
    assertEquals(response.getStatusCodeValue(), 401);
  }

  @Test
  public void ifReachEndpointsActiveGetAllEngineDocuments() throws TechpubsException{
    doNothing().when(authServiceImpl)
        .checkResourceAccessForProduct(isA(String.class),isA(String.class), isA(HttpServletRequest.class), isA(String.class));
    List<String> engineModels = new ArrayList<>();
	engineModels.add("CF34-10A");
	when(mockIEngineDocAdminApp.getEngineDocuments(isA(String.class), isA(List.class), isA(String.class), isA(Integer.class),
			isA(Integer.class), isA(SortBy.class))).thenReturn(engineDocs);
    ReflectionTestUtils.setField(engineDocAdminController, "sqlInjection", true);
    ReflectionTestUtils.setField(engineDocAdminController, "reachEndpointsActive", true);
	ResponseEntity response = engineDocAdminController.getAllEngineDocuments(SSOID, PORTAL_ID, PAGE, SIZE, SORT_BY,
			DOC_TYPE, engineModels,SEARCH_TERM, request);
    assertEquals(response.getStatusCodeValue(), 200);
  }

  @Test
  public void ifSSOIsNullReturnsUnauthorizedToGetAllEngineDocuments() throws TechpubsException{
    doThrow(TechpubsException.class).when(authServiceImpl)
        .checkResourceAccessForProduct(isNull(),isA(String.class), isA(HttpServletRequest.class), isA(String.class));
    List<String> engineModels = new ArrayList<>();
    engineModels.add("CF34-10A");
	when(mockIEngineDocAdminApp.getEngineDocuments(isA(String.class), isA(List.class),isA(String.class), isA(Integer.class),
			isA(Integer.class), isA(SortBy.class))).thenReturn(engineDocs);
    ReflectionTestUtils.setField(engineDocAdminController, "sqlInjection", true);
    ReflectionTestUtils.setField(engineDocAdminController, "reachEndpointsActive", true);
	ResponseEntity response = engineDocAdminController.getAllEngineDocuments(null, PORTAL_ID, PAGE, SIZE, SORT_BY,
			DOC_TYPE, engineModels, SEARCH_TERM, request);
    assertEquals(response.getStatusCodeValue(), 401);
  }

  @Test
  public void ifReachEndpointsNotActiveShowsResponseNotFoundWhenTryingToGetAllEngineDocuments() throws TechpubsException{
    doNothing().when(authServiceImpl)
        .checkResourceAccessForProduct(isA(String.class),isA(String.class), isA(HttpServletRequest.class), isA(String.class));
    List<String> engineModels = new ArrayList<>();
    engineModels.add("CF34-10A");
    when(mockIEngineDocAdminApp.getEngineDocuments(isA(String.class), isA(List.class), isA(String.class), isA(Integer.class),
			isA(Integer.class), isA(SortBy.class))).thenReturn(engineDocs);
    ReflectionTestUtils.setField(engineDocAdminController, "sqlInjection", true);
    ReflectionTestUtils.setField(engineDocAdminController, "reachEndpointsActive", false);
	ResponseEntity response = engineDocAdminController.getAllEngineDocuments(SSOID, PORTAL_ID, PAGE, SIZE, SORT_BY,
			DOC_TYPE, engineModels, SEARCH_TERM, request);
    assertEquals(response.getStatusCodeValue(), 404);
  }

  @Test
  public void ifReachEndpointsNotActiveForGetPDFFromS3ThenShowsResponseNotFound()
      throws TechpubsException {
    doNothing().when(authServiceImpl)
        .checkResourceAccessForProduct(isA(String.class),isA(String.class), isA(HttpServletRequest.class), isA(String.class));
    ReflectionTestUtils.setField(engineDocAdminController, "sqlInjection", true);
    ReflectionTestUtils.setField(engineDocAdminController, "reachEndpointsActive", false);
    ResponseEntity response = engineDocAdminController
        .getPDFfromS3(ID,SSOID, PORTAL_ID,request);
    assertEquals(response.getStatusCodeValue(), 404);
  }


  @Test
  public void downloadREACHPDFFromS3IfOneHasAccess() throws TechpubsException, FileNotFoundException {
    doNothing().when(authServiceImpl)
        .checkResourceAccessForProduct(isA(String.class),isA(String.class), isA(HttpServletRequest.class), isA(String.class));
    Path path = Paths.get("src/test/resources/test.txt");
    InputStream is = new FileInputStream(path.toFile());

    Map fileMap = new HashMap();
    fileMap.put("filename", path);
    fileMap.put("stream", is);
    when(mockIEngineDocAdminApp.getFileInputStreamFromS3(isA(String.class))).thenReturn(fileMap);
    ReflectionTestUtils.setField(engineDocAdminController, "reachEndpointsActive", true);
    ReflectionTestUtils.setField(engineDocAdminController, "sqlInjection", true);

    ResponseEntity response = engineDocAdminController.getPDFfromS3(ID, SSOID, PORTAL_ID,request);
    assertEquals(response.getStatusCodeValue(), 200);
  }


  @Test
  public void downloadREACHPDFFromS3ReturnsInternalServerErrorIfNoFileFound() throws TechpubsException, FileNotFoundException {
    doNothing().when(authServiceImpl)
        .checkResourceAccessForProduct(isA(String.class),isA(String.class), isA(HttpServletRequest.class), isA(String.class));
    Path path = Paths.get("src/test/resources/test.txt");
    InputStream is = new FileInputStream(path.toFile());
    Map fileMap = new HashMap();
    when(mockIEngineDocAdminApp.getFileInputStreamFromS3(isA(String.class))).thenReturn(fileMap);
    ReflectionTestUtils.setField(engineDocAdminController, "reachEndpointsActive", true);
    ReflectionTestUtils.setField(engineDocAdminController, "sqlInjection", true);

    ResponseEntity response = engineDocAdminController.getPDFfromS3(ID, SSOID, PORTAL_ID,request);
    assertEquals(response.getStatusCodeValue(), 500);
  }

}