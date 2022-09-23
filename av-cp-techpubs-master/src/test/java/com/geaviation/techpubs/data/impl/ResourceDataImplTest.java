//TODO we need to rethink these tess
//package com.geaviation.techpubs.data.impl;
//
//import com.geaviation.techpubs.data.util.DataConstants;
//import com.geaviation.techpubs.models.DocumentInfoModel;
//import com.geaviation.techpubs.models.ProgramItemModel;
//import com.geaviation.techpubs.models.ResourceMetaDataModel;
//import org.apache.commons.io.FileUtils;
//import org.dom4j.QName;
//import org.dom4j.tree.DefaultAttribute;
//import org.dom4j.tree.DefaultElement;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Value;
//
//import org.springframework.stereotype.Component;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.io.File;
//import java.io.IOException;
//import java.lang.reflect.Field;
//
//import static com.geaviation.techpubs.data.test.util.TestConstants.testNfsUrl;
//import static junit.framework.TestCase.assertTrue;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.mockito.Matchers.anyString;
//import static org.mockito.Mockito.when;
//
////@SpringBootTest()
//@ActiveProfiles("test")
//@RunWith(SpringRunner.class)
//@Component
//public class ResourceDataImplTest {
//
//  @Value("${PROGRAM.REPOSITORYBASE}")
//  private String repoBase;
//
//  @Mock
//  private ProgramItemModel pgmItem;
//
//  @Mock
//  private DefaultElement mockTOCData;
//
//  @Mock
//  private DefaultElement mockManual;
//
//
//  private ResourceDataImpl resourceData;
//
//  private DefaultElement mockDoc;
//
//  @Before
//  public void init() throws Exception {
//    MockitoAnnotations.initMocks(this);
//    resourceData = new ResourceDataImpl();
//    Field resourceDataField = ResourceDataImpl.class.getDeclaredField("baseUIUrl");
//    resourceDataField.setAccessible(true);
//    resourceDataField.set(resourceData, "http://localhost:3000");
//
//    mockDoc = new DefaultElement("figure");
//    mockDoc.add(new DefaultAttribute(new QName("toc"), "71-00-00-010-941A-D POWER PLANT ASSEMBLY"));
//    mockDoc
//        .add(new DefaultAttribute(new QName("title"), "71-00-00-010-941A-D POWER PLANT ASSEMBLY"));
//    mockDoc.add(new DefaultAttribute(new QName("revdate"), "20181210"));
//    mockDoc.add(new DefaultAttribute(new QName("revnbr"), "001"));
//    mockDoc.add(new DefaultAttribute(new QName("file"), "ALL"));
//    mockDoc.add(new DefaultAttribute(new QName("key"), "PASSPORT20-A-71-00-00-010-941A-D"));
//    mockDoc.add(new DefaultAttribute(new QName("mfile"), "PASSPORT20-A-71-00-00-010-941A-D.htm"));
//    mockDoc.add(new DefaultAttribute(new QName("nodeid"), "11"));
//    mockDoc.add(new DefaultAttribute(new QName("parentnodeid"), "10"));
//    mockDoc.add(new DefaultAttribute(new QName("docnbr"), "gek112064"));
//
//    when(pgmItem.getProgramOnlineVersionLocation()).thenReturn(testNfsUrl + "gek112060/2.3");
//    when(pgmItem.getProgramLocation()).thenReturn(testNfsUrl + "gek112060/");
//    when(pgmItem.getTocRoot()).thenReturn(mockTOCData);
//    when(pgmItem.getProgramDocnbr()).thenReturn("gek112060");
//    when(mockTOCData.selectSingleNode(anyString())).thenReturn(mockManual);
//    when(mockManual.selectSingleNode(anyString())).thenReturn(mockDoc);
//  }
//
//  @After
//  public void cleanup() {
//
//  }
//
//  @Test
//  public void testPrepareWrappedResources() throws IOException {
//    String testDocUrl = "/services/techpubs/techdocs/testID/pdf?type=fh";
//
//    DocumentInfoModel testDocModel = new DocumentInfoModel();
//    testDocModel.setTitle("Human Factors");
//    testDocModel.setType("FH");
//    testDocModel.setResourceUri("/techpubs/techdocs/testID?type=fh");
//    testDocModel.setDocumentsUri("/techpubs/techdocs/testID/associated?type=fh");
//
//    String result = resourceData
//        .prepareWrappedResource(testDocUrl, "application/pdf", testDocModel);
//
//    String expected = fileNameToStringContents(testNfsUrl
//        + "testFiles/expected/testPreparedWrappedResourcesExpected.htm");
//
//    assertEquals(expected, result);
//  }
//
//  @Test
//  public void testGetLogoHonda() throws IOException {
//    byte[] result = resourceData.getLogo("gehonda");
//    String resultString = new String(result);
//    String expected = fileNameToStringContents(
//        testNfsUrl + "testFiles/expected/graphics/hondalogo.png");
//
//    assertEquals(expected, resultString);
//  }
//
//  @Test
//  public void testGetLogoOther() throws IOException {
//    byte[] result = resourceData.getLogo("CWC");
//    String resultString = new String(result);
//
//    String expected = fileNameToStringContents(testNfsUrl + "testFiles/expected/graphics/logo.png");
//
//    assertEquals(expected, resultString);
//  }
//
//  @Test
//  public void testGetHTMLResourceNameHTML() {
//    DefaultElement mockDoc = new DefaultElement("mfmatr");
//    mockDoc.add(new DefaultAttribute(new QName("title"), "Table Of Content"));
//    mockDoc.add(new DefaultAttribute(new QName("key"), "EIPC_TOC"));
//    mockDoc.add(new DefaultAttribute(new QName("nodeid"), "7"));
//    mockDoc.add(new DefaultAttribute(new QName("parentnodeid"), "2"));
//    mockDoc.add(new DefaultAttribute(new QName("docnbr"), "gek112064"));
//
//    when(pgmItem.getProgramOnlineVersionLocation()).thenReturn(testNfsUrl + "gek112060/2.3");
//    when(pgmItem.getTocRoot()).thenReturn(mockTOCData);
//    when(pgmItem.getProgramDocnbr()).thenReturn("gek112060");
//    when(mockTOCData.selectSingleNode(anyString())).thenReturn(mockManual);
//    when(mockManual.selectSingleNode(anyString())).thenReturn(mockDoc);
//
//    ResourceMetaDataModel result = resourceData.getResourceNameTD(pgmItem, "gek112064", null,
//        "EIPC_TOC.htm", "low", false);
//
//    assertEquals("2", result.getContentid());
//    assertEquals("htm", result.getFileExtension());
//    assertEquals("EIPC_TOC.htm", result.getFileName());
//    assertEquals("gek112064", result.getManual());
//    assertEquals("gek112060", result.getProgram());
//    assertEquals("Table Of Content", result.getTitle());
//  }
//
//  @Test
//  public void testGetHTMLResourceNameCortona3D() {
//    ResourceMetaDataModel result = resourceData.getResourceNameTD(pgmItem, "gek112064", null,
//        "PASSPORT20-A-71-00-00-010-941A-D.htm", "low", false);
//
//    assertEquals("10", result.getContentid());
//    assertEquals("cortona3d", result.getFileExtension());
//    assertEquals("PASSPORT20-A-71-00-00-010-941A-D.htm", result.getFileName());
//    assertEquals("gek112064", result.getManual());
//    assertEquals("gek112060", result.getProgram());
//    assertEquals("71-00-00-010-941A-D POWER PLANT ASSEMBLY", result.getTitle());
//  }
//
//  @Test
//  public void testGetCortonaResourceTD() throws IOException {
//    byte[] resultByteArray = resourceData.getCortonaResourceTD(pgmItem, "gek112064",
//        "PASSPORT20-A-71-00-00-010-941A-D.htm");
//
//    String resultString = new String(resultByteArray);
//
//    String expected = fileNameToStringContents(testNfsUrl
//        + "testFiles/expected/getCortonaResourceTDExpected.cortona3d");
//
//    assertEquals(expected, resultString);
//  }
//
//
//  @Test
//  public void testCortonaCheckSD1000() {
//    when(pgmItem.getProgramOnlineVersionLocation()).thenReturn(testNfsUrl + "gek112060/2.3");
//
//    assertTrue(
//        resourceData.CortonaCheck(pgmItem, "gek112064", "PASSPORT20-A-71-00-00-010-941A-D.htm"));
//  }
//
//  @Test
//  public void testCortonaCheckATASpec() {
//    when(pgmItem.getProgramOnlineVersionLocation()).thenReturn(testNfsUrl + "gek112090/2.9");
//
//    assertTrue(resourceData.CortonaCheck(pgmItem, "gek112092", "71-00-00-01.htm"));
//  }
//
//  @Test
//  public void testCortonaCheckHtmNoCortona() {
//    when(pgmItem.getProgramOnlineVersionLocation()).thenReturn(testNfsUrl + "gek112060/2.3");
//
//    assertFalse(resourceData.CortonaCheck(pgmItem, "gek112063", "ESM_LOA.htm"));
//  }
//
//  @Test
//  public void testCortonaCheckHtmNoFile() {
//    when(pgmItem.getProgramOnlineVersionLocation()).thenReturn(testNfsUrl + "gek112060/2.3");
//
//    assertFalse(resourceData.CortonaCheck(pgmItem, "gek112064", "ESM_LOA2.htm"));
//  }
//  public void testCortonaCheckPdf() {
//    when(pgmItem.getProgramOnlineVersionLocation()).thenReturn(testNfsUrl + "gek112080/10.1");
//
//    assertFalse(resourceData.CortonaCheck(pgmItem,"gek112080","CF34-10E_PPBM_GEK_112771.pdf"));
//  }
//
//  @Test
//  public void testGetBinaryResourceTD() throws IOException {
//    byte[] resultByteArray = resourceData.getBinaryResourceTD(pgmItem, "gek112063",
//        "/images/CloseSection.jpg");
//    String resultString = new String(resultByteArray);
//
//    String expected = fileNameToStringContents(testNfsUrl
//        + "testFiles/expected/graphics/getBinaryResourceTDExpected.jpg");
//
//    assertEquals(expected, resultString);
//  }
//
//  @Test
//  public void testGetHTMLResourceSummaryTDNoSummaryAvailable() throws IOException {
//
//    when(pgmItem.getProgramOnlineVersionLocation()).thenReturn(testNfsUrl + "gek112060/2.3");
//    when(pgmItem.getTocRoot()).thenReturn(mockTOCData);
//    when(pgmItem.getProgramDocnbr()).thenReturn("gek112060");
//    when(mockTOCData.selectSingleNode(anyString())).thenReturn(mockDoc);
//
//    String result = new String(resourceData.getHTMLResourceSummaryTD(pgmItem, "gek112064",
//        "PASSPORT20-A-71-00-00-010-941A-D.htm"));
//
//    String expected = fileNameToStringContents(
//        testNfsUrl + "testFiles/expected/fileNotAvailable.htm");
//
//    assertEquals(expected, result);
//  }
//
//  @Test
//  public void testGetHTMLResourceSummaryTDNoFileAvailable() throws IOException {
//    when(pgmItem.getProgramOnlineVersionLocation()).thenReturn(testNfsUrl + "gek112060/2.3");
//    when(pgmItem.getTocRoot()).thenReturn(mockTOCData);
//    when(pgmItem.getProgramDocnbr()).thenReturn("gek112060");
//    when(mockTOCData.selectSingleNode(anyString())).thenReturn(null);
//
//    String result = new String(resourceData.getHTMLResourceSummaryTD(pgmItem, "gek112064",
//        "nonexistantFile.htm"));
//
//    String expected = fileNameToStringContents(
//        testNfsUrl + "testFiles/expected/fileNotAvailable.htm");
//
//    assertEquals(expected, result);
//  }
//
//  @Test
//  public void testGetStylesheet() throws IOException {
//    String result = new String(resourceData.getStylesheet("s1000d.css"));
//    String expected = fileNameToStringContents(
//        testNfsUrl + "testFiles/expected/styleSheetExpected.css");
//
//    assertEquals(expected, result);
//  }
//
//  public static class GetHTMLResourceTDTest {
//
//    @Value("${PROGRAM.REPOSITORYBASE}")
//    private String repoBase;
//
//    @Mock
//    private ProgramItemModel pgmItem;
//
//    @Mock
//    private DefaultElement mockTOCData;
//
//    @Mock
//    private DefaultElement mockManual;
//
//
//    private ResourceDataImpl resourceData;
//
//    private static DefaultElement mockDoc = null;
//
//    @Before
//    public void init() throws Exception {
//      MockitoAnnotations.initMocks(this);
//      resourceData = new ResourceDataImpl();
//      when(mockTOCData.selectSingleNode(anyString())).thenReturn(mockManual);
//
//      Field resourceDataField = ResourceDataImpl.class.getDeclaredField("baseUIUrl");
//      resourceDataField.setAccessible(true);
//      resourceDataField.set(resourceData, "http://localhost:3000");
//
//      mockDoc = new DefaultElement("mfmatr");
//      mockDoc.add(new DefaultAttribute(new QName("title"), "Table Of Content"));
//      mockDoc.add(new DefaultAttribute(new QName("key"), "EIPC_TOC"));
//      mockDoc.add(new DefaultAttribute(new QName("nodeid"), "7"));
//      mockDoc.add(new DefaultAttribute(new QName("parentnodeid"), "2"));
//      mockDoc.add(new DefaultAttribute(new QName("docnbr"), "gek112064"));
//    }
//
//    @Test
//    public void whenTargetNullAndTheUserIsNotExplicitlyEnabledToViewTheFileAndAFileIsInTheOnlineLocationWithAMatchingFilenameThenGetHTMLResourceTDShouldReturnThatFile
//        () throws IOException {
//      mockDoc.add(new DefaultAttribute(new QName("file"), "EIPC_TOC.htm"));
//
//      when(pgmItem.getProgramOnlineVersionLocation()).thenReturn(testNfsUrl + "gek112060/2.3");
//      when(pgmItem.getProgramLocation()).thenReturn(testNfsUrl + "gek112060/");
//      when(pgmItem.getTocRoot()).thenReturn(mockTOCData);
//      when(pgmItem.getProgramDocnbr()).thenReturn("gek112060");
//      when(mockTOCData.selectSingleNode(anyString())).thenReturn(mockManual);
//      when(mockManual.selectSingleNode(anyString())).thenReturn(mockDoc);
//
//      byte[] resultByteArray = resourceData.getHTMLResourceTD(pgmItem, "gek112064",
//          "EIPC_TOC.htm", "low", false, false);
//      String resultString = new String(resultByteArray);
//
//      String expected = fileNameToStringContents(testNfsUrl
//          + "testFiles/expected/EIPC_TOC_Expected.htm");
//
//      assertEquals(expected, resultString);
//    }
//
//    @Test
//    public void whenTargetNullAndTheUserIsNotExplicitlyEnabledToViewTheFileAndAFileIsNotInTheOnlineLocationWithAMatchingFilenameThenGetHTMLResourceTDShouldReturnTheFileFromTheProgramLocation
//        () throws IOException {
//      mockDoc.add(new DefaultAttribute(new QName("file"), "EIPC_TOC_2.htm"));
//
//      when(pgmItem.getProgramOnlineVersionLocation()).thenReturn(testNfsUrl + "gek112060/2.3");
//      when(pgmItem.getProgramLocation()).thenReturn(testNfsUrl + "gek112060/");
//      when(pgmItem.getProgramProgramLocation()).thenReturn(testNfsUrl + "gek112060/program");
//      when(pgmItem.getTocRoot()).thenReturn(mockTOCData);
//      when(pgmItem.getProgramDocnbr()).thenReturn("gek112060");
//      when(mockTOCData.selectSingleNode(anyString())).thenReturn(mockManual);
//      when(mockManual.selectSingleNode(anyString())).thenReturn(mockDoc);
//
//      byte[] resultByteArray = resourceData.getHTMLResourceTD(pgmItem, "gek112064", "EIPC_TOC_2.htm", "low", false, false);
//      String resultString = new String(resultByteArray);
//
//      String expected = fileNameToStringContents(testNfsUrl
//          + "testFiles/expected/EIPC_TOC_2_Expected.htm");
//
//      assertEquals(expected, resultString);
//    }
//
//    @Test
//    public void whenTargetNullAndTheFileRequestedIsACortonaFileThenGetHTMLResourceTDShouldReturnTheCortonaFile
//        () throws IOException {
//      mockDoc.add(new DefaultAttribute(new QName("file"), "PASSPORT20-A-71-00-00-010-941A-D.htm"));
//
//      when(pgmItem.getProgramOnlineVersionLocation()).thenReturn(testNfsUrl + "gek112060/2.3");
//      when(pgmItem.getProgramLocation()).thenReturn(testNfsUrl + "gek112060/");
//      when(pgmItem.getTocRoot()).thenReturn(mockTOCData);
//      when(pgmItem.getProgramDocnbr()).thenReturn("gek112060");
//      when(mockTOCData.selectSingleNode(anyString())).thenReturn(mockManual);
//      when(mockManual.selectSingleNode(anyString())).thenReturn(mockDoc);
//
//      byte[] resultByteArray = resourceData.getHTMLResourceTD(pgmItem, "gek112064",
//          "PASSPORT20-A-71-00-00-010-941A-D.htm", "low", false, false);
//      String resultString = new String(resultByteArray);
//
//      String expected = fileNameToStringContents(testNfsUrl
//          + "testFiles/expected/getCortonaResourceTDExpected.cortona3d");
//
//      assertEquals(expected, resultString);
//    }
//
//    @Test
//    public void whenTargetNullAndTheUserIsNotEnabledToViewTheFileAndTheRequestedFileDoesNotExistInTheOnlineOrProgramLocationThenGetHTMLResourceTDShouldReturnTheNoContentAvailableDocument
//        () throws IOException {
//      mockDoc.add(new DefaultAttribute(new QName("file"), "EIPC_TOC_3.htm"));
//
//      when(pgmItem.getProgramOnlineVersionLocation()).thenReturn(testNfsUrl + "gek112060/2.3");
//      when(pgmItem.getProgramLocation()).thenReturn(testNfsUrl + "gek112060/");
//      when(pgmItem.getTocRoot()).thenReturn(mockTOCData);
//      when(pgmItem.getProgramDocnbr()).thenReturn("gek112060");
//      when(mockTOCData.selectSingleNode(anyString())).thenReturn(mockManual);
//      when(mockManual.selectSingleNode(anyString())).thenReturn(mockDoc);
//
//      byte[] resultByteArray = resourceData.getHTMLResourceTD(pgmItem, "gek112064", "EIPC_TOC_3.htm", "low", false, false);
//      String resultString = new String(resultByteArray);
//
//      String expected = fileNameToStringContents(testNfsUrl
//          + "testFiles/expected/fileNotAvailable.htm");
//
//      assertEquals(expected, resultString);
//    }
//
//    @Test
//    public void whenTargetNullAndTheUserIsEnabledToViewTheFileAndTheRequestedFileDoesNotExistInTheOnlineOrProgramLocationsWithAMatchingFilenameThenGetHTMLResourceTDShouldReturnTheNoContentAvailableDocument
//        () throws IOException {
//      mockDoc.add(new DefaultAttribute(new QName("file"), "EIPC_TOC_3.htm"));
//
//      when(pgmItem.getProgramOnlineVersionLocation()).thenReturn(testNfsUrl + "gek112060/2.3");
//      when(pgmItem.getProgramLocation()).thenReturn(testNfsUrl + "gek112060/");
//      when(pgmItem.getTocRoot()).thenReturn(mockTOCData);
//      when(pgmItem.getProgramDocnbr()).thenReturn("gek112060");
//      when(mockTOCData.selectSingleNode(anyString())).thenReturn(mockManual);
//      when(mockManual.selectSingleNode(anyString())).thenReturn(mockDoc);
//
//      byte[] resultByteArray = resourceData.getHTMLResourceTD(pgmItem, "gek112064", "EIPC_TOC_3.htm", "low", false, true);
//      String resultString = new String(resultByteArray);
//
//      String expected = fileNameToStringContents(testNfsUrl
//          + "testFiles/expected/fileNotAvailable.htm");
//
//      assertEquals(expected, resultString);
//    }
//
//    @Test
//    public void whenTargetNullAndTheUserIsEnabledToViewTheFullPageProcedureAndTheFileIsInTheOnlineLocationThenGetHTMLResourceTDShouldReturnTheFileFromTheFullPageProcedureDirectory
//        () throws IOException {
//      mockDoc.add(new DefaultAttribute(new QName("file"), "EIPC_TOC.htm"));
//
//      when(pgmItem.getProgramOnlineVersionLocation()).thenReturn(testNfsUrl + "gek112060/2.3");
//      when(pgmItem.getProgramLocation()).thenReturn(testNfsUrl + "gek112060/");
//      when(pgmItem.getTocRoot()).thenReturn(mockTOCData);
//      when(pgmItem.getProgramDocnbr()).thenReturn("gek112060");
//      when(mockTOCData.selectSingleNode(anyString())).thenReturn(mockManual);
//      when(mockManual.selectSingleNode(anyString())).thenReturn(mockDoc);
//
//      byte[] resultByteArray = resourceData.getHTMLResourceTD(pgmItem, "gek112064",
//          "EIPC_TOC.htm", "low", false, true);
//      String resultString = new String(resultByteArray);
//
//      String expected = fileNameToStringContents(testNfsUrl
//          + "testFiles/expected/EIPC_TOC_SMM_Expected.htm");
//
//      assertEquals(expected, resultString);
//    }
//
//
//    @Test
//    public void whenTargetNullAndThePageblkTypeIsSMMAndTheUserIsEnabledToViewTheFileAndAFileIsNotInTheOnlineLocationWithAMatchingFilenameThenGetHTMLResourceTDShouldReturnTheFileFromTheFullPageDirectoryInTheProgramLocation
//        () throws IOException {
//      mockDoc.add(new DefaultAttribute(new QName("file"), "EIPC_TOC_2.htm"));
//
//      when(pgmItem.getProgramOnlineVersionLocation()).thenReturn(testNfsUrl + "gek112060/2.3");
//      when(pgmItem.getProgramProgramLocation()).thenReturn(testNfsUrl + "gek112060/program");
//      when(pgmItem.getProgramLocation()).thenReturn(testNfsUrl + "gek112060/");
//      when(pgmItem.getTocRoot()).thenReturn(mockTOCData);
//      when(pgmItem.getProgramDocnbr()).thenReturn("gek112060");
//      when(mockTOCData.selectSingleNode(anyString())).thenReturn(mockManual);
//      when(mockManual.selectSingleNode(anyString())).thenReturn(mockDoc);
//
//      byte[] resultByteArray = resourceData.getHTMLResourceTD(pgmItem, "gek112064",
//          "EIPC_TOC_2.htm", "low", false, true);
//      String resultString = new String(resultByteArray);
//
//      String expected = fileNameToStringContents(testNfsUrl
//          + "testFiles/expected/EIPC_TOC_2_SMM_Expected.htm");
//
//      assertEquals(expected, resultString);
//    }
//
//  }
//
//
//  public static class GetHTMLResourceTDDeprecatedTest {
//    @Value("${PROGRAM.REPOSITORYBASE}")
//    private String repoBase;
//
//    @Mock
//    private ProgramItemModel pgmItem;
//
//    @Mock
//    private DefaultElement mockTOCData;
//
//    @Mock
//    private DefaultElement mockManual;
//
//
//    private ResourceDataImpl resourceData;
//
//
//    @Before
//    public void init() throws Exception {
//      MockitoAnnotations.initMocks(this);
//      resourceData = new ResourceDataImpl();
//      when(mockTOCData.selectSingleNode(anyString())).thenReturn(mockManual);
//
//      Field resourceDataField = ResourceDataImpl.class.getDeclaredField("baseUIUrl");
//      resourceDataField.setAccessible(true);
//      resourceDataField.set(resourceData, "http://localhost:3000");
//    }
//
//    @Test
//    public void whenTargetNullAndAFileIsInTheOnlineLocationWithAMatchingFilenameThenGetHTMLResourceTDShouldReturnThatFile
//        () throws IOException {
//      DefaultElement mockDoc = new DefaultElement("mfmatr");
//      mockDoc.add(new DefaultAttribute(new QName("title"), "Table Of Content"));
//      mockDoc.add(new DefaultAttribute(new QName("file"), "EIPC_TOC.htm"));
//      mockDoc.add(new DefaultAttribute(new QName("key"), "EIPC_TOC"));
//      mockDoc.add(new DefaultAttribute(new QName("nodeid"), "7"));
//      mockDoc.add(new DefaultAttribute(new QName("parentnodeid"), "2"));
//      mockDoc.add(new DefaultAttribute(new QName("docnbr"), "gek112064"));
//
//      when(pgmItem.getProgramOnlineVersionLocation()).thenReturn(testNfsUrl + "gek112060/2.3");
//      when(pgmItem.getProgramLocation()).thenReturn(testNfsUrl + "gek112060/");
//      when(pgmItem.getTocRoot()).thenReturn(mockTOCData);
//      when(pgmItem.getProgramDocnbr()).thenReturn("gek112060");
//      when(mockTOCData.selectSingleNode(anyString())).thenReturn(mockManual);
//      when(mockManual.selectSingleNode(anyString())).thenReturn(mockDoc);
//
//      byte[] resultByteArray = resourceData.getHTMLResourceTD(pgmItem, "gek112064",
//          null, "EIPC_TOC.htm", "low", false);
//      String resultString = new String(resultByteArray);
//
//      String expected = fileNameToStringContents(testNfsUrl
//          + "testFiles/expected/EIPC_TOC_Expected.htm");
//
//      assertEquals(expected, resultString);
//    }
//
//    @Test
//    public void whenTargetNullAndAFileIsNotInTheOnlineLocationWithAMatchingFilenameThenGetHTMLResourceTDShouldReturnTheFileFromTheProgramLocation
//        () throws IOException {
//      DefaultElement mockDoc = new DefaultElement("mfmatr");
//      mockDoc.add(new DefaultAttribute(new QName("title"), "Table Of Content"));
//      mockDoc.add(new DefaultAttribute(new QName("file"), "EIPC_TOC_2.htm"));
//      mockDoc.add(new DefaultAttribute(new QName("key"), "EIPC_TOC_2"));
//      mockDoc.add(new DefaultAttribute(new QName("nodeid"), "7"));
//      mockDoc.add(new DefaultAttribute(new QName("parentnodeid"), "2"));
//      mockDoc.add(new DefaultAttribute(new QName("docnbr"), "gek112064"));
//
//      String test = testNfsUrl;
//      when(pgmItem.getProgramOnlineVersionLocation()).thenReturn(testNfsUrl + "gek112060/2.3");
//      when(pgmItem.getProgramLocation()).thenReturn(testNfsUrl + "gek112060/");
//      when(pgmItem.getProgramProgramLocation()).thenReturn(testNfsUrl + "gek112060/program");
//      when(pgmItem.getTocRoot()).thenReturn(mockTOCData);
//      when(pgmItem.getProgramDocnbr()).thenReturn("gek112060");
//      when(mockTOCData.selectSingleNode(anyString())).thenReturn(mockManual);
//      when(mockManual.selectSingleNode(anyString())).thenReturn(mockDoc);
//
//      byte[] resultByteArray = resourceData.getHTMLResourceTD(pgmItem, "gek112064",
//          null, "EIPC_TOC_2.htm", "low", false);
//      String resultString = new String(resultByteArray);
//
//      String expected = fileNameToStringContents(testNfsUrl
//          + "testFiles/expected/EIPC_TOC_2_Expected.htm");
//
//      assertEquals(expected, resultString);
//    }
//
//    @Test
//    public void whenTargetNullAndTheFileRequestedIsACortonaFileThenGetHTMLResourceTDShouldReturnTheCortonaWithAMatchingFileName
//        () throws IOException {
//      DefaultElement mockDoc = new DefaultElement("mfmatr");
//      mockDoc.add(new DefaultAttribute(new QName("title"), "Table Of Content"));
//      mockDoc
//          .add(new DefaultAttribute(new QName("file"), "PASSPORT20-A-71-00-00-010-941A-D.htm"));
//      mockDoc.add(new DefaultAttribute(new QName("key"), "PASSPORT20-A-71-00-00-010-941A-D"));
//      mockDoc.add(new DefaultAttribute(new QName("nodeid"), "7"));
//      mockDoc.add(new DefaultAttribute(new QName("parentnodeid"), "2"));
//      mockDoc.add(new DefaultAttribute(new QName("docnbr"), "gek112064"));
//
//      when(pgmItem.getProgramOnlineVersionLocation()).thenReturn(testNfsUrl + "gek112060/2.3");
//      when(pgmItem.getProgramLocation()).thenReturn(testNfsUrl + "gek112060/");
//      when(pgmItem.getTocRoot()).thenReturn(mockTOCData);
//      when(pgmItem.getProgramDocnbr()).thenReturn("gek112060");
//      when(mockTOCData.selectSingleNode(anyString())).thenReturn(mockManual);
//      when(mockManual.selectSingleNode(anyString())).thenReturn(mockDoc);
//
//      byte[] resultByteArray = resourceData.getHTMLResourceTD(pgmItem, "gek112064",
//          null, "PASSPORT20-A-71-00-00-010-941A-D.htm", "low", false);
//      String resultString = new String(resultByteArray);
//
//      String expected = fileNameToStringContents(testNfsUrl
//          + "testFiles/expected/getCortonaResourceTDExpected.cortona3d");
//
//      assertEquals(expected, resultString);
//    }
//
//    @Test
//    public void whenTargetNullAndTheRequestedFileDoesNotExistInTheOnlineOrProgramLocationsWithAMatchingFilenameThenGetHTMLResourceTDShouldReturnTheNoContentAvailableDocument
//        () throws IOException {
//      DefaultElement mockDoc = new DefaultElement("mfmatr");
//      mockDoc.add(new DefaultAttribute(new QName("title"), "Table Of Content"));
//      mockDoc.add(new DefaultAttribute(new QName("file"), "EIPC_TOC_3.htm"));
//      mockDoc.add(new DefaultAttribute(new QName("key"), "EIPC_TOC_3"));
//      mockDoc.add(new DefaultAttribute(new QName("nodeid"), "7"));
//      mockDoc.add(new DefaultAttribute(new QName("parentnodeid"), "2"));
//      mockDoc.add(new DefaultAttribute(new QName("docnbr"), "gek112064"));
//
//      when(pgmItem.getProgramOnlineVersionLocation()).thenReturn(testNfsUrl + "gek112060/2.3");
//      when(pgmItem.getProgramLocation()).thenReturn(testNfsUrl + "gek112060/");
//      when(pgmItem.getTocRoot()).thenReturn(mockTOCData);
//      when(pgmItem.getProgramDocnbr()).thenReturn("gek112060");
//      when(mockTOCData.selectSingleNode(anyString())).thenReturn(mockManual);
//      when(mockManual.selectSingleNode(anyString())).thenReturn(mockDoc);
//
//      byte[] resultByteArray = resourceData.getHTMLResourceTD(pgmItem, "gek112064",
//          null, "EIPC_TOC_3.htm", "low", false);
//      String resultString = new String(resultByteArray);
//
//      String expected = fileNameToStringContents(testNfsUrl
//          + "testFiles/expected/fileNotAvailable.htm");
//
//      assertEquals(expected, resultString);
//    }
//  }
//
//  private static String fileNameToStringContents(String fileLocation) throws IOException {
//    return new String(FileUtils.readFileToByteArray(new File(fileLocation)));
//  }
//}