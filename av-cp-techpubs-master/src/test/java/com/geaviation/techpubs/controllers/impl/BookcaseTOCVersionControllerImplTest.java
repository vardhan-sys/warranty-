package com.geaviation.techpubs.controllers.impl;

import com.amazonaws.services.dynamodbv2.xspec.B;
import com.geaviation.techpubs.data.api.techlib.IPermissionData;
import com.geaviation.techpubs.data.impl.BookcaseDataImpl;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.exceptions.TechpubsException.TechpubsAppError;
import com.geaviation.techpubs.models.BookcaseTocModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.models.reviewer.PublishPageblkRequest;
import com.geaviation.techpubs.models.techlib.dto.BookcaseVersionStatusDTO;
import com.geaviation.techpubs.models.techlib.dto.UserRolePolicyAttributesDto;
import com.geaviation.techpubs.services.api.IProgramApp;
import com.geaviation.techpubs.services.impl.BookcaseTOCApp;
import com.geaviation.techpubs.services.impl.admin.AuthServiceImpl;
import com.geaviation.techpubs.services.impl.admin.AuthorizationAppImpl;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

//@RunWith(Enclosed.class)
public class BookcaseTOCVersionControllerImplTest {

    private static final String PORTAL = "CWC";
    private static final String SSOID = "212601653";
    private static final String BOOKCASEKEY = "gek112060";
    private static final String BOOKKEY = "gek112064";
    private static final String SM_SSOID = "sm_ssoid";
    private static final String PORTAL_ID = "portal_id";
    private static final String VERSION = "9.8";
    private static final String BOOK_KEY = "bookKey1";
    private static final String TYPE = "type";
    private List<BookcaseVersionStatusDTO> bookcaseVersionDTOS = Lists.newArrayList(new BookcaseVersionStatusDTO());
    private BookcaseTocModel bookcaseTOCModel = new BookcaseTocModel(new ArrayList<>());


    @Mock
    private PublishPageblkRequest publishPageblkRequest;

    @Mock
    private AuthServiceImpl authServiceMock;

    @Mock
    private IPermissionData mockIPermissionData;

    @Mock
    private ResponseEntity responseEntity;

    @Mock
    private IProgramApp iProgramAppMock;

    @Mock
    private BookcaseTOCApp bookcaseTocAppMock;

    @Mock
    private HttpServletRequest request;

    @Mock
    private BookcaseDataImpl bookcaseDataImpl;

    @Mock
    private AuthorizationAppImpl authorizationApp;

    @InjectMocks
    private BookcaseTOCVersionControllerImpl bookcaseTOCControllerImpl;

    @Before
    public void setup() throws TechpubsException {
        // Instantiate class we're testing and inject mocks
        this.bookcaseTOCControllerImpl = new BookcaseTOCVersionControllerImpl();
        MockitoAnnotations.initMocks(this);
        when(request.getHeader(SM_SSOID)).thenReturn(SSOID);
        when(request.getHeader(PORTAL_ID)).thenReturn(PORTAL);
        when(bookcaseTocAppMock.getBookcaseVersionStatuses(isA(String.class))).thenReturn(bookcaseVersionDTOS);
        when(bookcaseTocAppMock.getBookcaseTOC(isA(String.class), isA(String.class), isA(Boolean.class), isA(String.class))).thenReturn(bookcaseTOCModel);
        when(iProgramAppMock.getAuthorizedPrograms(isA(String.class), isA(String.class), isA(SubSystem.class))).thenReturn(new ArrayList<>(Arrays.asList(BOOKCASEKEY)));

        UserRolePolicyAttributesDto dto = new UserRolePolicyAttributesDto();
        dto.setRole("Reviewer");
        when(authorizationApp.getUserRolePolicyAndAttributes(isA(String.class), isA(String.class))).thenReturn(Lists.newArrayList(dto));
    }

    @Test
    public void whenSSOIDIsNullShouldThrowTechpubsException() {
        Throwable e = Assertions.assertThrows(TechpubsException.class, () -> bookcaseTOCControllerImpl.getBookcaseVersions(null, PORTAL, BOOKCASEKEY));
        assertTrue(((TechpubsException) e).getTechpubsAppError().equals(TechpubsAppError.NOT_AUTHORIZED));
    }

    @Test
    public void whenPortalIdIsNullShouldThrowTechpubsException() {
        when(request.getHeader(PORTAL_ID)).thenReturn(null);
        Throwable e = Assertions.assertThrows(TechpubsException.class, () -> bookcaseTOCControllerImpl.getBookcaseVersions(SSOID, null, BOOKCASEKEY));
        assertTrue(((TechpubsException) e).getTechpubsAppError().equals(TechpubsAppError.NOT_AUTHORIZED));
    }

    @Test
    public void whenProgramIsNotAuthorizedShouldThrowTechpubsException() throws TechpubsException {
        when(iProgramAppMock.getAuthorizedPrograms(isA(String.class), isA(String.class), isA(SubSystem.class))).thenReturn(new ArrayList<>());
        Throwable e = Assertions.assertThrows(TechpubsException.class, () -> bookcaseTOCControllerImpl.getBookcaseVersions(SSOID, PORTAL, BOOKCASEKEY));
        assertTrue(((TechpubsException) e).getTechpubsAppError().equals(TechpubsAppError.NOT_AUTHORIZED));
    }

    @Test
    public void whenGetBookcaseVersionsReturnsBookcaseTOCModelShouldReturnSuccess() throws TechpubsException {
        assertEquals(bookcaseTOCControllerImpl.getBookcaseVersions(SSOID, PORTAL, BOOKCASEKEY).getStatusCodeValue(), 200);
    }

    @Test
    public void whenGetBookcaseVersionsReturnsBookcaseTOCModelShouldReturnTheBookcaseTOCModel() throws TechpubsException {
        assertEquals(bookcaseTOCControllerImpl.getBookcaseVersions(SSOID, PORTAL, BOOKCASEKEY).getBody(), bookcaseVersionDTOS);
    }

    @Test
    public void whenGetBookcaseTopVersionTOCReturnsBookcaseTOCModelShouldReturnSuccess()
            throws TechpubsException {
        assertEquals(200,
                bookcaseTOCControllerImpl.getBookcaseTopVersionTOC(SSOID, PORTAL, BOOKCASEKEY, VERSION, request)
                        .getStatusCodeValue());
    }

    @Test
    public void whenGetBookcaseTopVersionTOCReturnsBookcaseTOCModelShouldReturnTheBookcaseTOCModel()
            throws TechpubsException {
        assertEquals(bookcaseTOCModel,
                bookcaseTOCControllerImpl.getBookcaseTopVersionTOC(SSOID, PORTAL, BOOKCASEKEY, VERSION, request)
                        .getBody());
    }

    @Test
    public void whenGetBookVersionTOCGetBookVersionTOCReturnsBookcaseTOCModelShouldReturnSuccess()
            throws TechpubsException {
        assertEquals(200, bookcaseTOCControllerImpl
                .getBookVersionTOC(SSOID, PORTAL, BOOKCASEKEY, BOOK_KEY, VERSION, request).getStatusCodeValue());
    }

    @Test
    public void whenGetBookVersionTOCReturnsBookcaseTOCModelShouldReturnTheBookcaseTOCModel()
            throws TechpubsException {
        assertEquals(bookcaseTOCModel, bookcaseTOCControllerImpl
                .getBookVersionTOC(SSOID, PORTAL, BOOKCASEKEY, VERSION, BOOK_KEY, request).getBody());
    }

    @Test
    public void whenSSOIDIsNullGetBookcaseTopTOCShouldThrowTechpubsException() {
        when(request.getHeader(SM_SSOID)).thenReturn(null);
        Throwable e = Assertions.assertThrows(TechpubsException.class, () -> bookcaseTOCControllerImpl.getBookcaseTopTOC(null, PORTAL, BOOKCASEKEY));
        assertTrue(((TechpubsException) e).getTechpubsAppError().equals(TechpubsAppError.NOT_AUTHORIZED));
    }

    @Test
    public void whenPortalIdIsNullGetBookcaseTopTOCShouldThrowTechpubsException() {
        when(request.getHeader(PORTAL_ID)).thenReturn(null);
        Throwable e = Assertions.assertThrows(TechpubsException.class, () -> bookcaseTOCControllerImpl.getBookcaseTopTOC(SSOID, null, BOOKCASEKEY));
        assertTrue(((TechpubsException) e).getTechpubsAppError().equals(TechpubsAppError.NOT_AUTHORIZED));
    }

    @Test
    public void whenProgramIsNotAuthorizedGetBookcaseTopTOCShouldThrowTechpubsException() throws TechpubsException {
        when(iProgramAppMock.getAuthorizedPrograms(isA(String.class), isA(String.class), isA(SubSystem.class))).thenReturn(new ArrayList<>());
        Throwable e = Assertions.assertThrows(TechpubsException.class, () -> bookcaseTOCControllerImpl.getBookcaseTopTOC(SSOID, PORTAL, BOOKCASEKEY));
        assertTrue(((TechpubsException) e).getTechpubsAppError().equals(TechpubsAppError.NOT_AUTHORIZED));
    }

    @Test
    public void whenGetBookcaseTOCReturnsBookcaseTOCModelShouldReturnSuccess() throws TechpubsException {
        assertEquals(bookcaseTOCControllerImpl.getBookcaseTopTOC(SSOID, PORTAL, BOOKCASEKEY).getStatusCodeValue(), 200);
    }


//    @Test
//    public void whenGetBookcaseTOCReturnsBookcaseTOCModelShouldReturnTheBookcaseTOCModel()
//            throws TechpubsException {
//        assertEquals(bookcaseTOCControllerImpl.getBookcaseTopTOC(SSOID, PORTAL, BOOKCASEKEY).getBody(),
//                bookcaseTOCModel);
//    }
//
//    @Test
//    public void whenSSOIDIsNullGetBookcaseTopVersionTOCShouldThrowTechpubsException() throws TechpubsException {
//        authServiceMock.checkResourceAccess(null, "review-overlay", request);
//        //when(request.getHeader(SM_SSOID)).thenReturn(null);
//        Throwable e = Assertions.assertThrows(TechpubsException.class, () -> bookcaseTOCControllerImpl
//                .getBookcaseTopVersionTOC(null, PORTAL, BOOKCASEKEY, VERSION, request));
//        assertTrue(
//                ((TechpubsException) e).getTechpubsAppError().equals(TechpubsAppError.NOT_AUTHORIZED));
//    }
//
//    @Test
//    public void whenSSOIDIsEmptyShouldThrowTechpubsException() {
//      when(request.getHeader(SM_SSOID)).thenReturn("");
//      Throwable e = Assertions.assertThrows(TechpubsException.class, () -> bookcaseTOCControllerImpl
//          .getBookcaseTopVersionTOC(BOOKCASEKEY, VERSION, request));
//      assertTrue(
//          ((TechpubsException) e).getTechpubsAppError().equals(TechpubsAppError.NOT_AUTHORIZED));
//    }
//
//    @Test
//    public void whenPortalIdIsNullShouldThrowTechpubsException() {
//      when(request.getHeader(PORTAL_ID)).thenReturn(null);
//      Throwable e = Assertions.assertThrows(TechpubsException.class, () -> bookcaseTOCControllerImpl
//          .getBookcaseTopVersionTOC(BOOKCASEKEY, VERSION, request));
//      assertTrue(
//          ((TechpubsException) e).getTechpubsAppError().equals(TechpubsAppError.NOT_AUTHORIZED));
//    }
//
//    @Test
//    public void whenPortalIdEmptyShouldThrowTechpubsException() {
//      when(request.getHeader(PORTAL_ID)).thenReturn("");
//      Throwable e = Assertions.assertThrows(TechpubsException.class, () -> bookcaseTOCControllerImpl
//          .getBookcaseTopVersionTOC(BOOKCASEKEY, VERSION, request));
//      assertTrue(
//          ((TechpubsException) e).getTechpubsAppError().equals(TechpubsAppError.NOT_AUTHORIZED));
//    }
//
//    @Test
//    public void whenProgramIsNotAuthorizedGetBookcaseTopVersionTOCShouldThrowTechpubsException() throws TechpubsException {
//      when(iProgramAppMock
//          .getAuthorizedPrograms(isA(String.class), isA(String.class), isA(SubSystem.class)))
//          .thenReturn(new ArrayList<>());
//      Throwable e = Assertions.assertThrows(TechpubsException.class, () -> bookcaseTOCControllerImpl
//          .getBookcaseTopVersionTOC(SSOID,PORTAL, BOOKCASEKEY, VERSION, request));
//      assertTrue(
//          ((TechpubsException) e).getTechpubsAppError().equals(TechpubsAppError.NOT_AUTHORIZED));
//    }
//
//    @Test
//    public void whenSSOIDIsNullGetBookVersionTOCShouldThrowTechpubsException() throws TechpubsException {
//      when(request.getHeader(SM_SSOID)).thenReturn("");
//      Throwable e = Assertions.assertThrows(TechpubsException.class, () -> bookcaseTOCControllerImpl
//          .getBookVersionTOC(BOOKCASEKEY, BOOK_KEY, VERSION, request));
//      assertTrue(
//          ((TechpubsException) e).getTechpubsAppError().equals(TechpubsAppError.NOT_AUTHORIZED));
//    }
//        }
//
//    @Test
//    public void whenSSOIDIsEmptyShouldThrowTechpubsException() {
//      when(request.getHeader(SM_SSOID)).thenReturn("");
//      Throwable e = Assertions.assertThrows(TechpubsException.class, () -> bookcaseTOCControllerImpl
//          .getBookVersionTOC(BOOKCASEKEY, BOOK_KEY, VERSION, request));
//      assertTrue(
//          ((TechpubsException) e).getTechpubsAppError().equals(TechpubsAppError.NOT_AUTHORIZED));
//    }
//
//    @Test
//    public void whenPortalIdIsNullShouldThrowTechpubsException() {
//      when(request.getHeader(PORTAL_ID)).thenReturn(null);
//      Throwable e = Assertions.assertThrows(TechpubsException.class, () -> bookcaseTOCControllerImpl
//          .getBookVersionTOC(BOOKCASEKEY, BOOK_KEY, VERSION, request));
//      assertTrue(
//          ((TechpubsException) e).getTechpubsAppError().equals(TechpubsAppError.NOT_AUTHORIZED));
//    }
//
//    @Test
//    public void whenPortalIdEmptyShouldThrowTechpubsException() {
//      when(request.getHeader(PORTAL_ID)).thenReturn("");
//      Throwable e = Assertions.assertThrows(TechpubsException.class, () -> bookcaseTOCControllerImpl
//          .getBookVersionTOC(BOOKCASEKEY, BOOK_KEY, VERSION, request));
//      assertTrue(
//          ((TechpubsException) e).getTechpubsAppError().equals(TechpubsAppError.NOT_AUTHORIZED));
//
//    }
//
//    @Test
//    public void whenProgramIsNotAuthorizedShouldThrowTechpubsException() throws TechpubsException {
//      when(iProgramAppMock
//          .getAuthorizedPrograms(isA(String.class), isA(String.class), isA(SubSystem.class)))
//          .thenReturn(new ArrayList<>());
//      Throwable e = Assertions.assertThrows(TechpubsException.class, () -> bookcaseTOCControllerImpl
//          .getBookVersionTOC(BOOKCASEKEY, BOOK_KEY, VERSION, request));
//      assertTrue(
//          ((TechpubsException) e).getTechpubsAppError().equals(TechpubsAppError.NOT_AUTHORIZED));
//    }
  }

