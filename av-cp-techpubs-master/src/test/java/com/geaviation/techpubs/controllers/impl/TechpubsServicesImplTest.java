package com.geaviation.techpubs.controllers.impl;

import com.geaviation.techpubs.data.impl.BookcaseDataImpl;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.exceptions.TechpubsException.TechpubsAppError;
import com.geaviation.techpubs.models.BookDAO;
import com.geaviation.techpubs.models.BookcaseContentModel;
import com.geaviation.techpubs.models.techlib.dto.BookcaseBookcaseVersionDto;
import com.geaviation.techpubs.services.api.IProgramApp;
import com.geaviation.techpubs.services.impl.BookcaseContentModelApp;
import com.geaviation.techpubs.services.impl.BookcaseTOCApp;
import com.geaviation.techpubs.services.util.AppConstants;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class TechpubsServicesImplTest {

  public static final String PORTAL = "CWC";
  public static final String SSOID = "212719881";
  private static final String SM_SSOID = "sm_ssoid";
  private static final String PORTAL_ID = "portal_id";

  @Mock
  private IProgramApp iProgramAppMock;

  @Mock
  private BookcaseTOCApp bookcaseTocAppMock;

  @Mock
  private HttpServletRequest request;


  @InjectMocks
  private DocumentDownloadControllerImpl documentDownloadController;

  @Before
  public void setUp() {
    // Instantiate class we're testing and inject mocks
    this.documentDownloadController = new DocumentDownloadControllerImpl();
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void getCloudFrontCookiesReturn500IfUserIsNotAuthorized() throws TechpubsException {
    List<String> programs = new ArrayList<>();
    when(iProgramAppMock.getAuthorizedPrograms(any(), any(), any())).thenReturn(programs);

    ResponseEntity response = documentDownloadController.getCloudFrontCookies("gek112060","CWC","gek108745",null);

    assertEquals(response.getStatusCodeValue(), 500);
  }

  //Remove this class during US478605 feature flag cleanup
  public static class GetBookcaseItemsTest {

    @Mock
    private BookcaseContentModelApp bookcaseContentModelApp;

    @Mock
    private HttpServletRequest request;

    //Remove this class during US478605 feature flag cleanup
    @InjectMocks
    private UnusedEndpointsControllerImpl unusedEndpointsController;

    private BookcaseContentModel bookcaseContentModel;
    private Map<String, String> queryParams;

    @Before
    public void setup() throws TechpubsException {
      BookDAO bookcaseContentDAO = new BookDAO();
      bookcaseContentDAO.setKey("key 1");

      queryParams = new HashedMap();
      queryParams.put(AppConstants.ICOLUMNS, "8");
      queryParams.put(AppConstants.MDATAPROP + 0, "bookcasetitle");
      queryParams.put(AppConstants.MDATAPROP + 1, "title");
      queryParams.put(AppConstants.MDATAPROP + 2, "revisiondate");
      queryParams.put(AppConstants.IDISPLAYLENGTH, "2");
      queryParams.put(AppConstants.IDISPLAYSTART, "1");
      queryParams.put(AppConstants.ICOLUMNS, "10");
      queryParams.put(AppConstants.SECHO, "4");
      queryParams.put(AppConstants.MDATAPROP + 3, "family");
      queryParams.put(AppConstants.MDATAPROP + 4, "model");
      queryParams.put(AppConstants.MDATAPROP + 5, "aircraft");
      queryParams.put(AppConstants.MDATAPROP + 6, "tail");
      queryParams.put(AppConstants.SSEARCH + 3, "family");
      queryParams.put(AppConstants.SSEARCH + 4, "model");
      queryParams.put(AppConstants.SSEARCH + 5, "aircraft");
      queryParams.put(AppConstants.SSEARCH + 6, "tail");
      queryParams.put(AppConstants.TYPE, "ic");

      bookcaseContentModel = new BookcaseContentModel(new ArrayList<>(Arrays.asList(
          bookcaseContentDAO)));
      MockitoAnnotations.initMocks(this);
      when(request.getHeader(SM_SSOID)).thenReturn(SSOID);
      when(request.getHeader(PORTAL_ID)).thenReturn(PORTAL);
      when(bookcaseContentModelApp
          .getBookcaseItemModel(isA(String.class), isA(String.class), isA(Map.class),
              isA(Map.class))).thenReturn(
          bookcaseContentModel);
    }

    @Test
    public void whenSSOIDIsNullShouldThrowTechpubsException() {
      when(request.getHeader(SM_SSOID)).thenReturn(null);
      Throwable e = Assertions.assertThrows(TechpubsException.class, () -> unusedEndpointsController
          .getBookcaseContent(new MultivaluedHashMap(queryParams), request));
      assertTrue(
          ((TechpubsException) e).getTechpubsAppError().equals(TechpubsAppError.NOT_AUTHORIZED));
    }

    @Test
    public void whenSSOIDIsEmptyShouldThrowTechpubsException() {
      when(request.getHeader(SM_SSOID)).thenReturn("");
      Throwable e = Assertions.assertThrows(TechpubsException.class, () -> unusedEndpointsController
          .getBookcaseContent(new MultivaluedHashMap(queryParams), request));
      assertTrue(
          ((TechpubsException) e).getTechpubsAppError().equals(TechpubsAppError.NOT_AUTHORIZED));
    }

    @Test
    public void whenPortalIdIsNullShouldThrowTechpubsException() {
      when(request.getHeader(PORTAL_ID)).thenReturn(null);
      Throwable e = Assertions.assertThrows(TechpubsException.class, () -> unusedEndpointsController
          .getBookcaseContent(new MultivaluedHashMap(queryParams), request));
      assertTrue(
          ((TechpubsException) e).getTechpubsAppError().equals(TechpubsAppError.NOT_AUTHORIZED));
    }

    @Test
    public void whenPortalIdEmptyShouldThrowTechpubsException() {
      when(request.getHeader(PORTAL_ID)).thenReturn("");
      Throwable e = Assertions.assertThrows(TechpubsException.class, () -> unusedEndpointsController
          .getBookcaseContent(new MultivaluedHashMap(queryParams), request));
      assertTrue(
          ((TechpubsException) e).getTechpubsAppError().equals(TechpubsAppError.NOT_AUTHORIZED));

    }

    @Test
    public void whenAQueryParameterIsNullShouldThrowTechpubsExceptionWithInvalidParameterException() {
      Map<String, String> parameterMap = new HashedMap(queryParams);
      parameterMap.put(AppConstants.IDISPLAYLENGTH, null);
      Throwable e = Assertions.assertThrows(TechpubsException.class, () -> unusedEndpointsController
          .getBookcaseContent(new MultivaluedHashMap(parameterMap), request));
      assertTrue(
          ((TechpubsException) e).getTechpubsAppError().equals(TechpubsAppError.INVALID_PARAMETER));

    }

    @Test
    public void whenQueryParameterIsNotAnIntegerShouldThrowTechpubsExceptionWithInvalidParameterException() {
      Map<String, String> parameterMap = new HashedMap(queryParams);
      parameterMap.put(AppConstants.IDISPLAYLENGTH, "abc");
      Throwable e = Assertions.assertThrows(TechpubsException.class, () -> unusedEndpointsController
          .getBookcaseContent(new MultivaluedHashMap(parameterMap), request));
      assertTrue(
          ((TechpubsException) e).getTechpubsAppError().equals(TechpubsAppError.INVALID_PARAMETER));

    }

    @Test
    public void whenQueryParameterIsEmptyStringShouldThrowTechpubsExceptionWithInvalidParameterException() {
      Map<String, String> parameterMap = new HashedMap(queryParams);
      parameterMap.put(AppConstants.IDISPLAYLENGTH, "");
      Throwable e = Assertions.assertThrows(TechpubsException.class, () -> unusedEndpointsController
          .getBookcaseContent(new MultivaluedHashMap(parameterMap), request));
      assertTrue(
          ((TechpubsException) e).getTechpubsAppError().equals(TechpubsAppError.INVALID_PARAMETER));

    }

    @Test
    public void whenQueryParameterIsLessThanTheMinValueForThatParameterShouldThrowTechpubsExceptionWithInvalidParameterException() {
      Map<String, String> parameterMap = new HashedMap(queryParams);
      parameterMap.put(AppConstants.IDISPLAYLENGTH, "0");
      Throwable e = Assertions.assertThrows(TechpubsException.class, () -> unusedEndpointsController
          .getBookcaseContent(new MultivaluedHashMap(parameterMap), request));
      assertTrue(
          ((TechpubsException) e).getTechpubsAppError().equals(TechpubsAppError.INVALID_PARAMETER));

    }

    @Test
    public void whenGetBookcaseItemModelReturnsBookListShouldReturnSuccess()
        throws TechpubsException {
      when(bookcaseContentModelApp
          .getBookcaseItemModel(isA(String.class), isA(String.class), isA(Map.class),
              isA(Map.class))).thenReturn(
          bookcaseContentModel);
      assertEquals(
          unusedEndpointsController.getBookcaseContent(new MultivaluedHashMap(queryParams), request)
              .getStatus(), 200);
    }


    @Test
    public void whenGetBooksReturnsBookcaseTOCModelShouldReturnTheBookList()
        throws TechpubsException {
      when(bookcaseContentModelApp
          .getBookcaseItemModel(isA(String.class), isA(String.class), isA(Map.class),
              isA(Map.class))).thenReturn(
          bookcaseContentModel);
      Response response = unusedEndpointsController
          .getBookcaseContent(new MultivaluedHashMap(queryParams), request);
      assertEquals(response.getEntity(), bookcaseContentModel);
    }
  }

  public static class GetOnlineBookcaseVersions {

    @Mock
    EntityManager em;
    @Mock
    TypedQuery query;
    @Mock
    TypedQuery paramQuery;
    @InjectMocks
    BookcaseDataImpl bookcaseDataImpl;
    private List<BookcaseBookcaseVersionDto> bookcaseInfoList;
    private BookcaseBookcaseVersionDto info1;
    private BookcaseBookcaseVersionDto info2;

    @Before
    public void setup() {
      MockitoAnnotations.initMocks(this);

      bookcaseInfoList = new LinkedList<>();
      info1 = new BookcaseBookcaseVersionDto();
      info1.setBookcaseKey("key1");
      bookcaseInfoList.add(info1);
      info2 = new BookcaseBookcaseVersionDto();
      info2.setBookcaseKey("key2");
      bookcaseInfoList.add(info2);

      when(em.createNamedQuery(anyString(), any())).thenReturn(query);
      when(query.setParameter("status", AppConstants.ONLINE)).thenReturn(paramQuery);
      when(paramQuery.getResultList()).thenReturn(bookcaseInfoList);
    }

    @Test
    public void successfulGetOnlineBookcases() {
      // just check that bookcaseData calls the correct IBookcaseData method
      List<BookcaseBookcaseVersionDto> output = bookcaseDataImpl.getBookcasesOnlineVersions();

      assertEquals(bookcaseInfoList.size(), output.size());
      assertTrue((output.contains(info1)));
      assertTrue((output.contains(info2)));
    }
  }
}