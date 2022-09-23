package com.geaviation.techpubs.controllers.impl;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.exceptions.TechpubsException.TechpubsAppError;
import com.geaviation.techpubs.models.techlib.dto.AirframeDto;
import com.geaviation.techpubs.models.techlib.dto.AvSystemExcelDownloadDTO;
import com.geaviation.techpubs.services.api.IAvSystemDocumentApp;
import com.geaviation.techpubs.services.api.ISearchAppCaller;
import com.geaviation.techpubs.services.impl.SalesforceSvcImpl;
import com.geaviation.techpubs.services.impl.UserService;

public class AvSystemDocumentControllerImplTest {
	
	@Mock
	private HttpServletRequest request;
	
	@Mock
	private IAvSystemDocumentApp avSystemDocumentApp;
	
	@Mock
	private ISearchAppCaller searchAppCaller;
	
	@Mock
	private UserService userService;

	@Mock
	private SalesforceSvcImpl salesforceSvc;
	
	@InjectMocks
	private AvSystemDocumentControllerImpl avSysDocController;
	
	private static final String SM_SSOID = "sm_ssoid";
	public static final String SSOID = "sso-id";
	private static final String PORTAL_ID = "portal_id";
	public static final String PORTAL = "CWC";
	private static final String docType = "docType";
	private static final String docSite = "docSite";
	private static final String docNumber = "docNumber";
	private static final String fileName = "fileName";
	
	private AvSystemExcelDownloadDTO avSystemExcelDownloadDTO;
	
	@Before
	public void setUp(){
	MockitoAnnotations.initMocks(this);
	when(request.getHeader(SM_SSOID)).thenReturn(SSOID);
    	when(request.getHeader(PORTAL_ID)).thenReturn(PORTAL);
	}
	
	@Test
    	public void getAvSystemExcelDocumentReturns200() throws TechpubsException {

		ReflectionTestUtils.setField(avSysDocController, "sqlInjection", true);
		ReflectionTestUtils.setField(avSysDocController, "searchEndpoint", true);

		String jsonString = "{\"took\":2,\"timed_out\":false,\"_shards\":{\"total\":1,\"successful\":1,"
				+ "\"skipped\":0,\"failed\":0},\"hits\":{\"total\":{\"value\":1877,\"relation\":\"eq\"},"
				+ "\"max_score\":null,\"hits\":[{\"_index\":\"av-cp-techpubs-avsystem_v1.3\",\"_type\":\"_doc\","
				+ "\"_id\":\"4iTUpYIB39TmZCRwNTdK\",\"_score\":null,\"_source\":{\"document_title\":\"test\","
				+ "\"airframes\":[\"F100\",\"A340\"],\"site\":\"Avionics & Power Systems Cheltenham\","
				+ "\"document_number\":\"test\",\"deleted\":true,\"revision_number\":\"4\","
				+ "\"part_numbers\":[\"987789987\"],\"revision_date\":\"2022-08-16\",\"index_date\":\"2022-08-16\","
				+ "\"publish_date\":\"2022-08-16\",\"document_type\":\"Component Maintenance Manual\"},"
				+ "\"fields\":{\"revision_date\":[\"2022-08-16\"]},\"sort\":[1660608000000]}]}}";
		String payload = "{\"index\":\"av-cp-techpubs-avsystem\",\"body\":{\"from\":0,\"size\":1867,\"sort\":[{\"revision_date\":{\"order\":\"desc\"}}],\"_source\":{\"exclude\":[\"content\"]},\"docvalue_fields\":[{\"field\":\"revision_date\",\"format\":\"date\"}],\"query\":{\"bool\":{\"must\":[],\"filter\":[],\"should\":[],\"must_not\":[]}}}}";
		
		when(searchAppCaller.callSearchEndpoint(SSOID, PORTAL_ID, payload)).thenReturn(jsonString);
		ResponseEntity response = avSysDocController.search(SSOID, PORTAL_ID, payload);

	    	assertEquals(response.getStatusCodeValue(), 200);
        
    	}
	
    	@Test
    	public void getAvSystemExcelDocumentReturns404() throws TechpubsException {

		ReflectionTestUtils.setField(avSysDocController, "sqlInjection", true);
		ReflectionTestUtils.setField(avSysDocController, "searchEndpoint", false);

		String jsonString = "{\"took\":2,\"timed_out\":false,\"_shards\":{\"total\":1,\"successful\":1,"
				+ "\"skipped\":0,\"failed\":0},\"hits\":{\"total\":{\"value\":1877,\"relation\":\"eq\"},"
				+ "\"max_score\":null,\"hits\":[{\"_index\":\"av-cp-techpubs-avsystem_v1.3\",\"_type\":\"_doc\","
				+ "\"_id\":\"4iTUpYIB39TmZCRwNTdK\",\"_score\":null,\"_source\":{\"document_title\":\"test\","
				+ "\"airframes\":[\"F100\",\"A340\"],\"site\":\"Avionics & Power Systems Cheltenham\","
				+ "\"document_number\":\"test\",\"deleted\":true,\"revision_number\":\"4\","
				+ "\"part_numbers\":[\"987789987\"],\"revision_date\":\"2022-08-16\",\"index_date\":\"2022-08-16\","
				+ "\"publish_date\":\"2022-08-16\",\"document_type\":\"Component Maintenance Manual\"},"
				+ "\"fields\":{\"revision_date\":[\"2022-08-16\"]},\"sort\":[1660608000000]}]}}";
		String payload = "{\"index\":\"av-cp-techpubs-avsystem\",\"body\":{\"from\":0,\"size\":1867,\"sort\":[{\"revision_date\":{\"order\":\"desc\"}}],\"_source\":{\"exclude\":[\"content\"]},\"docvalue_fields\":[{\"field\":\"revision_date\",\"format\":\"date\"}],\"query\":{\"bool\":{\"must\":[],\"filter\":[],\"should\":[],\"must_not\":[]}}}}";
		
		when(searchAppCaller.callSearchEndpoint(SSOID, PORTAL_ID, payload)).thenReturn(jsonString);
		ResponseEntity response = avSysDocController.search(SSOID, PORTAL_ID, payload);

	    	assertEquals(response.getStatusCodeValue(), 404);
        
    	}
    	
    	@Test
	public void getEntitledAirframesReturns200() throws TechpubsException {
            
            ReflectionTestUtils.setField(avSysDocController, "sqlInjection", true);
            ReflectionTestUtils.setField(avSysDocController, "salesforceEndpointsActive", true);
            
            List<AirframeDto> airframeDtoList = new ArrayList <AirframeDto>();
            String icaoCode = "GEAE";
            when (userService.getIcaoCode(SSOID)).thenReturn(icaoCode);
            when(salesforceSvc.getEntitledAirframes(icaoCode)).thenReturn(airframeDtoList);
            ResponseEntity response = avSysDocController.getEntitledAirframes(SSOID, PORTAL_ID, request);
            
            assertEquals(response.getStatusCodeValue(), 200);
            
        }
        
        @Test
        public void getEntitledAirframesReturns404() throws TechpubsException {
            
            ReflectionTestUtils.setField(avSysDocController, "sqlInjection", true);
            ReflectionTestUtils.setField(avSysDocController, "salesforceEndpointsActive", false);
            
            List<AirframeDto> airframeDtoList = new ArrayList <AirframeDto>();
            String icaoCode = "GEAE";
            when (userService.getIcaoCode(SSOID)).thenReturn(icaoCode);
            when(salesforceSvc.getEntitledAirframes(icaoCode)).thenReturn(airframeDtoList);
            ResponseEntity response = avSysDocController.getEntitledAirframes(SSOID, PORTAL_ID, request);
            
            assertEquals(response.getStatusCodeValue(), 404);
        }
        
        @Test
        public void getPdfFileForDocumentReturns200() throws TechpubsException, FileNotFoundException {
            ReflectionTestUtils.setField(avSysDocController, "sqlInjection", true);
            ReflectionTestUtils.setField(avSysDocController, "avsystemDocumentEndpointsActive", true);
            Path path = Paths.get("src/test/resources/test.txt");
            InputStream is = new FileInputStream(path.toFile());
            when (avSystemDocumentApp.getDocumentFromS3(SSOID, PORTAL_ID,"docType", "docSite", "docNumber", "fileName")).thenReturn(new InputStreamResource(is));
            ResponseEntity response = avSysDocController.getPdfFileForDocument(SSOID, PORTAL_ID, docType, docSite, docNumber, fileName, request);
            assertEquals(response.getStatusCodeValue(), 200);
            
        }
        
        @Test
        public void getPdfFileForDocumentReturns404() throws TechpubsException {
            ReflectionTestUtils.setField(avSysDocController, "sqlInjection", true);
            ReflectionTestUtils.setField(avSysDocController, "avsystemDocumentEndpointsActive", false);
            when (avSystemDocumentApp.getDocumentFromS3(SSOID, PORTAL_ID,"docType", "docSite", "docNumber", "fileName")).thenReturn(null);
            ResponseEntity response = avSysDocController.getPdfFileForDocument(SSOID, PORTAL_ID, docType, docSite, docNumber, fileName, request);
            assertEquals(response.getStatusCodeValue(), 404);
            
        }
        
        @Test
        public void getPdfFileForDocumentReturns400() throws TechpubsException {
            ReflectionTestUtils.setField(avSysDocController, "sqlInjection", true);
            ReflectionTestUtils.setField(avSysDocController, "avsystemDocumentEndpointsActive", true);
            when (avSystemDocumentApp.getDocumentFromS3(SSOID, PORTAL_ID, "docType", "docSite", "docNumber", "fileName")).thenThrow(new TechpubsException(TechpubsAppError.INVALID_PARAMETER));
            ResponseEntity response = avSysDocController.getPdfFileForDocument(SSOID, PORTAL_ID, docType, docSite, docNumber, fileName, request);
            assertEquals(response.getStatusCodeValue(), 400);
        }
        
        @Test
        public void getPdfFileForDocumentReturns401() throws TechpubsException {
            ReflectionTestUtils.setField(avSysDocController, "sqlInjection", true);
            ReflectionTestUtils.setField(avSysDocController, "avsystemDocumentEndpointsActive", true);
            when (avSystemDocumentApp.getDocumentFromS3(SSOID, PORTAL_ID, "docType", "docSite", "docNumber", "fileName")).thenThrow(new TechpubsException(TechpubsAppError.NOT_AUTHORIZED));
            ResponseEntity response = avSysDocController.getPdfFileForDocument(SSOID, PORTAL_ID, docType, docSite, docNumber, fileName, request);
            assertEquals(response.getStatusCodeValue(), 401);
        }
        
        @Test
        public void getPdfFileForDocumentReturns500() throws TechpubsException {
            ReflectionTestUtils.setField(avSysDocController, "sqlInjection", true);
            ReflectionTestUtils.setField(avSysDocController, "avsystemDocumentEndpointsActive", true);
            when (avSystemDocumentApp.getDocumentFromS3(SSOID, PORTAL_ID,"docType", "docSite", "docNumber", "fileName")).thenThrow(new TechpubsException(TechpubsAppError.INTERNAL_ERROR));
            ResponseEntity response = avSysDocController.getPdfFileForDocument(SSOID, PORTAL_ID, docType, docSite, docNumber, fileName, request);
            assertEquals(response.getStatusCodeValue(), 500);
         
        }
	 
}
