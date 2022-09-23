package com.geaviation.techpubs.controllers.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.geaviation.techpubs.controllers.impl.admin.AvSystemControllerImpl;
import com.geaviation.techpubs.controllers.requests.EnableStatusBody;
import com.geaviation.techpubs.data.api.techlib.ISalesforceCompanyData;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.techlib.SystemDocumentSiteLookupEntity;
import com.geaviation.techpubs.models.techlib.SystemDocumentTypeLookupEntity;
import com.geaviation.techpubs.models.techlib.dto.AirframeDto;
import com.geaviation.techpubs.models.techlib.dto.SalesforceCompanyDto;
import com.geaviation.techpubs.models.techlib.dto.SalesforceCompanyLookupDTO;
import com.geaviation.techpubs.models.techlib.dto.SystemDocumentByIdDTO;
import com.geaviation.techpubs.services.api.admin.IAvSystemUploaderApp;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.impl.SalesforceSvcImpl;
import com.geaviation.techpubs.services.impl.admin.AuthServiceImpl;
import com.geaviation.techpubs.models.techlib.SystemDocumentEntity;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

public class AvSystemControllerImplTest {

  @Mock
  private HttpServletRequest requestMock;

  @Mock
  private AuthServiceImpl authServiceImplMock;

  @Mock
  private SalesforceSvcImpl salesforceSvcMock;

  @Mock
  private IAvSystemUploaderApp iAvSystemUploaderMock;

  @Mock
  private ISalesforceCompanyData iSalesforceCompanyDataMock;

  @InjectMocks
  AvSystemControllerImpl avSystemControllerImplMock;

  private static final String SM_SSO_ID = "sm_sso-id";
  public static final String SSO_ID = "sso-id";
  private static final String PORTAL_ID = "portal_id";
  public static final String PORTAL = "CWC";
  private static final String ICAO_CODE = "icao_code";
  private List <String> companyIds;
  private EnableStatusBody enableStatusBody;


  @Before
  public void setUp(){
    MockitoAnnotations.initMocks(this);
    when(requestMock.getHeader(SM_SSO_ID)).thenReturn(SSO_ID);
    when(requestMock.getHeader(PORTAL_ID)).thenReturn(PORTAL);

    companyIds = new ArrayList<>();
    companyIds.add("one");
    companyIds.add("two");
    enableStatusBody = new EnableStatusBody();
    enableStatusBody.setEnabled(true);
    enableStatusBody.setCompanyIds(companyIds);
  }

  @Test
  public void getSalesForceCompaniesListReturns200() throws TechpubsException {
    doNothing().when(authServiceImplMock)
        .checkResourceAccessForProduct(isA(String.class), isA(String.class),isA(HttpServletRequest.class), isA(String.class));
    ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
    ReflectionTestUtils.setField(avSystemControllerImplMock, "salesforceEndpointsActive", true);
    List<String> airframes = new ArrayList<String>();
   	List<String> entitlementType = new ArrayList<String>();
    List<SalesforceCompanyDto> salesforceCompanyDtoList = new ArrayList<>();
    when(salesforceSvcMock.getSalesforceCompanies(airframes, entitlementType)).thenReturn(salesforceCompanyDtoList);
    ResponseEntity response = avSystemControllerImplMock.getSalesforceCompanies(SSO_ID,airframes, entitlementType, requestMock);

    assertEquals(response.getStatusCodeValue(), 200);
  }

  @Test
  public void getSalesForceCompaniesListReturnsUnauthorizedStatus() throws TechpubsException {
    doThrow(TechpubsException.class).when(authServiceImplMock)
        .checkResourceAccessForProduct(isNull(), isA(String.class),isA(HttpServletRequest.class), isA(String.class));
    ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
    ReflectionTestUtils.setField(avSystemControllerImplMock, "salesforceEndpointsActive", true);
    List<String> airframes = new ArrayList<String>();
   	List<String> entitlementType = new ArrayList<String>();
    List<SalesforceCompanyDto> salesforceCompanyDtoList = new ArrayList<>();
    when(salesforceSvcMock.getSalesforceCompanies(airframes, entitlementType)).thenReturn(salesforceCompanyDtoList);
    ResponseEntity response = avSystemControllerImplMock.getSalesforceCompanies(null, airframes, entitlementType, requestMock);

    assertEquals(response.getStatusCodeValue(), 401);
  }

  @Test
  public void getSalesForceCompaniesListNotFoundIfFeatureFlagIsFalse() throws TechpubsException {
    doNothing().when(authServiceImplMock)
        .checkResourceAccessForProduct(isA(String.class), isA(String.class),isA(HttpServletRequest.class), isA(String.class));
    ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
    ReflectionTestUtils.setField(avSystemControllerImplMock, "salesforceEndpointsActive", false);
    List<String> airframes = new ArrayList<String>();
   	List<String> entitlementType = new ArrayList<String>();
    List<SalesforceCompanyDto> salesforceCompanyDtoList = new ArrayList<>();
    when(salesforceSvcMock.getSalesforceCompanies(airframes, entitlementType)).thenReturn(salesforceCompanyDtoList);
    ResponseEntity response = avSystemControllerImplMock.getSalesforceCompanies(SSO_ID,airframes, entitlementType, requestMock);

    assertEquals(response.getStatusCodeValue(), 404);
  }

  @Test
  public void getSalesForceAirFramesListReturns200() throws TechpubsException {
    doNothing().when(authServiceImplMock)
        .checkResourceAccessForProduct(isA(String.class), isA(String.class),isA(HttpServletRequest.class), isA(String.class));
    ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
    ReflectionTestUtils.setField(avSystemControllerImplMock, "salesforceEndpointsActive", true);

    List<AirframeDto> airframeDtoList = new ArrayList<>();
    when(salesforceSvcMock.getSalesforceAirframes()).thenReturn(airframeDtoList);
    ResponseEntity response = avSystemControllerImplMock.getSalesforceAirframes(SSO_ID,PORTAL_ID, requestMock);

    assertEquals(response.getStatusCodeValue(), 200);
  }

  @Test
  public void getSalesForceAirFramesListReturnsUnauthorizedStatus() throws TechpubsException {
    doThrow(TechpubsException.class).when(authServiceImplMock)
        .checkResourceAccessForProduct(isNull(), isA(String.class),isA(HttpServletRequest.class), isA(String.class));
    ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
    ReflectionTestUtils.setField(avSystemControllerImplMock, "salesforceEndpointsActive", true);

    List<AirframeDto> airframeDtoList = new ArrayList<>();
    when(salesforceSvcMock.getSalesforceAirframes()).thenReturn(airframeDtoList);
    ResponseEntity response = avSystemControllerImplMock.getSalesforceAirframes(null, PORTAL_ID, requestMock);

    assertEquals(response.getStatusCodeValue(), 401);
  }

  @Test
  public void getSalesForceAirFramesListReturnsNotFoundIfFeatureFlagIsFalse() throws TechpubsException {
    doNothing().when(authServiceImplMock)
        .checkResourceAccessForProduct(isA(String.class), isA(String.class),isA(HttpServletRequest.class), isA(String.class));
    ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
    ReflectionTestUtils.setField(avSystemControllerImplMock, "salesforceEndpointsActive", false);

    List<AirframeDto> airframeDtoList = new ArrayList<>();
    when(salesforceSvcMock.getSalesforceAirframes()).thenReturn(airframeDtoList);
    ResponseEntity response = avSystemControllerImplMock.getSalesforceAirframes(SSO_ID, PORTAL_ID, requestMock);

    assertEquals(response.getStatusCodeValue(), 404);
  }

  @Test
  public void getAllSystemDocumentsTypeListReturns200() throws TechpubsException {
    doNothing().when(authServiceImplMock)
        .checkResourceAccessForProduct(isA(String.class), isA(String.class),isA(HttpServletRequest.class), isA(String.class));
    ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
    ReflectionTestUtils.setField(avSystemControllerImplMock, "getDocumentDataActive", true);

    List<SystemDocumentTypeLookupEntity> systemDocumentTypeLookupEntityList = new ArrayList<>();
    when(iAvSystemUploaderMock.getSystemDocumentType()).thenReturn(systemDocumentTypeLookupEntityList);
    ResponseEntity response = avSystemControllerImplMock.getAllSystemDocumentsType(SSO_ID, requestMock);

    assertEquals(response.getStatusCodeValue(), 200);
  }

  @Test
  public void getAllSystemDocumentsTypeListReturnsUnauthorizedStatus() throws TechpubsException {
    doThrow(TechpubsException.class).when(authServiceImplMock)
        .checkResourceAccessForProduct(isNull(), isA(String.class),isA(HttpServletRequest.class), isA(String.class));
    ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
    ReflectionTestUtils.setField(avSystemControllerImplMock, "getDocumentDataActive", true);

    List<SystemDocumentTypeLookupEntity> systemDocumentTypeLookupEntityList = new ArrayList<>();
    when(iAvSystemUploaderMock.getSystemDocumentType()).thenReturn(systemDocumentTypeLookupEntityList);
    ResponseEntity response = avSystemControllerImplMock.getAllSystemDocumentsType(null, requestMock);

    assertEquals(response.getStatusCodeValue(), 401);
  }

  @Test
  public void getAllSystemDocumentsTypeListReturnsNotFoundIfFeatureFlagIsFalse() throws TechpubsException {
    doNothing().when(authServiceImplMock)
        .checkResourceAccessForProduct(isA(String.class), isA(String.class),isA(HttpServletRequest.class), isA(String.class));
    ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
    ReflectionTestUtils.setField(avSystemControllerImplMock, "getDocumentDataActive", false);

    List<SystemDocumentTypeLookupEntity> systemDocumentTypeLookupEntityList = new ArrayList<>();
    when(iAvSystemUploaderMock.getSystemDocumentType()).thenReturn(systemDocumentTypeLookupEntityList);
    ResponseEntity response = avSystemControllerImplMock.getAllSystemDocumentsType(SSO_ID, requestMock);

    assertEquals(response.getStatusCodeValue(), 404);
  }

  @Test
  public void getSystemDocumentSiteListReturns200() throws TechpubsException {
    doNothing().when(authServiceImplMock)
        .checkResourceAccessForProduct(isA(String.class), isA(String.class),isA(HttpServletRequest.class), isA(String.class));
    ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
    ReflectionTestUtils.setField(avSystemControllerImplMock, "getDocumentDataActive", true);

    List<SystemDocumentSiteLookupEntity> systemDocumentSiteLookupEntityList = new ArrayList<>();
    when(iAvSystemUploaderMock.getSystemDocumentSite()).thenReturn(systemDocumentSiteLookupEntityList);
    ResponseEntity response = avSystemControllerImplMock.getSystemDocumentSite(SSO_ID, requestMock);

    assertEquals(response.getStatusCodeValue(), 200);
  }

  @Test
  public void getSystemDocumentSiteListReturnsUnauthorizedStatus() throws TechpubsException {
    doThrow(TechpubsException.class).when(authServiceImplMock)
        .checkResourceAccessForProduct(isNull(), isA(String.class),isA(HttpServletRequest.class), isA(String.class));
    ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
    ReflectionTestUtils.setField(avSystemControllerImplMock, "getDocumentDataActive", true);

    List<SystemDocumentSiteLookupEntity> systemDocumentSiteLookupEntityList = new ArrayList<>();
    when(iAvSystemUploaderMock.getSystemDocumentSite()).thenReturn(systemDocumentSiteLookupEntityList);
    ResponseEntity response = avSystemControllerImplMock.getSystemDocumentSite(null, requestMock);

    assertEquals(response.getStatusCodeValue(), 401);
  }

  @Test
  public void getSystemDocumentSiteListReturnsNotFoundIfFeatureFlagIsFalse() throws TechpubsException {
    doNothing().when(authServiceImplMock)
        .checkResourceAccessForProduct(isA(String.class), isA(String.class),isA(HttpServletRequest.class), isA(String.class));
    ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
    ReflectionTestUtils.setField(avSystemControllerImplMock, "getDocumentDataActive", false);

    List<SystemDocumentSiteLookupEntity> systemDocumentSiteLookupEntityList = new ArrayList<>();
    when(iAvSystemUploaderMock.getSystemDocumentSite()).thenReturn(systemDocumentSiteLookupEntityList);
    ResponseEntity response = avSystemControllerImplMock.getSystemDocumentSite(SSO_ID, requestMock);

    assertEquals(response.getStatusCodeValue(), 404);
  }

  @Test
  public void getSystemDocumentByIdReturns200() throws TechpubsException {
    SystemDocumentByIdDTO systemDocumentByIdDTO = new SystemDocumentByIdDTO();
    systemDocumentByIdDTO.setSystemDocumentId(SSO_ID);
    ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
    ReflectionTestUtils.setField(avSystemControllerImplMock, "getDocumentDataActive", true);

    when(iAvSystemUploaderMock.getSystemDocumentById(SSO_ID)).thenReturn(systemDocumentByIdDTO);
    ResponseEntity response = avSystemControllerImplMock.getSystemDocumentById(SSO_ID, SSO_ID, requestMock);

    assertEquals(response.getStatusCodeValue(), 200);
  }

  @Test
  public void getSystemDocumentByIdReturnsUnauthorizedStatus() throws TechpubsException {
    doThrow(TechpubsException.class).when(authServiceImplMock)
            .checkResourceAccessForProduct(isNull(), isA(String.class),isA(HttpServletRequest.class), isA(String.class));
    ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
    ReflectionTestUtils.setField(avSystemControllerImplMock, "getDocumentDataActive", true);

    when(iAvSystemUploaderMock.getSystemDocumentById(null)).thenThrow(TechpubsException.class);
    ResponseEntity response = avSystemControllerImplMock.getSystemDocumentById(null, null, requestMock);

    assertEquals(response.getStatusCodeValue(), 401);
  }

  @Test
  public void getSystemDocumentByIdReturnsNotFoundIfFeatureFlagIsFalse() throws TechpubsException {
    doThrow(TechpubsException.class).when(authServiceImplMock)
            .checkResourceAccessForProduct(isNull(), isA(String.class), isNull(), isA(String.class));
    ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
    ReflectionTestUtils.setField(avSystemControllerImplMock, "getDocumentDataActive", false);

    when(iAvSystemUploaderMock.getSystemDocumentById(null)).thenThrow(TechpubsException.class);
    ResponseEntity response = avSystemControllerImplMock.getSystemDocumentById(null, null, requestMock);

    assertEquals(response.getStatusCodeValue(), 404);
  }
  
  @Test
  public void getAvSystemExcelDocumentReturns200() throws TechpubsException, ExcelException {
    doNothing().when(authServiceImplMock)
	  .checkResourceAccessForProduct(isA(String.class), isA(String.class),isA(HttpServletRequest.class), isA(String.class));
    ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
    ReflectionTestUtils.setField(avSystemControllerImplMock, "downloadActive", true);
    ResponseEntity response = avSystemControllerImplMock.downloadUsers(SSO_ID, requestMock);

    assertEquals(response.getStatusCodeValue(), 200);

  }

  @Test
  public void getAvSystemExcelDocumentReturnsUnauthorizedStatus() throws TechpubsException, ExcelException {
    doThrow(TechpubsException.class).when(authServiceImplMock)
	 .checkResourceAccessForProduct(isNull(), isA(String.class),isA(HttpServletRequest.class), isA(String.class));
    ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
    ReflectionTestUtils.setField(avSystemControllerImplMock, "downloadActive", true);
    ResponseEntity response = avSystemControllerImplMock.downloadUsers(null, requestMock);

    assertEquals(response.getStatusCodeValue(), 401);
  }

  @Test
  public void getAvSystemExcelDocumentReturnsNotFoundIfFeatureFlagIsFalse() throws TechpubsException, ExcelException {
    doThrow(TechpubsException.class).when(authServiceImplMock)
	  .checkResourceAccessForProduct(isNull(), isA(String.class), isNull(), isA(String.class));
    ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
    ReflectionTestUtils.setField(avSystemControllerImplMock, "downloadActive", false);
    ResponseEntity response = avSystemControllerImplMock.downloadUsers(SSO_ID, requestMock);

    assertEquals(response.getStatusCodeValue(), 404);
  }
  
	@Test
	public void downloadSystemDocumentFile200() throws TechpubsException, ExcelException {
		doNothing().when(authServiceImplMock).checkResourceAccessForProduct(isA(String.class), isA(String.class),
				isA(HttpServletRequest.class), isA(String.class));
		SystemDocumentEntity systemDocument = new SystemDocumentEntity();
		systemDocument.setFileName("test");
		ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
		ReflectionTestUtils.setField(avSystemControllerImplMock, "downloadActive", true);
		when(iAvSystemUploaderMock.getSystemDocumentEntityById(isA(String.class))).thenReturn(systemDocument);
		ResponseEntity response = avSystemControllerImplMock.downloadSystemDocumentFile(SSO_ID, "id", requestMock);
		assertEquals(response.getStatusCodeValue(), 200);
	}

	@Test
	public void downloadSystemDocumentFileReturns404() throws TechpubsException {
		doNothing().when(authServiceImplMock).checkResourceAccessForProduct(isA(String.class), isA(String.class),
				isA(HttpServletRequest.class), isA(String.class));
		String id = "id";
		ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
		ResponseEntity response = avSystemControllerImplMock.downloadSystemDocumentFile(SSO_ID, id, requestMock);
		assertEquals(response.getStatusCodeValue(), 404);
	}

	@Test
	public void downloadSystemDocumentFileReturns401() throws TechpubsException, ExcelException {
		doThrow(TechpubsException.class).when(authServiceImplMock).checkResourceAccessForProduct(isNull(),
				isA(String.class), isA(HttpServletRequest.class), isA(String.class));
		ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
		ReflectionTestUtils.setField(avSystemControllerImplMock, "downloadActive", true);
		ResponseEntity response = avSystemControllerImplMock.downloadSystemDocumentFile(null, null, requestMock);
		assertEquals(response.getStatusCodeValue(), 401);
	}

	@Test
	public void downloadSystemDocumentFile500() throws TechpubsException, ExcelException {
		doNothing().when(authServiceImplMock).checkResourceAccessForProduct(isA(String.class), isA(String.class),
				isA(HttpServletRequest.class), isA(String.class));
		SystemDocumentEntity systemDocument = new SystemDocumentEntity();
		systemDocument.setFileName(null);
		ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
		ReflectionTestUtils.setField(avSystemControllerImplMock, "downloadActive", true);
		when(iAvSystemUploaderMock.getSystemDocumentEntityById(isA(String.class))).thenThrow(TechpubsException.class);
		ResponseEntity response = avSystemControllerImplMock.downloadSystemDocumentFile(SSO_ID, "id", requestMock);
		assertEquals(response.getStatusCodeValue(), 500);
	}

  @Test
  public void toggleSalesforceEnableStatusReturns204() throws TechpubsException {
    doNothing().when(authServiceImplMock).checkResourceAccessForProduct(isA(String.class), isA(String.class),
            isA(HttpServletRequest.class), isA(String.class));
    SystemDocumentEntity systemDocument = new SystemDocumentEntity();
    ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
    ReflectionTestUtils.setField(avSystemControllerImplMock, "salesforceEndpointsActive", true);
    when(iAvSystemUploaderMock.getSystemDocumentEntityById(isA(String.class))).thenReturn(systemDocument);
    doNothing().when(salesforceSvcMock).updateEnableStatus(enableStatusBody);
    ResponseEntity response = avSystemControllerImplMock.toggleSalesforceEnableStatus(SSO_ID, enableStatusBody, requestMock);
    assertEquals(204, response.getStatusCodeValue());
  }

  @Test
  public void toggleSalesforceEnableStatusReturnsUnauthorizedStatus() throws TechpubsException {
    doThrow(TechpubsException.class).when(authServiceImplMock).checkResourceAccessForProduct(isNull(),
            isA(String.class), isA(HttpServletRequest.class), isA(String.class));
    SystemDocumentEntity systemDocument = new SystemDocumentEntity();
    ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
    ReflectionTestUtils.setField(avSystemControllerImplMock, "salesforceEndpointsActive", true);
    when(iAvSystemUploaderMock.getSystemDocumentEntityById(isA(String.class))).thenReturn(systemDocument);
    doNothing().when(salesforceSvcMock).updateEnableStatus(enableStatusBody);
    ResponseEntity response = avSystemControllerImplMock.toggleSalesforceEnableStatus(null, enableStatusBody, requestMock);
    assertEquals(401, response.getStatusCodeValue());
  }

  @Test
  public void toggleSalesforceEnableStatusReturnsNotFoundIfFeatureFlagIsFalse() throws TechpubsException {
    doNothing().when(authServiceImplMock)
            .checkResourceAccessForProduct(isA(String.class), isA(String.class), isNull(), isA(String.class));
    ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
    ReflectionTestUtils.setField(avSystemControllerImplMock, "salesforceEndpointsActive", false);
    when(iAvSystemUploaderMock.getSystemDocumentById(null)).thenThrow(TechpubsException.class);
    ResponseEntity response = avSystemControllerImplMock.toggleSalesforceEnableStatus(SSO_ID, null, requestMock);
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  public void getSalesforceCompanyLookupReturns200() throws TechpubsException {
    List<SalesforceCompanyLookupDTO> createCompany = new ArrayList<>();
    doNothing().when(authServiceImplMock)
            .checkResourceAccessForProduct(isA(String.class), isA(String.class),isA(HttpServletRequest.class), isA(String.class));
    ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
    ReflectionTestUtils.setField(avSystemControllerImplMock, "salesforceEndpointsActive", true);
    when(iSalesforceCompanyDataMock.getSalesforceCompanyLookup(ICAO_CODE)).thenReturn(createCompany);
    ResponseEntity response = avSystemControllerImplMock.getSalesforceCompanyLookup(SSO_ID, ICAO_CODE, requestMock);
    assertEquals(response.getStatusCodeValue(), 200);
  }

  @Test
  public void getSalesforceCompanyLookupReturnsUnauthorizedStatus() throws TechpubsException, ExcelException {
    doThrow(TechpubsException.class).when(authServiceImplMock)
            .checkResourceAccessForProduct(isNull(), isA(String.class),isA(HttpServletRequest.class), isA(String.class));
    ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
    ReflectionTestUtils.setField(avSystemControllerImplMock, "salesforceEndpointsActive", true);
    ResponseEntity response = avSystemControllerImplMock.getSalesforceCompanyLookup(null, ICAO_CODE, requestMock);
    assertEquals(response.getStatusCodeValue(), 401);
  }

  @Test
  public void getSalesforceCompanyLookupReturnsNotFoundIfFeatureFlagIsFalse() throws TechpubsException, ExcelException {
    doThrow(TechpubsException.class).when(authServiceImplMock)
            .checkResourceAccessForProduct(isNull(), isA(String.class), isNull(), isA(String.class));
    ReflectionTestUtils.setField(avSystemControllerImplMock, "sqlInjection", true);
    ReflectionTestUtils.setField(avSystemControllerImplMock, "salesforceEndpointsActive", false);
    ResponseEntity response = avSystemControllerImplMock.getSalesforceCompanyLookup(null, null, requestMock);
    assertEquals(response.getStatusCodeValue(), 404);
  }
}
