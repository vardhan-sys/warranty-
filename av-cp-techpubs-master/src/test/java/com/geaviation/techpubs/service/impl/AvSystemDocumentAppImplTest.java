package com.geaviation.techpubs.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.geaviation.techpubs.data.api.techlib.ISystemDocumentData;
import com.geaviation.techpubs.data.factories.AmazonS3ClientFactory;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.exceptions.TechpubsException.TechpubsAppError;
import com.geaviation.techpubs.models.techlib.SystemDocumentEntity;
import com.geaviation.techpubs.services.api.ISearchAppCaller;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;
import com.geaviation.techpubs.services.impl.AvSystemDocumentAppImpl;
import com.geaviation.techpubs.services.impl.SalesforceSvcImpl;
import com.geaviation.techpubs.services.impl.UserService;

public class AvSystemDocumentAppImplTest {
	
	@Mock
	private ISearchAppCaller searchAppCaller;
	
	@InjectMocks
	private AvSystemDocumentAppImpl avSystemDocAppImpl;
	
	@Mock
	private ISystemDocumentData systemDocumentData;
	
	@Mock
	private UserService userService;

	@Mock
    	private SalesforceSvcImpl salesforceSvc;
	
	@Mock
	private AmazonS3ClientFactory amazonS3ClientFactory;
	
	@Mock
	AmazonS3Client amazonS3Client;
	
	@Mock
	S3Object s3Object;
	
	
	public static final String SSOID = "sso-id";
	private static final String PORTAL_ID = "portal_id";
	
	@Before
	public void setUp(){
	MockitoAnnotations.initMocks(this);

	}
	
	@Test
    	public void getAvSystemExcelDocumentReturnsWhenDocumentAvailable() throws TechpubsException, ExcelException {

		String payload = "{\"index\":\"av-cp-techpubs-avsystem\",\"body\":{\"from\":0,\"size\":1867,\"sort\":[{\"revision_date\":{\"order\":\"desc\"}}],\"_source\":{\"exclude\":[\"content\"]},\"docvalue_fields\":[{\"field\":\"revision_date\",\"format\":\"date\"}],\"query\":{\"bool\":{\"must\":[],\"filter\":[],\"should\":[],\"must_not\":[]}}}}";
		String jsonString = "{\"took\":2,\"timed_out\":false,\"_shards\":{\"total\":1,\"successful\":1,"
				+ "\"skipped\":0,\"failed\":0},\"hits\":{\"total\":{\"value\":1877,\"relation\":\"eq\"},"
				+ "\"max_score\":null,\"hits\":[{\"_index\":\"av-cp-techpubs-avsystem_v1.3\",\"_type\":\"_doc\","
				+ "\"_id\":\"4iTUpYIB39TmZCRwNTdK\",\"_score\":null,\"_source\":{\"document_title\":\"test\","
				+ "\"airframes\":[\"F100\",\"A340\"],\"site\":\"Avionics & Power Systems Cheltenham\","
				+ "\"document_number\":\"test\",\"deleted\":true,\"revision_number\":\"4\","
				+ "\"part_numbers\":[\"987789987\"],\"revision_date\":\"2022-08-16\",\"index_date\":\"2022-08-16\","
				+ "\"publish_date\":\"2022-08-16\",\"document_type\":\"Component Maintenance Manual\"},"
				+ "\"fields\":{\"revision_date\":[\"2022-08-16\"]},\"sort\":[1660608000000]}]}}";
		when(searchAppCaller.callSearchEndpoint(SSOID, PORTAL_ID, payload)).thenReturn(jsonString);
		FileWithBytes response = avSystemDocAppImpl.getAvSystemExcelDocument(SSOID, PORTAL_ID, payload);

	    	assertEquals(response.getFileName(), "Aviation_Systems.xlsx");
        
    }
	
    @Test
    public void getAvSystemExcelDocumentReturnsNoHits() throws TechpubsException, ExcelException {

		String jsonString = "{\"took\":2,\"timed_out\":false,\"_shards\":{\"total\":1,\"successful\":1,"
				+ "\"skipped\":0,\"failed\":0},\"hits1\":{\"total\":{\"value\":1877,\"relation\":\"eq\"},"
				+ "\"max_score\":null,\"hits1\":[{\"_index\":\"av-cp-techpubs-avsystem_v1.3\",\"_type\":\"_doc\","
				+ "\"_id\":\"4iTUpYIB39TmZCRwNTdK\",\"_score\":null,\"_source\":{\"document_title\":\"test\","
				+ "\"airframes\":[\"F100\",\"A340\"],\"site\":\"Avionics & Power Systems Cheltenham\","
				+ "\"document_number\":\"test\",\"deleted\":true,\"revision_number\":\"4\","
				+ "\"part_numbers\":[\"987789987\"],\"revision_date\":\"2022-08-16\",\"index_date\":\"2022-08-16\","
				+ "\"publish_date\":\"2022-08-16\",\"document_type\":\"Component Maintenance Manual\"},"
				+ "\"fields\":{\"revision_date\":[\"2022-08-16\"]},\"sort\":[1660608000000]}]}}";
		String payload = "{\"index\":\"av-cp-techpubs-avsystem\",\"body\":{\"from\":0,\"size\":1867,\"sort\":[{\"revision_date\":{\"order\":\"desc\"}}],\"_source\":{\"exclude\":[\"content\"]},\"docvalue_fields\":[{\"field\":\"revision_date\",\"format\":\"date\"}],\"query\":{\"bool\":{\"must\":[],\"filter\":[],\"should\":[],\"must_not\":[]}}}}";
		
		when(searchAppCaller.callSearchEndpoint(SSOID, PORTAL_ID, payload)).thenReturn(jsonString);
		Throwable e = Assertions.assertThrows(JSONException.class,
	            () -> avSystemDocAppImpl.getAvSystemExcelDocument(SSOID, PORTAL_ID, payload));

	    	assertEquals(e.getMessage(), "No value for hits");
        
    }
	
    @Test
    public void whenAvSystemExcelDocumentDownloadDTOListIsEmptyThenReturnsNull() throws TechpubsException, ExcelException, JSONException {

		String jsonString = "";
		String payload = "{\"index\":\"av-cp-techpubs-avsystem\",\"body\":{\"from\":0,\"size\":1867,\"sort\":[{\"revision_date\":{\"order\":\"desc\"}}],\"_source\":{\"exclude\":[\"content\"]},\"docvalue_fields\":[{\"field\":\"revision_date\",\"format\":\"date\"}],\"query\":{\"bool\":{\"must\":[],\"filter\":[],\"should\":[],\"must_not\":[]}}}}";
		Throwable e = Assertions.assertThrows(NullPointerException.class,
	            () -> avSystemDocAppImpl.getAvSystemExcelDocument(SSOID, PORTAL_ID, payload));

	    assertEquals(e.getMessage(), null);
        
    }
    
    //@Test
    public void getetDocumentFromS3Returns200() throws TechpubsException, FileNotFoundException {
  		when(amazonS3ClientFactory.getS3Client()).thenReturn(amazonS3Client);
  		when(amazonS3Client.getObject(any())).thenReturn(s3Object);
  		String filePath = "src/test/resources/test.txt";
  		SystemDocumentEntity systemDocument = new SystemDocumentEntity();
  		systemDocument.setFileName("fileName");
  		when(systemDocumentData.findByDocNumberSiteValueTypeValueAndDeleted("docNumber", "docSite", "docType", false)).thenReturn(Optional.of(systemDocument));
  		String icaoCode = "GEAE";
        	when (userService.getIcaoCode(SSOID)).thenReturn(icaoCode);
  		Path path = Paths.get(filePath);
  		FileInputStream fis = new FileInputStream(path.toFile());
  		when(s3Object.getObjectContent()).thenReturn(new S3ObjectInputStream(fis, null));
  		InputStreamResource in = avSystemDocAppImpl.getDocumentFromS3(SSOID, PORTAL_ID,"docType", "docSite", "docNumber", "fileName");
  		assertNotNull(in);
    }
    
    //@Test
    public void getDocumentFromS3Returns404() throws TechpubsException {
		String filePath = "src/test/resources/test.txt";
		SystemDocumentEntity systemDocument = new SystemDocumentEntity();
		systemDocument.setS3FilePath(filePath);
		Throwable e = Assertions.assertThrows(TechpubsException.class,
	            () -> avSystemDocAppImpl.getDocumentFromS3(SSOID, PORTAL_ID,"docType", "docSite", "docNumber", "fileName"));
		assertEquals(e.getMessage(), TechpubsException.TechpubsAppError.DATA_NOT_FOUND.getErrorMsg());
    }
    
    
    //@Test
    public void getDocumentFromS3ReturnsInvalidParameter() throws TechpubsException, FileNotFoundException {
  		SystemDocumentEntity systemDocument = new SystemDocumentEntity();
  		systemDocument.setFileName("fileName1");
  		when(systemDocumentData.findByDocNumberSiteValueTypeValueAndDeleted("docNumber", "docSite", "docType", false)).thenReturn(Optional.of(systemDocument));
  		String icaoCode = "GEAE";
        	when (userService.getIcaoCode(SSOID)).thenReturn(icaoCode);
  		Throwable e = Assertions.assertThrows(TechpubsException.class,
	            () -> avSystemDocAppImpl.getDocumentFromS3(SSOID, PORTAL_ID, "docType", "docSite", "docNumber", "fileName"));
		assertEquals(e.getMessage(), TechpubsException.TechpubsAppError.INVALID_PARAMETER.getErrorMsg());
    }
    
    //@Test
    public void getDocumentFromS3ReturnsUnAuthorized() throws TechpubsException, FileNotFoundException {
  		SystemDocumentEntity systemDocument = new SystemDocumentEntity();
  		systemDocument.setFileName("fileName");
  		when(systemDocumentData.findByDocNumberSiteValueTypeValueAndDeleted("docNumber", "docSite", "docType", false)).thenReturn(Optional.of(systemDocument));
  		String icaoCode = "GEAE1";
        	when (userService.getIcaoCode(SSOID)).thenReturn(icaoCode);
        	when(salesforceSvc.entitlementExistsForAirframeandDocType(icaoCode,"docType", systemDocument.getAirframes())).thenReturn(false);
  		Throwable e = Assertions.assertThrows(TechpubsException.class,
	            () -> avSystemDocAppImpl.getDocumentFromS3(SSOID, PORTAL_ID, "docType", "docSite", "docNumber", "fileName"));
		assertEquals(e.getMessage(), TechpubsException.TechpubsAppError.NOT_AUTHORIZED.getErrorMsg());
    }
    
   //@Test
    public void getDocumentFromS3ReturnsInternalError() throws TechpubsException, FileNotFoundException {
  		when(amazonS3ClientFactory.getS3Client()).thenReturn(amazonS3Client);
  		when(amazonS3Client.getObject(any())).thenThrow(AmazonServiceException.class);
  		String filePath = "src/test/resources/test.txt";
  		SystemDocumentEntity systemDocument = new SystemDocumentEntity();
  		systemDocument.setFileName("fileName");
  		when(systemDocumentData.findByDocNumberSiteValueTypeValueAndDeleted("docNumber", "docSite", "docType", false)).thenReturn(Optional.of(systemDocument));
  		String icaoCode = "GEAE";
        	when (userService.getIcaoCode(SSOID)).thenReturn(icaoCode);
  		Path path = Paths.get(filePath);
  		FileInputStream fis = new FileInputStream(path.toFile());
  		when(s3Object.getObjectContent()).thenReturn(new S3ObjectInputStream(fis, null));
  		Throwable e = Assertions.assertThrows(TechpubsException.class,
	            () -> avSystemDocAppImpl.getDocumentFromS3(SSOID, PORTAL_ID, "docType", "docSite", "docNumber", "fileName"));
		assertEquals(e.getMessage(), TechpubsException.TechpubsAppError.DOWNLOADING_FILE_FROM_S3_ERROR.getErrorMsg());
    }
    
}
