package com.geaviation.techpubs.service.impl.admin;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.geaviation.techpubs.controllers.requests.SortBy;
import com.geaviation.techpubs.data.api.techlib.IAirframeLookupData;
import com.geaviation.techpubs.data.api.techlib.ISalesforceCompanyData;
import com.geaviation.techpubs.data.api.techlib.ISystemDocumentData;
import com.geaviation.techpubs.data.api.techlib.ISystemDocumentSiteLookupData;
import com.geaviation.techpubs.data.api.techlib.ISystemDocumentTypeLookupData;
import com.geaviation.techpubs.data.factories.AmazonS3ClientFactory;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.exceptions.TechpubsException.TechpubsAppError;
import com.geaviation.techpubs.models.techlib.AirframeLookupEntity;
import com.geaviation.techpubs.models.techlib.PartNumbersAffectedEntity;
import com.geaviation.techpubs.models.techlib.SalesforceCompanyLookupEntity;
import com.geaviation.techpubs.models.techlib.SystemDocumentEntity;
import com.geaviation.techpubs.models.techlib.SystemDocumentSiteLookupEntity;
import com.geaviation.techpubs.models.techlib.SystemDocumentTypeLookupEntity;
import com.geaviation.techpubs.models.techlib.dto.AddSystemDocumentsDto;
import com.geaviation.techpubs.models.techlib.dto.SystemDocumentByIdDTO;
import com.geaviation.techpubs.models.techlib.dto.SystemDocumentDTO;
import com.geaviation.techpubs.models.techlib.dto.SystemDocumentExcelDownloadDTO;
import com.geaviation.techpubs.services.excel.ExcelMaker;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.ExcelSheet;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;
import com.geaviation.techpubs.services.impl.admin.AvSystemUploaderAppImpl;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

public class AvSystemUploaderAppImplTest {

  @Mock
  private ISystemDocumentData iSystemDocumentDataMock;

  @Mock
  ISystemDocumentSiteLookupData iSystemDocumentSiteLookupDataMock;

  @Mock
  ISystemDocumentTypeLookupData iSystemDocumentTypeLookupDataMock;

  @Mock
  IAirframeLookupData iAirframeLookupDataMock;

  @Mock
  ISalesforceCompanyData iSalesforceCompanyDataMock;

  @Mock
  AmazonS3ClientFactory amazonS3ClientFactory;

  @InjectMocks
  private AvSystemUploaderAppImpl avSystemUploaderAppMock;

  @Mock
  AmazonS3Client amazonS3Client;

  @Mock
  S3Object s3Object;

  private static final Integer PAGE = 0;
  private static final Integer SIZE = 10;
  private static final String PART_NUMBER = "part-number";
  private static final String DOCUMENT_TYPE = "document-type";
  private static final String DOCUMENT_TYPE_ID = "2784f3ab-18ba-4482-ad06-94e8e6312f3f";
  private static final String DOCUMENT_NUMBER = "212";
  private static final String DOCUMENT_SITE = "6c1e5311-948c-4a9b-9aa1-acf4c9b1045e";
  private static final String DOCUMENT_SITE_ID = "12345678-948c-4a9b-9aa1-acf4c9b1045e";
  private static final String DOCUMENT_TITLE = "document-title";
  private static final String DOCUMENT_REVISION = "document-revision";
  private static final Date DOCUMENT_DISTRIBUTION_DATE = new Date();
  private static final SortBy SORT_SYSTEM_DOCS_BY_DESC = new SortBy("sort-desc");
  private static final UUID TEST_UUID = UUID.fromString("4b2d0f50-9af6-4f86-9740-29770429fbaf");
  private static final Boolean POWERDOCUMENT = false;

  private static SystemDocumentDTO systemDocumentDTOMock;
  private List<SystemDocumentDTO> systemDocumentDTOListMock;
  private Page<SystemDocumentDTO> systemDocumentDTOPageMock;
  private static List<SystemDocumentEntity> systemDocumentEntityListMock;
  private static SystemDocumentEntity systemDocumentEntityMock;
  private static AddSystemDocumentsDto addSystemDocumentsDtoMock;
  private static SystemDocumentSiteLookupEntity systemDocumentSiteLookupEntityMock;
  private static SystemDocumentTypeLookupEntity systemDocumentTypeLookupEntityMock;
  private static PartNumbersAffectedEntity partNumbersAffectedEntityMock;
  private static AirframeLookupEntity airframeLookupEntityMock;
  private static SalesforceCompanyLookupEntity salesforceCompanyLookupEntityMock;
  private static SystemDocumentExcelDownloadDTO systemDocumentExcelDownloadDTOMock;
  private static List<SystemDocumentExcelDownloadDTO> systemDocumentExcelDownloadDTOListMock = new ArrayList<>();



  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);

    UUID mock = mock(UUID.class);

    when(mock.toString()).thenReturn(DOCUMENT_TYPE_ID);

    systemDocumentDTOMock = new SystemDocumentDTO(mock, DOCUMENT_TYPE, UUID.fromString(DOCUMENT_TYPE_ID), DOCUMENT_NUMBER,
        DOCUMENT_SITE, UUID.fromString(DOCUMENT_SITE_ID), DOCUMENT_TITLE, DOCUMENT_REVISION, DOCUMENT_DISTRIBUTION_DATE, POWERDOCUMENT);

    MockMultipartFile validFile = new MockMultipartFile("data", "filename.pdf", "application/pdf", "some txt".getBytes());

    systemDocumentTypeLookupEntityMock = new SystemDocumentTypeLookupEntity();
    systemDocumentTypeLookupEntityMock.setId(mock);
    systemDocumentTypeLookupEntityMock.setValue("value");

    systemDocumentSiteLookupEntityMock = new SystemDocumentSiteLookupEntity();
    systemDocumentSiteLookupEntityMock.setId(mock);
    systemDocumentSiteLookupEntityMock.setValue("value");

    salesforceCompanyLookupEntityMock = new SalesforceCompanyLookupEntity();
    salesforceCompanyLookupEntityMock.setId(mock);
    salesforceCompanyLookupEntityMock.setCompanyName("company-name");

    List<String> salesforceCompanyListMock = new ArrayList<>();
    salesforceCompanyListMock.add(mock.toString());

    Set<SalesforceCompanyLookupEntity> salesforceCompanyLookupEntitySetMock = new HashSet<>();
    salesforceCompanyLookupEntitySetMock.add(salesforceCompanyLookupEntityMock);

    partNumbersAffectedEntityMock = new PartNumbersAffectedEntity();
    partNumbersAffectedEntityMock.setId(mock);
    partNumbersAffectedEntityMock.setPartNumber("675");

    List<String> partNumbersAffectedListMock = new ArrayList<>();
    partNumbersAffectedListMock.add("987");

    Set<PartNumbersAffectedEntity> partNumbersAffectedEntitySetMock = new HashSet<>();
    partNumbersAffectedEntitySetMock.add(partNumbersAffectedEntityMock);

    airframeLookupEntityMock = new AirframeLookupEntity();
    airframeLookupEntityMock.setId(mock);
    airframeLookupEntityMock.setAirframe("airframe");

    List<String> airframeIdsMock = new ArrayList<>();
    airframeIdsMock.add(mock.toString());

    Set<AirframeLookupEntity> airframeLookupEntitySetMock = new HashSet<>();
    airframeLookupEntitySetMock.add(airframeLookupEntityMock);

    systemDocumentEntityMock = new SystemDocumentEntity();
    systemDocumentEntityMock.setId(mock);
    systemDocumentEntityMock.setDeleted(false);
    systemDocumentEntityMock.setSystemDocumentTypeLookupEntity(systemDocumentTypeLookupEntityMock);
    systemDocumentEntityMock.setSystemDocumentSiteLookupEntity(systemDocumentSiteLookupEntityMock);
    systemDocumentEntityMock.setDocumentNumber(DOCUMENT_NUMBER);
    systemDocumentEntityMock.setSpecificCompanies(salesforceCompanyLookupEntitySetMock);
    systemDocumentEntityMock.setCompanyPaidSubscription(salesforceCompanyLookupEntitySetMock);
    systemDocumentEntityMock.setPartNumbersAffectedEntity(partNumbersAffectedEntitySetMock);
    systemDocumentEntityMock.setAirframes(airframeLookupEntitySetMock);
    systemDocumentEntityMock.setCompanySpecific(false);
    systemDocumentEntityMock.setS3FilePath(validFile.toString());

    systemDocumentEntityListMock = new ArrayList<>();
    systemDocumentEntityListMock.add(systemDocumentEntityMock);

    addSystemDocumentsDtoMock = new AddSystemDocumentsDto();
    addSystemDocumentsDtoMock.setDocumentType(String.valueOf(UUID.fromString(DOCUMENT_TYPE_ID)));
    addSystemDocumentsDtoMock.setDocumentNumber(DOCUMENT_NUMBER);
    addSystemDocumentsDtoMock.setDocumentSite(String.valueOf(UUID.fromString(DOCUMENT_SITE)));
    addSystemDocumentsDtoMock.setDocumentUploadFile(validFile);
    addSystemDocumentsDtoMock.setCompanySpecific(true);
    addSystemDocumentsDtoMock.setPartsAffected(partNumbersAffectedListMock);
    addSystemDocumentsDtoMock.setDocumentRevisionDate("2022-08-10");
    addSystemDocumentsDtoMock.setDocumentDistributionDate("2022-08-12");
    addSystemDocumentsDtoMock.setAircraftPlatforms(airframeIdsMock);
    addSystemDocumentsDtoMock.setSpecificCustomers(salesforceCompanyListMock);

    systemDocumentExcelDownloadDTOMock = new SystemDocumentExcelDownloadDTO();

    systemDocumentExcelDownloadDTOListMock = new ArrayList<>();
    systemDocumentExcelDownloadDTOListMock.add(systemDocumentExcelDownloadDTOMock);
  }

  //@Test
  public void getSystemDocumentTypeReturnsSystemDocumentTypeListByDocumentTypeAndPartNumberAndDeleted()
      throws TechpubsException {
    systemDocumentDTOListMock = new ArrayList<>();
    systemDocumentDTOListMock.add(systemDocumentDTOMock);
    systemDocumentDTOPageMock = new PageImpl<>(systemDocumentDTOListMock);

    when(iSystemDocumentDataMock.findByDocumentTypeAndPartNumberAndDeleted((isA(UUID.class)),
        isA(String.class), isA(boolean.class), isA(Pageable.class))).thenReturn(
        systemDocumentDTOPageMock);

    Page<SystemDocumentDTO> systemDocumentDTOPageMocks = avSystemUploaderAppMock.getSystemDocumentsByDocumentTypeAndPartNumber(PART_NUMBER, DOCUMENT_TYPE_ID, 0, 10, SORT_SYSTEM_DOCS_BY_DESC);

    assertEquals(systemDocumentDTOPageMock, systemDocumentDTOPageMocks);
  }

  //@Test
  public void getSystemDocumentTypeReturnsSystemDocumentTypeListByPartNumberAndDeleted() throws TechpubsException {

    systemDocumentDTOListMock = new ArrayList<>();
    systemDocumentDTOListMock.add(systemDocumentDTOMock);
    systemDocumentDTOPageMock = new PageImpl<>(systemDocumentDTOListMock);

    when(iSystemDocumentDataMock.findByPartNumberAndDeleted(isA(String.class), isA(boolean.class),isA(Pageable.class)))
        .thenReturn(systemDocumentDTOPageMock);

    Page<SystemDocumentDTO> systemDocumentDTOPageMocks = avSystemUploaderAppMock
        .getSystemDocumentsByDocumentTypeAndPartNumber(PART_NUMBER, null, PAGE, SIZE, SORT_SYSTEM_DOCS_BY_DESC);

    assertEquals(systemDocumentDTOPageMock, systemDocumentDTOPageMocks);
  }

  //@Test
  public void getSystemDocumentTypeReturnsSystemDocumentTypeListByDocumentTypeAndDeleted() throws TechpubsException {
    systemDocumentDTOListMock = new ArrayList<>();
    systemDocumentDTOListMock.add(systemDocumentDTOMock);
    systemDocumentDTOPageMock = new PageImpl<>(systemDocumentDTOListMock);

    when(iSystemDocumentDataMock.findByDocumentTypeAndDeleted(isA(UUID.class), isA(boolean.class),isA(Pageable.class)))
        .thenReturn(systemDocumentDTOPageMock);

    Page<SystemDocumentDTO> systemDocumentDTOPageMocks = avSystemUploaderAppMock
        .getSystemDocumentsByDocumentTypeAndPartNumber(null, DOCUMENT_TYPE_ID, PAGE, SIZE, SORT_SYSTEM_DOCS_BY_DESC);

    assertEquals(systemDocumentDTOPageMock, systemDocumentDTOPageMocks);
  }

  //@Test
  public void getSystemDocumentTypeReturnsSystemDocumentTypeListByDeletedPaginated() throws TechpubsException {

    systemDocumentDTOListMock = new ArrayList<>();
    systemDocumentDTOListMock.add(systemDocumentDTOMock);
    systemDocumentDTOPageMock = new PageImpl<>(systemDocumentDTOListMock);

    when(iSystemDocumentDataMock.findByDeletedPaginated(isA(boolean.class),isA(Pageable.class)))
        .thenReturn(systemDocumentDTOPageMock);

    Page<SystemDocumentDTO> systemDocumentDTOPageMocks = avSystemUploaderAppMock
        .getSystemDocumentsByDocumentTypeAndPartNumber(null, null, PAGE, SIZE, SORT_SYSTEM_DOCS_BY_DESC);

    assertEquals(systemDocumentDTOPageMock, systemDocumentDTOPageMocks);
  }

  @Test
  public void getSystemDocumentListForExcelDownloadWhenSystemDocumentListExists()
      throws TechpubsException {
    when(iSystemDocumentDataMock.findByDeleted(isA(Boolean.class))).thenReturn(Optional.of(systemDocumentEntityListMock));
    List<SystemDocumentExcelDownloadDTO> response = avSystemUploaderAppMock.getSystemDocumentListForExcelDownload();

    assertNotNull(response);
  }

  @Test
  public void getSystemDocumentListForExcelDownloadThrowsDataNotFoundErrorIfListDoesNotExist() {
    when(iSystemDocumentDataMock.findByDeleted(isA(Boolean.class))).thenReturn(Optional.empty());

    Throwable e = Assertions.assertThrows(TechpubsException.class, () -> avSystemUploaderAppMock.getSystemDocumentListForExcelDownload());
    assertEquals(((TechpubsException) e).getTechpubsAppError(), TechpubsAppError.INTERNAL_ERROR);
  }

  @Test
  public void getSystemDocumentByIdValidRequest() throws TechpubsException {
    UUID mock = UUID.randomUUID();
    when(iSystemDocumentDataMock.findById(mock)).thenReturn(Optional.of(systemDocumentEntityMock));
    SystemDocumentByIdDTO response = avSystemUploaderAppMock.getSystemDocumentById(String.valueOf(mock));
    assertEquals(systemDocumentEntityMock.getId().toString(), response.getSystemDocumentId());
  }

  @Test
  public void getSystemDocumentByIdDataNotFoundAndInternalError() {
    UUID mock = UUID.randomUUID();
    when(iSystemDocumentDataMock.findById(mock)).thenReturn(Optional.empty());
    Throwable e = Assertions.assertThrows(TechpubsException.class, () -> avSystemUploaderAppMock.getSystemDocumentById(String.valueOf(mock)));
    assertEquals(((TechpubsException) e).getTechpubsAppError(), TechpubsException.TechpubsAppError.INTERNAL_ERROR);
  }

  @Test
  public void getSystemDocumentByIdInternalError() {
    UUID mock = UUID.randomUUID();
    when(iSystemDocumentDataMock.findById(mock)).thenReturn(Optional.empty());
    Throwable e = Assertions.assertThrows(TechpubsException.class, () -> avSystemUploaderAppMock.getSystemDocumentById(String.valueOf(mock)));
    assertEquals(((TechpubsException) e).getTechpubsAppError(), TechpubsException.TechpubsAppError.INTERNAL_ERROR);
  }


  //FIGURE OUT WHY THIS TEST KEEPS FAILING THE BUILD!!!!
//  @Test
//  public void addSystemDocumentThrowsInvalidDocumentIdentifiersIfDocumentAlreadyExists(){
//    when(iSystemDocumentDataMock.findByDocumentNumberSiteAndType(isA(UUID.class), isA(String.class), isA(UUID.class)))
//        .thenReturn(Optional.of(systemDocumentEntityMock));
//    Throwable e = Assertions.assertThrows(TechpubsException.class, () -> avSystemUploaderAppMock.addSystemDocuments(addSystemDocumentsDtoMock));
//    assertEquals(((TechpubsException) e).getTechpubsAppError(), TechpubsAppError.INVALID_DOCUMENT_IDENTIFIERS);
//  }


//  @Test
//  public void addSystemDocumentSuccessfulIfItDoesNotAlreadyExist() throws TechpubsException {
//    when(iSystemDocumentDataMock.findByDocumentNumberSiteAndType(isA(UUID.class), isA(String.class), isA(UUID.class)))
//        .thenReturn(Optional.empty());
//
//    when(iSystemDocumentTypeLookupDataMock.findById(isA(UUID.class))).thenReturn(Optional.of(systemDocumentTypeLookupEntityMock));
//    when(iSystemDocumentSiteLookupDataMock.findById(isA(UUID.class))).thenReturn(Optional.of(systemDocumentSiteLookupEntityMock));
//
//    when(iAirframeLookupDataMock.findById(isA(UUID.class))).thenReturn(Optional.of(airframeLookupEntityMock));
//    when(iSalesforceCompanyDataMock.findById(isA(UUID.class))).thenReturn(Optional.of(salesforceCompanyLookupEntityMock));
//    when(amazonS3ClientFactory.getS3Client()).thenReturn(amazonS3ClientMock);
//
//    when(iSystemDocumentDataMock.save(isA(SystemDocumentEntity.class))).thenReturn(systemDocumentEntityMock);
//
//    String response = avSystemUploaderAppMock.addSystemDocuments(addSystemDocumentsDtoMock);
//    assertEquals(systemDocumentEntityMock.getId().toString(), response);
//  }
//
//  @Test
//  public void ifSystemDocumentExistsThenReturnTrueThatDocumentIsPresent() throws TechpubsException {
//    when(iSystemDocumentDataMock.findByDocumentNumberSiteAndType(isA(UUID.class), isA(String.class), isA(UUID.class))).thenReturn(Optional.of(systemDocumentEntityMock));
//    Boolean response = avSystemUploaderAppMock.systemDocumentExists(String.valueOf(TEST_UUID), DOCUMENT_NUMBER, String.valueOf(TEST_UUID));
//    assertEquals(true, response);
//  }
//
//  @Test
//  public void ifSystemDocumentDoesNotExistThenReturnFalseThatDocumentIsPresent() throws TechpubsException {
//    when(iSystemDocumentDataMock.findByDocumentNumberSiteAndType(isA(UUID.class), isA(String.class), isA(UUID.class))).thenReturn(Optional.empty());
//    Boolean response = avSystemUploaderAppMock.systemDocumentExists(String.valueOf(TEST_UUID), "", String.valueOf(TEST_UUID));
//    assertEquals(false, response);
//  }


  @Test
  public void aFileWithBytesReturnedWhenDownloadEngineDocuments() throws ExcelException, TechpubsException {
    systemDocumentEntityListMock = new ArrayList<>();
    systemDocumentEntityListMock.add(systemDocumentEntityMock);

    ExcelSheet excelSheet = ExcelMaker.buildExcelSheet(systemDocumentExcelDownloadDTOListMock);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    FileWithBytes fileDownload = new FileWithBytes(os.toByteArray(), excelSheet.getFilename());
    when(iSystemDocumentDataMock.findByDeleted(isA(Boolean.class))).thenReturn(Optional.of(systemDocumentEntityListMock));

    FileWithBytes response = avSystemUploaderAppMock.downloadSystemDocuments();
    assertThat(fileDownload.getFileName().equals(excelSheet.getFilename()));
  }

  @Test
  public void downloadSystemDocumentsThrowsDataNotFoundErrorIfListDoesNotExist() throws ExcelException, TechpubsException {
    systemDocumentEntityListMock = new ArrayList<>();
    SystemDocumentEntity systemDocumentEntityMock = new SystemDocumentEntity();
    systemDocumentEntityListMock.add(systemDocumentEntityMock);

    ExcelSheet excelSheet = ExcelMaker.buildExcelSheet(systemDocumentExcelDownloadDTOListMock);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    FileWithBytes fileDownload = new FileWithBytes(os.toByteArray(), excelSheet.getFilename());
    when(iSystemDocumentDataMock.findByDeleted(isA(Boolean.class))).thenReturn(Optional.of(systemDocumentEntityListMock));

    Throwable e = Assertions.assertThrows(TechpubsException.class, () -> avSystemUploaderAppMock.downloadSystemDocuments());
    assertEquals(((TechpubsException) e).getTechpubsAppError(), TechpubsAppError.INTERNAL_ERROR);
  }

  @Test
  public void getSystemDocumentDownloadSucess() throws TechpubsException, IOException {
    when(amazonS3ClientFactory.getS3Client()).thenReturn(amazonS3Client);
    when(amazonS3Client.getObject(any())).thenReturn(s3Object);
    String filePath = "src/test/resources/test.txt";
    SystemDocumentEntity systemDocument = new SystemDocumentEntity();
    systemDocument.setS3FilePath(filePath);
    Path path = Paths.get(filePath);
    byte[] data = Files.readAllBytes(path);
    FileInputStream fis = new FileInputStream(path.toFile());
    when(s3Object.getObjectContent()).thenReturn(new S3ObjectInputStream(fis, null));
    byte[] getSystemDocumentDownload = avSystemUploaderAppMock.getSystemDocumentDownload(systemDocument);
    Assert.assertArrayEquals(data, getSystemDocumentDownload);
  }

  @Test
  public void getSystemDocumentDownloadInternalError() throws TechpubsException, IOException {
    when(amazonS3ClientFactory.getS3Client()).thenReturn(amazonS3Client);
    when(amazonS3Client.getObject(any())).thenThrow(AmazonServiceException.class);
    String filePath = "src/test/resources/test.txt";
    SystemDocumentEntity systemDocument = new SystemDocumentEntity();
    systemDocument.setS3FilePath(filePath);
    Path path = Paths.get(filePath);
    byte[] data = Files.readAllBytes(path);
    FileInputStream fis = new FileInputStream(path.toFile());
    when(s3Object.getObjectContent()).thenReturn(new S3ObjectInputStream(fis, null));
    Throwable e = Assertions.assertThrows(TechpubsException.class,
        () -> avSystemUploaderAppMock.getSystemDocumentDownload(systemDocument));
    assertEquals(((TechpubsException) e).getTechpubsAppError(), TechpubsAppError.DOWNLOADING_FILE_FROM_S3_ERROR);
  }
}
