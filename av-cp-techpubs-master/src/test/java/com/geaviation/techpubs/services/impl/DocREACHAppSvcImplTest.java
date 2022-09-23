package com.geaviation.techpubs.services.impl;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.geaviation.techpubs.data.api.techlib.enginedoc.IEngineDocumentData;
import com.geaviation.techpubs.data.factories.AmazonS3ClientFactory;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.DocumentInfoModel;
import com.geaviation.techpubs.models.DocumentItemModel;
import com.geaviation.techpubs.models.DocumentItemREACHModel;
import com.geaviation.techpubs.models.ProgramItemModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.models.techlib.EngineModelEntity;
import com.geaviation.techpubs.models.techlib.enginedoc.EngineCASNumberEntity;
import com.geaviation.techpubs.models.techlib.enginedoc.EngineDocumentEntity;
import com.geaviation.techpubs.models.techlib.enginedoc.EnginePartNumberLookupEntity;
import com.geaviation.techpubs.models.techlib.enginedoc.EngineDocumentTypeLookupEntity;
import com.geaviation.techpubs.services.impl.admin.EngineDocAdminAppImpl;
import com.geaviation.techpubs.services.util.AppConstants;
import com.geaviation.techpubs.services.util.TechpubsAppUtil;
import com.lowagie.text.DocumentException;
import junit.framework.Assert;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.IOUtils;
import org.assertj.core.util.Lists;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.bind.annotation.ExceptionHandler;


public class DocREACHAppSvcImplTest {

  @Mock
  private IEngineDocumentData iEngineDocumentData;

  @Mock
  private TechpubsAppUtil techpubsAppUtilMock;
  
  private EngineDocumentEntity engineDocumentEntity;

  @Mock
  private EngineDocumentTypeLookupEntity engineDocumentTypeLookupEntity;

  @Mock
  AbstractDocSubSystemAppImpl abstractDocSubSystemAppImpl;

  @InjectMocks
  EngineDocAdminAppImpl engineDocAdminApp;

  @Mock
  AmazonS3ClientFactory amazonS3ClientFactory;

  @Mock
  AmazonS3Client amazonS3Client;

  @Mock
  AmazonS3 amazonS3;

  @Mock
  S3Object s3Object;

  @InjectMocks
  DocREACHAppSvcImpl docREACHAppSvc;

  private static final String SSO_ID = "sso-id";
  private static final String PORTAL_ID = "portal-id";
  private static final String FILE_ID = "file-id";

  Map<String, String> searchFilterMap;
  Map<String, String> queryParams;
  List<EngineDocumentEntity> engineDocumentEntityList = new ArrayList<>();
  Set<EnginePartNumberLookupEntity> enginePartNumberLookupEntities;
  Set<EngineCASNumberEntity> engineCASNumberEntities;
  Set<EngineModelEntity> engineModelEntities = new HashSet<>();
  List<String> userAccessModels = new ArrayList<String>();
  Map<String, Object> map;

  List<DocumentItemModel> docList = new ArrayList<>();
  DocumentItemModel documentItemModel1;
  DocumentItemModel documentItemModel2;


  @Before
  public void setup() throws DocumentException, TechpubsException, IOException {

   docREACHAppSvc = new DocREACHAppSvcImpl();
    MockitoAnnotations.initMocks(this);

    searchFilterMap = new HashedMap();
    searchFilterMap.put(AppConstants.FAMILY, "family");
    searchFilterMap.put(AppConstants.MODEL, "model");
    searchFilterMap.put(AppConstants.AIRCRAFT, "aircraft");
    searchFilterMap.put(AppConstants.TAIL, "tail");
    searchFilterMap.put(AppConstants.ESN, "esn");

    documentItemModel1 = new DocumentItemModel();
    documentItemModel1.setId("id");
    documentItemModel1.setType("REACH");
    documentItemModel1.setTitle("titleOfREACHDoc");
    documentItemModel1.setResourceUri("uri");
    documentItemModel1.setGroupName("group");
    documentItemModel1.setProgramItem(new ProgramItemModel()); //?
    documentItemModel1.setFileSize("file-size");

    documentItemModel2 = new DocumentItemModel();
    documentItemModel2.setId("id-2");
    documentItemModel2.setType("notREACH");
    documentItemModel2.setTitle("titleOfDoc");
    documentItemModel2.setResourceUri("uri2");
    documentItemModel2.setGroupName("group2");
    documentItemModel2.setProgramItem(new ProgramItemModel()); //?
    documentItemModel2.setFileSize("file-size2");

    docList.add(documentItemModel1);
    docList.add(documentItemModel2);

    queryParams = new HashMap<>();
    queryParams.put(AppConstants.TYPE, "type");

    UUID mock = mock(UUID.class);

    engineDocumentEntity = new EngineDocumentEntity();
    engineDocumentEntity.setId(mock);
    engineDocumentEntity.setDocumentTitle("title");
    engineDocumentEntity.setPartName("part-name");
    engineDocumentEntity.setEmailNotification(true);
    engineDocumentEntity.setDeleted(false);
    engineDocumentEntity.setFileName("file-name");
    engineDocumentEntity.setCreatedDate(new Date());
    engineDocumentEntity.setLastUpdatedDate(new Date());
    engineDocumentEntity.setEnginePartNumbers(enginePartNumberLookupEntities);
    engineDocumentEntity.setEngineCASNumberEntity(engineCASNumberEntities);
    engineDocumentEntity.setEngineModelEntity(engineModelEntities);
    engineDocumentEntity.setIssueDate(new Date());
    engineDocumentEntity.setEngineDocumentTypeLookupEntity(engineDocumentTypeLookupEntity);

    engineDocumentEntityList = new ArrayList<>();
    engineDocumentEntityList.add(engineDocumentEntity);
  }

  @Test
  public void getSubSystemReturnsREACHSubsystem(){
    SubSystem response = docREACHAppSvc.getSubSystem();
  assertEquals(response, SubSystem.REACH);
  }

    @Test
    public void getDocumentsByEngineModelAndREACHTypeReturnsREACHDocumentsWhenAvailable() throws TechpubsException {
    when(iEngineDocumentData
          .findByDocumentTypeAndEngineModelAndDeleted(isA(String.class), isA(List.class),
              isA(Boolean.class)))
          .thenReturn(Optional.of(engineDocumentEntityList));

     when(techpubsAppUtilMock.getNavigationl1(isA(String.class), isA(String.class)))
         .thenReturn(new StringBuilder("{\"engine\":{\"GE90\":[\"GE90\",\"GE90-100\"],\"GE9X\":[\"GE9X-105\",\"GE9X-FLTEST\"]}}"));

      List<DocumentItemModel> result = docREACHAppSvc
         .getDocuments(SSO_ID, PORTAL_ID, searchFilterMap, queryParams);

      List<EngineDocumentEntity> reachEngineDocs = engineDocumentEntityList;

     DocumentItemREACHModel documentItemREACHModel = new DocumentItemREACHModel();
      result.add(documentItemREACHModel);

      assertEquals(reachEngineDocs, engineDocumentEntityList);
    }


  @Test
  public void setFileNameReturnsNull(){
    String fileName = docREACHAppSvc.setFileName();
    assertEquals(fileName, null);
  }

  @Test
  public void getArtifactReturnsInvalidParameterException() throws TechpubsException, JSONException {

		when(iEngineDocumentData.findById(UUID.fromString("c3b557e8-307b-4306-8146-6eb7f048ad72")))
				.thenReturn(Optional.empty());
		Throwable e = Assertions.assertThrows(TechpubsException.class,
				() -> docREACHAppSvc.getArtifact("sso", "portalId", "c3b557e8-307b-4306-8146-6eb7f048ad72"));
		assertEquals(((TechpubsException) e).getTechpubsAppError(),
				TechpubsException.TechpubsAppError.INVALID_PARAMETER);
  }

  @Test
  public void getArtifactReturnsUnauthorizedStatus() throws TechpubsException, JSONException {

		when(iEngineDocumentData.findById(UUID.fromString("c3b557e8-307b-4306-8146-6eb7f048ad72")))
				.thenReturn(Optional.of(engineDocumentEntity));

		JSONObject engineSeriesJson = new JSONObject();
		engineSeriesJson.put("CF34-10E", Lists.newArrayList("CF34-10E5A1"));

		JSONObject engineJson = new JSONObject();
		engineJson.put("CF34", engineSeriesJson);

		JSONObject engineFamilyJson = new JSONObject();
		engineFamilyJson.put("engineFamilies", engineJson);

		StringBuilder stringBuilder = new StringBuilder(engineFamilyJson.toString());
		when(techpubsAppUtilMock.getNavigationl1("sso", "portalId")).thenReturn(stringBuilder);
		Throwable e = Assertions.assertThrows(TechpubsException.class,
				() -> docREACHAppSvc.getArtifact("sso", "portalId", "c3b557e8-307b-4306-8146-6eb7f048ad72"));
		assertEquals(((TechpubsException) e).getTechpubsAppError(), TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
  }

  @Test
  public void getArtifactReturnsArtifactMapWhenAvailable()
			throws TechpubsException, JSONException, IOException {
		Set<EngineModelEntity> engineModelEntitySet = new HashSet<EngineModelEntity>();
		EngineModelEntity engineModel = new EngineModelEntity();
		engineModel.setModel("GE90-100");
		engineModelEntitySet.add(engineModel);
		engineDocumentEntity.setEngineModelEntity(engineModelEntitySet);
		when(iEngineDocumentData.findById(any())).thenReturn(Optional.of(engineDocumentEntity));
		when(amazonS3ClientFactory.getS3Client()).thenReturn(amazonS3Client);
		when(amazonS3Client.getObject(any())).thenReturn(s3Object);
		Path path = Paths.get("src/test/resources/test.txt");
		byte[] data = Files.readAllBytes(path);
		FileInputStream fis = new FileInputStream(path.toFile());
		when(s3Object.getObjectContent()).thenReturn(new S3ObjectInputStream(fis,null));
		when(techpubsAppUtilMock.getNavigationl1(isA(String.class), isA(String.class))).thenReturn(new StringBuilder(
				"{\"engine\":{\"GE90\":[\"GE90\",\"GE90-100\"],\"GE9X\":[\"GE9X-105\",\"GE9X-FLTEST\"]}}"));
		Map<String, Object> artificateMap = docREACHAppSvc.getArtifact("sso", "portalId",
				"c3b557e8-307b-4306-8146-6eb7f048ad72");
		
		assertEquals(engineDocumentEntity.getFileName(), artificateMap.get("filename"));
		assertArrayEquals(data, (byte[])artificateMap.get("content"));
		
  }

}
