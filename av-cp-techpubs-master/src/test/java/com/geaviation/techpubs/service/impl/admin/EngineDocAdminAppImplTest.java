package com.geaviation.techpubs.service.impl.admin;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.geaviation.techpubs.models.techlib.dto.*;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.geaviation.techpubs.controllers.requests.SortBy;
import com.geaviation.techpubs.data.api.techlib.enginedoc.IEngineDocumentData;
import com.geaviation.techpubs.data.api.techlib.enginedoc.IEngineDocumentTypeLookupData;
import com.geaviation.techpubs.data.api.techlib.enginedoc.IEnginePartNumberLookupData;
import com.geaviation.techpubs.data.factories.AmazonS3ClientFactory;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.exceptions.TechpubsException.TechpubsAppError;
import com.geaviation.techpubs.models.techlib.EngineModelEntity;
import com.geaviation.techpubs.models.techlib.enginedoc.EngineCASNumberEntity;
import com.geaviation.techpubs.models.techlib.enginedoc.EngineDocumentEntity;
import com.geaviation.techpubs.models.techlib.enginedoc.EngineDocumentTypeLookupEntity;
import com.geaviation.techpubs.models.techlib.enginedoc.EnginePartNumberLookupEntity;
import com.geaviation.techpubs.services.api.validator.IEngineDocumentsValidator;
import com.geaviation.techpubs.services.excel.ExcelMaker;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.ExcelSheet;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;
import com.geaviation.techpubs.services.impl.admin.EngineDocAdminAppImpl;
import com.geaviation.techpubs.services.impl.validator.EngineDocumentsValidatorImpl;
import com.geaviation.techpubs.services.util.admin.EngineModelTableUpdater;

public class EngineDocAdminAppImplTest {

    private static final int PAGE = 0;
    private static final int SIZE = 5;
    private static final String ID = "id";
    private static final String DOCUMENT_TYPE = "document-type";
    private static final String DOCUMENT_TITLE = "document-title";
    private static final List<String> ENGINE_MODELS = new ArrayList<>();
    private static final String LAST_UPDATED_DATE = "last-updated-date";
    private static final String ISSUE_DATE = "issue-date";
    private static final SortBy SORT_ENGINE_DOCS_BY_DESC = new SortBy("desc");
    private static final String PART_NAME = "part-name";
    private static final String SSO_ID = "sso-id";
    private static final String SEARCH_TERM ="search-term";
    private final Set<EngineDocumentEntity> mockEngineDocumentEntitySet = new HashSet<EngineDocumentEntity>();
    private final Set<EnginePartNumberLookupEntity> enginePartNumberLookupEntitySetMock = new HashSet<>();
    private final Set<EngineCASNumberEntity> engineCASNumberEntitySetMock = new HashSet<>();
    private final Set<EngineModelEntity> engineModelEntitySetMock = new HashSet<>();
    private final List<String> engineModelsListMock = new ArrayList<>();
    private final List<String> inputModelsListMock = new ArrayList<>();
    private final List<String> casNumbersListMock = new ArrayList<>();
    private final List<String> partNumbersListMock = new ArrayList<>();
    private final Map<String, List<String>> engineFamilyToModelListMap = new HashMap<>();
    private Page<EngineDocumentDTO> engineDocumentDTOPageMock;
    private Page<EngineDocumentEntity> engineDocumentEntityPageMock;
    private EngineDocumentDTO engineDocumentDTOMock;
    private EngineDocumentExcelDownloadDTO engineDocumentExcelDownloadDTOMock;
    private EngineModelEntity engineModelEntityMock;
    private List<EngineDocumentDTO> engineDocumentDTOListMock;
    private List<EngineDocumentEntity> engineDocumentEntityListMock;
    private List<EngineDocumentExcelDownloadDTO> engineDocumentExcelDownloadDTOListMock = new ArrayList<>();
    private EngineDocumentEntity engineDocumentEntityMock;
    private EngineDocumentAddReachDTO engineDocumentAddReachDTOMock;
    private EnginePartNumberLookupEntity enginePartNumberLookupEntityMock;
    private EngineCASNumberEntity engineCASNumberEntityMock;
    private List<EngineDocumentTypeLookupEntity> engineDocumentTypeLookupList;
    
    @InjectMocks
    private EngineDocAdminAppImpl engineDocAdminAppMock;
    
    @Mock
    private EngineDocumentTypeLookupEntity engineDocumentTypeLookupEntityMock;
    
    @Mock
    private IEngineDocumentTypeLookupData iEngineDocumentTypeLookupDataMock;
    
    @Mock
    private AmazonS3ClientFactory amazonS3ClientFactoryMock;
    
    @Mock
    private AmazonS3Client amazonS3ClientMock;
    
    @Mock
    private S3Object S3ObjectMock;
    
    @Mock
    private IEngineDocumentData engineDocumentDataMock;
    
    @Mock
    private IEnginePartNumberLookupData iEnginePartNumberLookupDataMock;
    
    @Mock
    private EngineModelTableUpdater engModelUpdaterMock;
    
    @Mock
    private IEngineDocumentsValidator engineDocValidator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        UUID mock = mock(UUID.class);

        engineDocumentDTOMock = new EngineDocumentDTO(ID, DOCUMENT_TYPE, DOCUMENT_TITLE, ENGINE_MODELS, LAST_UPDATED_DATE, ISSUE_DATE);

        engineDocumentEntityMock = new EngineDocumentEntity();
        engineDocumentEntityMock.setId(mock);
        engineDocumentEntityMock.setDocumentTitle(DOCUMENT_TITLE);
        engineDocumentEntityMock.setPartName(PART_NAME);
        engineDocumentEntityMock.setEmailNotification(false);
        engineDocumentEntityMock.setDeleted(false);
        engineDocumentEntityMock.setFileName("file-name");
        engineDocumentEntityMock.setCreatedDate(new Date(2022 - 06 - 17));
        engineDocumentEntityMock.setLastUpdatedDate(new Date(2022 - 06 - 16));
        engineDocumentEntityMock.setIssueDate(new Date(2022 - 06 - 18));
        engineDocumentEntityMock.setEngineDocumentTypeLookupEntity(new EngineDocumentTypeLookupEntity());
        engineDocumentEntityMock.setEnginePartNumbers(enginePartNumberLookupEntitySetMock);
        engineDocumentEntityMock.setEngineCASNumberEntity(engineCASNumberEntitySetMock);
        engineDocumentEntityMock.setEngineModelEntity(engineModelEntitySetMock);
        engineDocumentEntityMock.setEngineDocumentTypeLookupEntity(engineDocumentTypeLookupEntityMock);

        Set<EngineDocumentEntity> engineDocumentEntitySetMock = new HashSet<>();
        engineDocumentEntitySetMock.add(engineDocumentEntityMock);

        engineDocumentDTOMock = new EngineDocumentDTO();

        engineDocumentExcelDownloadDTOMock = new EngineDocumentExcelDownloadDTO();

        enginePartNumberLookupEntityMock = new EnginePartNumberLookupEntity();
        enginePartNumberLookupEntityMock.setId(mock);
        enginePartNumberLookupEntityMock.setValue("9999");
        enginePartNumberLookupEntityMock.setLastUpdatedBy("last-updated-by");
        enginePartNumberLookupEntityMock.setLastUpdatedDate(new Date());

        enginePartNumberLookupEntitySetMock.add(enginePartNumberLookupEntityMock);

        engineDocumentExcelDownloadDTOListMock = new ArrayList<>();
        engineDocumentExcelDownloadDTOListMock.add(engineDocumentExcelDownloadDTOMock);

        Set<EnginePartNumberLookupEntity> enginePartNumberLookupEntitySetMock = new HashSet<>();
        enginePartNumberLookupEntitySetMock.add(enginePartNumberLookupEntityMock);

        engineCASNumberEntityMock = new EngineCASNumberEntity();
        engineCASNumberEntityMock.setId(mock);
        engineCASNumberEntityMock.setEngineDocumentEntity(engineDocumentEntityMock);
        engineCASNumberEntityMock.setCasNumber("98765");

        Set<EngineCASNumberEntity> engineCASNumberEntitySetMock = new HashSet<>();
        engineCASNumberEntitySetMock.add(engineCASNumberEntityMock);

        engineModelsListMock.add("CF34-10A");

        inputModelsListMock.add("CF34-10A");

        casNumbersListMock.add("9999");

        partNumbersListMock.add("1111");

        engineDocumentAddReachDTOMock = new EngineDocumentAddReachDTO();
        engineDocumentAddReachDTOMock.setDocumentTitle("document-title");
        engineDocumentAddReachDTOMock.setEngineModels(engineModelsListMock);
        engineDocumentAddReachDTOMock.setIssueDate("2022-06-09");
        engineDocumentAddReachDTOMock.setPartName("part-name");
        engineDocumentAddReachDTOMock.setEmailNotification(false);
        engineDocumentAddReachDTOMock.setEngineModels(engineModelsListMock);
        engineDocumentAddReachDTOMock.setCasNumbers(casNumbersListMock);
        engineDocumentAddReachDTOMock.setPartNumbers(partNumbersListMock);

        engineModelEntityMock = new EngineModelEntity();
        engineModelEntityMock.setModel("CF6-6");
        engineModelEntityMock.setFamily("family");
        engineModelEntityMock.setLastUpdatedAt(new Timestamp(2022 - 06 - 03));
        engineModelEntityMock.setCreatedAt(new Timestamp(2022 - 07 - 01));
        engineModelEntityMock.setCreatedBy("created-by");
        engineModelEntityMock.setLastUpdatedBy("last-updated-by");

        Set<EngineModelEntity> engineModelEntitySetMock = new HashSet<>();
        engineModelEntitySetMock.add(engineModelEntityMock);

        engineDocumentTypeLookupEntityMock = new EngineDocumentTypeLookupEntity();
        engineDocumentTypeLookupEntityMock.setId(mock);
        engineDocumentTypeLookupEntityMock.setEngineDocumentEntity(engineDocumentEntitySetMock);
        engineDocumentTypeLookupEntityMock.setValue("REACH");

    }

    @Test
    public void getEngineDocumentsGivenPageAndSortByDescAndReachMvp2NotActiveReturnListOfEngineDocuments() throws TechpubsException {
        engineDocumentDTOListMock = new ArrayList<>();
        engineDocumentDTOListMock.add(engineDocumentDTOMock);
        engineDocumentDTOPageMock = new PageImpl<>(engineDocumentDTOListMock);

        engineDocumentEntityListMock = new ArrayList<>();
        engineDocumentEntityListMock.add(engineDocumentEntityMock);
        engineDocumentEntityPageMock = new PageImpl<>(engineDocumentEntityListMock);

        String documentType = "Reach";
        List<String> engineModels = new ArrayList<>();
        engineModels.add("CF34-10A");

        when(engineDocumentDataMock.findByDeleted( isA(Boolean.class), isA(Pageable.class))).thenReturn(engineDocumentEntityPageMock);

        Page<EngineDocumentDTO> engineDocumentDTOPage = engineDocAdminAppMock.getEngineDocuments(documentType, engineModels,SEARCH_TERM, PAGE, SIZE, SORT_ENGINE_DOCS_BY_DESC);

        List<String> engineModelsList = new ArrayList<>();
        EngineModelEntity engineModelEntity = new EngineModelEntity();
        engineModelsList.add(engineModelEntity.getModel());

        engineDocumentEntityMock.getEngineModelEntity();

        assertEquals(engineDocumentDTOPage, engineDocumentDTOPage);
    }
    
    @Test
    public void getEngineDocumentsGivenPageAndSortByDescAndReachMvp2ActiveReturnListOfEngineDocuments() throws TechpubsException {
        engineDocumentDTOListMock = new ArrayList<>();
        engineDocumentDTOListMock.add(engineDocumentDTOMock);
        engineDocumentDTOPageMock = new PageImpl<>(engineDocumentDTOListMock);

        engineDocumentEntityListMock = new ArrayList<>();
        engineDocumentEntityListMock.add(engineDocumentEntityMock);
        engineDocumentEntityPageMock = new PageImpl<>(engineDocumentEntityListMock);

        String documentType = "Reach";
        List<String> engineModels = new ArrayList<>();
        engineModels.add("CF34-10A");
      
        ReflectionTestUtils.setField(engineDocAdminAppMock, "reachMvp2Active", true);

        when(engineDocumentDataMock.findByDocumentTypeAndEngineModelAndDeletedPaginated(isA(String.class), isA(List.class), isA(String.class), isA(Boolean.class), isA(Pageable.class))).thenReturn(engineDocumentEntityPageMock);
        Page<EngineDocumentDTO> engineDocumentDTOPage = engineDocAdminAppMock.getEngineDocuments(documentType, engineModels,SEARCH_TERM, PAGE, SIZE, SORT_ENGINE_DOCS_BY_DESC);

        List<String> engineModelsList = new ArrayList<>();
        EngineModelEntity engineModelEntity = new EngineModelEntity();
        engineModelsList.add(engineModelEntity.getModel());

        engineDocumentEntityMock.getEngineModelEntity();

        assertEquals(engineDocumentDTOPage, engineDocumentDTOPage);
    }


    @Test
    public void getEngineDocumentTypesReturnsAllEngineDocumentTypesList() {
        engineDocumentTypeLookupList = new ArrayList<>();
        engineDocumentTypeLookupEntityMock = new EngineDocumentTypeLookupEntity(UUID.fromString("edf0c4b7-ce82-4d0d-bf12-3ac61f7bf2b6"), "REACH", mockEngineDocumentEntitySet);
        engineDocumentTypeLookupList.add(engineDocumentTypeLookupEntityMock);
        when(iEngineDocumentTypeLookupDataMock.findAll()).thenReturn(engineDocumentTypeLookupList);
        List<String> engineDocumentTypesList = engineDocAdminAppMock.getEngineDocumentTypes();
        assertFalse(engineDocumentTypesList.isEmpty());
    }

    @Test
    public void getEngineDocumentTypesReturnsEmptyEngineDocumentTypesList() {
        engineDocumentTypeLookupList = new ArrayList<>();
        when(iEngineDocumentTypeLookupDataMock.findAll()).thenReturn(engineDocumentTypeLookupList);
        List<String> engineDocumentTypesList = engineDocAdminAppMock.getEngineDocumentTypes();
        assertTrue(engineDocumentTypesList.isEmpty());
    }

    @Test
    public void AddEngineDocumentsSuccessfullyAddsAFile() throws TechpubsException {
        List<EngineModelEntity> engineModelEntityList = new ArrayList<>();
        engineModelEntityList.add(engineModelEntityMock);

        when(iEngineDocumentTypeLookupDataMock.findByValue("REACH")).thenReturn(Optional.of(engineDocumentTypeLookupEntityMock));

        when(amazonS3ClientFactoryMock.getS3Client()).thenReturn(amazonS3ClientMock);
        MockMultipartFile validFile = new MockMultipartFile("data", "filename.pdf", "application/pdf", "some txt".getBytes());

        when(engineDocumentDataMock.save(isA(EngineDocumentEntity.class))).thenReturn(engineDocumentEntityMock);

        when(iEnginePartNumberLookupDataMock.findByValue("part-number")).thenReturn(Optional.of(enginePartNumberLookupEntityMock));

        when(engModelUpdaterMock.validateEngineModels(anyList(), anyString())).thenReturn(new HashSet<>());
        
        doNothing().when(engineDocValidator).validateEngineDocuments("REACH", engineDocumentAddReachDTOMock);
        
        engineDocumentAddReachDTOMock.setDocumentUploadFile(validFile);
        
        engineDocAdminAppMock.addEngineDocuments(engineDocumentAddReachDTOMock, SSO_ID, "REACH");

        EngineDocumentEntity newEngineDoc = engineDocAdminAppMock.addEngineDocuments(engineDocumentAddReachDTOMock, SSO_ID, "REACH");

        assertNotNull(newEngineDoc);
    }

    @Test
    public void AddEngineDocumentsThrowsInvalidParameterErrorWhenValidateEngineModelFails() throws TechpubsException {
        List<EngineModelEntity> engineModelEntityList = new ArrayList<>();
        engineModelEntityList.add(engineModelEntityMock);

        inputModelsListMock.add("CF7-7");


        when(iEngineDocumentTypeLookupDataMock.findByValue("REACH")).thenReturn(Optional.of(engineDocumentTypeLookupEntityMock));
       
        when(amazonS3ClientFactoryMock.getS3Client()).thenReturn(amazonS3ClientMock);
        MockMultipartFile validFile = new MockMultipartFile("data", "filename.pdf", "application/pdf", "some txt".getBytes());

        when(engineDocumentDataMock.save(isA(EngineDocumentEntity.class))).thenReturn(engineDocumentEntityMock);

        when(iEnginePartNumberLookupDataMock.findByValue("part-number")).thenReturn(Optional.of(enginePartNumberLookupEntityMock));

        when(engModelUpdaterMock.validateEngineModels(anyList(), anyString()))
        	.thenThrow(new TechpubsException(TechpubsAppError.INVALID_PARAMETER));
        
        engineDocumentAddReachDTOMock.setDocumentUploadFile(validFile);
        
        Throwable e = Assertions.assertThrows(TechpubsException.class, () 
        		-> engineDocAdminAppMock.addEngineDocuments(engineDocumentAddReachDTOMock, SSO_ID, "REACH"));
        assertEquals(((TechpubsException) e).getTechpubsAppError(), TechpubsAppError.INVALID_PARAMETER);

    }

    @Test
    public void AddDocumentTestReturnsInvalidParameterWhenEngineTypeLookupEntityIsNotPresent() throws TechpubsException {
        List<EngineModelEntity> engineModelEntityList = new ArrayList<>();
        engineModelEntityList.add(engineModelEntityMock);

        when(iEngineDocumentTypeLookupDataMock.findByValue("REACH")).thenReturn(Optional.empty());

        MockMultipartFile validFile = new MockMultipartFile("data", "filename.pdf", "application/pdf", "some txt".getBytes());

        engineDocumentAddReachDTOMock.setDocumentUploadFile(validFile);

        try {
            engineDocAdminAppMock.addEngineDocuments(engineDocumentAddReachDTOMock, SSO_ID, "REACH");
        } catch (TechpubsException e) {
            assertEquals(e.getTechpubsAppError(), TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }
    }

    @Test
    public void AddDocumentTestReturnsInvalidParameterWhenErrorParsingIssueDate() throws TechpubsException {
        List<EngineModelEntity> engineModelEntityList = new ArrayList<>();
        engineModelEntityList.add(engineModelEntityMock);

        engineDocumentAddReachDTOMock.setIssueDate("tiger");

        when(iEngineDocumentTypeLookupDataMock.findByValue("REACH")).thenReturn(Optional.of(engineDocumentTypeLookupEntityMock));

        when(amazonS3ClientFactoryMock.getS3Client()).thenReturn(amazonS3ClientMock);
        MockMultipartFile validFile = new MockMultipartFile("data", "filename.pdf", "application/pdf", "some txt".getBytes());

        when(engineDocumentDataMock.save(isA(EngineDocumentEntity.class))).thenReturn(engineDocumentEntityMock);

        when(iEnginePartNumberLookupDataMock.findByValue("part-number")).thenReturn(Optional.of(enginePartNumberLookupEntityMock));

        engineDocumentAddReachDTOMock.setDocumentUploadFile(validFile);
        try {
            engineDocAdminAppMock.addEngineDocuments(engineDocumentAddReachDTOMock, SSO_ID, "REACH");
        } catch (TechpubsException e) {
            assertEquals(e.getTechpubsAppError(), TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }
    }

    @Test
    public void whenNoDocumentUploadFileThenAddEngineDocumentsWillResultInAnInvalidParameterError() {
        when(engineDocumentDataMock.save(isA(EngineDocumentEntity.class))).thenReturn(engineDocumentEntityMock);

        Throwable e = Assertions.assertThrows(TechpubsException.class, () -> engineDocAdminAppMock.addEngineDocuments(engineDocumentAddReachDTOMock, SSO_ID, "REACH"));
        assertEquals(((TechpubsException) e).getTechpubsAppError(), TechpubsAppError.INVALID_PARAMETER);
    }

    @Test
    public void whenDocumentIsNotAPDFThenAddEngineDocumentsWillResultInAnInvalidParameterError() {
        when(engineDocumentDataMock.save(isA(EngineDocumentEntity.class))).thenReturn(engineDocumentEntityMock);

        MockMultipartFile nonPDFFile = new MockMultipartFile("data", "filename.txt", "text/plain", "some txt".getBytes());

        engineDocumentAddReachDTOMock.setDocumentUploadFile(nonPDFFile);

        Throwable e = Assertions.assertThrows(TechpubsException.class, () -> engineDocAdminAppMock.addEngineDocuments(engineDocumentAddReachDTOMock, SSO_ID, "test/plain"));
        assertEquals(((TechpubsException) e).getTechpubsAppError(), TechpubsAppError.INVALID_PARAMETER);
    }


    @Test
    public void deleteEngineDocumentInvalidParameterReturnsInvalidParameter() {
        UUID mock = mock(UUID.class);
        mockStatic(UUID.class);
        String id = "id";
        given(UUID.fromString(id)).willReturn(mock);
        Throwable e = Assertions.assertThrows(TechpubsException.class, () -> engineDocAdminAppMock.deleteEngineDocument("uuid"));
        assertEquals(((TechpubsException) e).getTechpubsAppError(), TechpubsException.TechpubsAppError.INVALID_PARAMETER);
    }

    @Test
    public void deleteEngineDocumentOptionalIsEmptyReturnsInvalidParameter() {
        UUID mock = UUID.randomUUID();
        when(engineDocumentDataMock.findById(mock)).thenReturn(Optional.empty());
        Throwable e = Assertions.assertThrows(TechpubsException.class, () -> engineDocAdminAppMock.deleteEngineDocument(String.valueOf(mock)));
        assertEquals(((TechpubsException) e).getTechpubsAppError(), TechpubsException.TechpubsAppError.INVALID_PARAMETER);
    }

    @Test
    public void deleteEngineDocumentValidRequest() throws TechpubsException {
        UUID mock = UUID.randomUUID();
        EngineDocumentEntity engineDocument = new EngineDocumentEntity();
        engineDocument.setId(mock);
        engineDocument.setDeleted(true);
        when(engineDocumentDataMock.findById(mock)).thenReturn(Optional.of(engineDocument));
        when(engineDocAdminAppMock.deleteEngineDocument(String.valueOf(mock))).thenReturn(engineDocument);
        assertEquals(engineDocument, engineDocument);
    }

    @Test
    public void getFileInputStreamFromS3DownloadError() throws TechpubsException {
        UUID mock = UUID.randomUUID();
        EngineDocumentEntity engineDocument = new EngineDocumentEntity();
        engineDocument.setId(mock);
        when(engineDocumentDataMock.findById(mock)).thenReturn(Optional.of(engineDocument));
        when(amazonS3ClientFactoryMock.getS3Client()).thenThrow(AmazonServiceException.class);
        Throwable e = Assertions.assertThrows(TechpubsException.class, () -> engineDocAdminAppMock.getFileInputStreamFromS3(String.valueOf(mock)));
        assertEquals(((TechpubsException) e).getTechpubsAppError(), TechpubsException.TechpubsAppError.DOWNLOADING_FILE_FROM_S3_ERROR);
    }

    @Test
    public void getFileInputStreamFromS3InvalidParameter() {
        UUID mock = UUID.randomUUID();
        when(engineDocumentDataMock.findById(mock)).thenReturn(Optional.empty());
        Throwable e = Assertions.assertThrows(TechpubsException.class, () -> engineDocAdminAppMock.getFileInputStreamFromS3(String.valueOf(mock)));
        assertEquals(((TechpubsException) e).getTechpubsAppError(), TechpubsException.TechpubsAppError.INVALID_PARAMETER);
    }

    @Test
    public void getFileInputStreamFromS3ForAValidFile() throws TechpubsException, FileNotFoundException {
        UUID mock = UUID.randomUUID();

        when(engineDocumentDataMock.findById(any())).thenReturn(Optional.of(engineDocumentEntityMock));
        when(amazonS3ClientFactoryMock.getS3Client()).thenReturn(amazonS3ClientMock);
        when(amazonS3ClientMock.getObject(any())).thenReturn(S3ObjectMock);

        Path path = Paths.get("src/test/resources/test.txt");
        FileInputStream fis = new FileInputStream(path.toFile());
        when(S3ObjectMock.getObjectContent()).thenReturn(new S3ObjectInputStream(fis, null));

        Map<String, Object> actualResult = engineDocAdminAppMock.getFileInputStreamFromS3(String.valueOf(mock));
        FileInputStream fis2 = (FileInputStream) actualResult.get("stream");

        assertEquals(engineDocumentEntityMock.getFileName(), actualResult.get("filename"));
        assertEquals(fis, fis2);
    }

    @Test
    public void getEngineDocumentListForExcelDownloadWhenDocumentAvailable() throws TechpubsException {
        engineDocumentEntityListMock = new ArrayList<>();
        engineDocumentEntityListMock.add(engineDocumentEntityMock);

        when(engineDocumentDataMock.findByDeleted(isA(Boolean.class))).thenReturn(Optional.of(engineDocumentEntityListMock));

        List<EngineDocumentExcelDownloadDTO> engineDocumentExcelDownloadDTOList = engineDocAdminAppMock.getEngineDocumentListForExcelDownload();
        assertEquals(engineDocumentExcelDownloadDTOList, engineDocumentExcelDownloadDTOList);
    }

    @Test
    public void getEngineDocumentListForExcelDownloadReturnsDataNotFoundErrorWhenNoEngineDocumentEntityListNotAvailable() throws TechpubsException {
        engineDocumentEntityListMock = new ArrayList<>();

        when(engineDocumentDataMock.findByDeleted(isA(Boolean.class))).thenReturn(Optional.empty());

        Throwable e = Assertions.assertThrows(TechpubsException.class, () -> engineDocAdminAppMock.getEngineDocumentListForExcelDownload());
        assertEquals(((TechpubsException) e).getTechpubsAppError(), TechpubsAppError.DATA_NOT_FOUND);
    }

    @Test
    public void whenEngineDocumentExcelDownloadDTOListIsEmptyThenReturnsNull() throws ExcelException, TechpubsException {
        when(engineDocumentDataMock.findByDeleted(isA(Boolean.class))).thenReturn(Optional.empty());

        try {
            engineDocAdminAppMock.downloadEngineDocuments();
        } catch (TechpubsException e) {
            assertEquals(e.getTechpubsAppError(), TechpubsAppError.DATA_NOT_FOUND);
        }
    }

    @Test
    public void aFileWithBytesReturnedWhenDownloadEngineDocuments() throws ExcelException, TechpubsException {
        engineDocumentEntityListMock = new ArrayList<>();
        engineDocumentEntityListMock.add(engineDocumentEntityMock);

        ExcelSheet excelSheet = ExcelMaker.buildExcelSheet(engineDocumentExcelDownloadDTOListMock);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        FileWithBytes fileDownload = new FileWithBytes(os.toByteArray(), excelSheet.getFilename());
        when(engineDocumentDataMock.findByDeleted(isA(Boolean.class))).thenReturn(Optional.of(engineDocumentEntityListMock));

        engineDocAdminAppMock.downloadEngineDocuments();

        assertThat(fileDownload.getFileName().equals(excelSheet.getFilename()));
    }

    @Test
    public void hardDeleteEngineDocumentSuccessfullyDeletesIfEngineDocumentIsPresent()
        throws TechpubsException {
        UUID mock = UUID.randomUUID();

        when(engineDocumentDataMock.findById(mock)).thenReturn(Optional.of(engineDocumentEntityMock));

        when(amazonS3ClientFactoryMock.getS3Client()).thenReturn(amazonS3ClientMock);

        EngineDocumentEntity response = engineDocAdminAppMock.hardDeleteEngineDocument(String.valueOf(mock));

        assertEquals(engineDocumentEntityMock, response);
    }

    @Test
    public void hardDeleteEngineDocumentReturnsUnableToFindEngineDocument(){
        UUID mock = UUID.randomUUID();

        when(engineDocumentDataMock.findById(isA(UUID.class))).thenReturn(Optional.empty());

        Throwable e = Assertions.assertThrows(TechpubsException.class,
            () -> engineDocAdminAppMock.hardDeleteEngineDocument(String.valueOf(mock)));
        assertEquals(((TechpubsException) e).getTechpubsAppError(),
            TechpubsException.TechpubsAppError.INVALID_PARAMETER);
    }

    @Test
    public void getEngineDocumentByIdIfEngineDocumentAvailable() throws TechpubsException, FileNotFoundException {
        UUID mock = UUID.randomUUID();

        when(engineDocumentDataMock.findById(any())).thenReturn(Optional.of(engineDocumentEntityMock));
        EngineDocumentByIdReachDTO response  = engineDocAdminAppMock.getEngineDocumentById(String.valueOf(mock));

        assertEquals(engineDocumentEntityMock.getId().toString(), response.getDocumentId());
    }

    @Test
    public void getEngineDocumentByIdPurposeFail() throws TechpubsException, FileNotFoundException {
        UUID mock = UUID.randomUUID();

        when(engineDocumentDataMock.findById(any())).thenReturn(Optional.empty());

        Throwable e = Assertions.assertThrows(TechpubsException.class,
            () -> engineDocAdminAppMock.getEngineDocumentById(String.valueOf(mock)));
        assertEquals(((TechpubsException) e).getTechpubsAppError(),
            TechpubsAppError.INTERNAL_ERROR);
    }
}

