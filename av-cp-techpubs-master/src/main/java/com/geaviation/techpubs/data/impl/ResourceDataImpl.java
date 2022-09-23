package com.geaviation.techpubs.data.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geaviation.dss.service.common.exception.TechnicalException;
import com.geaviation.techpubs.config.S3Config;
import com.geaviation.techpubs.data.api.IResourceData;
import com.geaviation.techpubs.data.api.techlib.IBookcaseVersionData;
import com.geaviation.techpubs.data.api.techlib.ICortonaLookup;
import com.geaviation.techpubs.data.api.techlib.IPageblkLookupData;
import com.geaviation.techpubs.data.factories.AmazonS3ClientFactory;
import com.geaviation.techpubs.data.html.filter.BrowserSupportFilter;
import com.geaviation.techpubs.data.html.filter.CMMPartsFilter;
import com.geaviation.techpubs.data.html.filter.HTMLWriterFilter;
import com.geaviation.techpubs.data.html.filter.InjectDocumentSelectionsFilter;
import com.geaviation.techpubs.data.html.filter.InjectMetaDataFilter;
import com.geaviation.techpubs.data.html.filter.ReplaceStyleSheetFilter;
import com.geaviation.techpubs.data.html.filter.UpdateEmbedDocumentFilter;
import com.geaviation.techpubs.data.html.filter.UpdateResourceFilter;
import com.geaviation.techpubs.data.html.filter.UpdateTargetAnchorsFilter;
import com.geaviation.techpubs.data.s3.Util;
import com.geaviation.techpubs.data.util.DataConstants;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.DocumentInfoModel;
import com.geaviation.techpubs.models.DocumentInfoTDModel;
import com.geaviation.techpubs.models.ProgramItemModel;
import com.geaviation.techpubs.models.RelatedDocumentInfoModel;
import com.geaviation.techpubs.models.ResourceMetaDataModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.models.techlib.CortonaLookupEntity;
import com.geaviation.techpubs.models.techlib.dto.CortonaTargetDto;
import com.geaviation.techpubs.models.techlib.dto.PageblkLookupDto;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.TermQuery;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.bson.types.ObjectId;
import org.cyberneko.html.HTMLConfiguration;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.tree.DefaultAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.geaviation.techpubs.data.util.DataConstants.RESOURCE_DOC;
import static com.geaviation.techpubs.services.util.AppConstants.SMM_FULL_PAGE_DIRECTORY_SUFFIX;

@Component
@RefreshScope
public class ResourceDataImpl implements IResourceData {

  public static final String HTML_BIN_WRAPPER_HTM = "html/bin_wrapper.htm";
  public static final String NO_CHANGE = "no-change";

  List<String> cortonaBookcases = Stream.of("gek112059", "gek112060",
      "gek112090", "gek112110", "gek112121", "gek112865", "gek112865_lr",
      "gek114118", "gek114118_lr").collect(Collectors.toList());

  @Value("${techpubs.services.lazyLoadingImages}")
  private boolean lazyLoadingImages;

  @Value("${techpubs.services.CORTONA_3D_S3}")
  private boolean CORTONA_3D_S3;

  @Autowired
  private S3Config s3Config;

  @Autowired
  private AmazonS3ClientFactory amazonS3ClientFactory;

  @Autowired
  private AwsResourcesService awsResourcesService;

  @Autowired
  private MongoClient mongoClient;

  @Autowired
  private IResourceData iResourceData;

  @Autowired
  private IBookcaseVersionData iBookcaseVersionData;

  @Autowired
  private IPageblkLookupData iPageblkLookupData;

  @Autowired
  private ICortonaLookup iCortonaLookup;

  private static final Logger log = LogManager.getLogger(ResourceDataImpl.class);

  private static final String AUGMENTATIONS = "http://cyberneko.org/html/features/augmentations";
  private static final String BALANCETAGS = "http://cyberneko.org/html/features/balance-tags";
  private static final String FILTERS = "http://cyberneko.org/html/properties/filters";
  private static final String ELEMNAMES = "http://cyberneko.org/html/properties/names/elems";
  private static final String CORTONA_EXTENSION = "cortona3d";
  private static final String ATTRNAMES = "http://cyberneko.org/html/properties/names/attrs";
  private static final String MANS = "/mans/";
  public static final String ALERT_COVER = "alert-cover";
  public static final String DOC_NBR = "./*[@docnbr='";

  @Override
  @LogExecutionTime
  public String prepareWrappedResource(String embedDocument, String contentType,
      DocumentInfoModel documentInfo) {
    String sDocument = "";
    BufferedInputStream inStream = null;
    XMLDocumentFilter[] filters;

    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      ObjectMapper mapper = new ObjectMapper();
      String jsonDocInfo = mapper.writeValueAsString(documentInfo);

      HTMLConfiguration parser = new HTMLConfiguration();
      // Disable augmentations.
      parser.setFeature(AUGMENTATIONS, false);
      // Disable tag balancing.
      parser.setFeature(BALANCETAGS, false);
      // Leave element names as is
      parser.setProperty(ELEMNAMES, DataConstants.ELEMNAMES_MATCH);
      // Leave attribute names as is
      parser.setProperty(ATTRNAMES, DataConstants.NO_CHANGE);

      inStream = new BufferedInputStream(
          this.getClass().getClassLoader().getResourceAsStream(HTML_BIN_WRAPPER_HTM));
      filters = new XMLDocumentFilter[3];
      filters[0] = new UpdateEmbedDocumentFilter(embedDocument, contentType);
      filters[1] = new InjectMetaDataFilter(jsonDocInfo);
      filters[2] = new HTMLWriterFilter(baos, DataConstants.UTF_8);

      parser.setProperty(FILTERS, filters);
      XMLInputSource xis = new XMLInputSource(null, null, null, inStream, null);
      parser.parse(xis);
      baos.close();
      sDocument = baos.toString();
      inStream.close();
    } catch (JsonMappingException | JsonGenerationException e) {
      log.error(DataConstants.PREPARED_WRAPPED_BIN + "(JsonMappingException)", e);
      throw new TechnicalException(
          DataConstants.PREPARED_WRAPPED_BIN + " (JsonMappingException)", e);
    } catch (IOException e) {
      log.error(DataConstants.PREPARED_WRAPPED_BIN + "(" + embedDocument + ")", e);
      throw new TechnicalException(
          DataConstants.PREPARED_WRAPPED_BIN + "(" + embedDocument + ")", e);
    } finally {
      IOUtils.closeQuietly(inStream);
    }

    return sDocument;
  }

  @Override
  @LogExecutionTime
  public String getCMMParts(String publication, String fileId, List<String[]> partsList) {

    String sDocument = "";
    BufferedInputStream inStream = null;
    XMLDocumentFilter[] filters;

    HTMLConfiguration parser = new HTMLConfiguration();
    // Disable augmentations.
    parser.setFeature(AUGMENTATIONS, false);
    // Disable tag balancing.
    parser.setFeature(BALANCETAGS, false);
    // Leave element names as is
    parser.setProperty(ELEMNAMES, DataConstants.ELEMNAMES_MATCH);
    // Leave attribute names as is
    parser.setProperty(ATTRNAMES, DataConstants.NO_CHANGE);

    DocumentInfoModel documentInfo = new DocumentInfoModel();
    documentInfo.setType("CMM");
    documentInfo.setTitle("Part Number List for : " + publication);
    documentInfo
        .setResourceUri(DataConstants.RESOURCE_URI_CMM + DataConstants.RESOURCE_CMM + fileId
            + DataConstants.RESOURCE_DOCUMENTS_CMM_PARTS);
    documentInfo
        .setDocumentsUri(DataConstants.RESOURCE_URI_CMM + fileId
            + DataConstants.RESOURCE_DOCUMENTS_ASSOCIATED
            + DataConstants.TYPE_PARAM + DataConstants.RESOURCE_CMM_LOWER_CASE);

    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      ObjectMapper mapper = new ObjectMapper();
      String jsonDocInfo = mapper.writeValueAsString(documentInfo);

      inStream = new BufferedInputStream(
          this.getClass().getClassLoader().getResourceAsStream("html/CMMPartsList.htm"));
      filters = new XMLDocumentFilter[3];
      filters[0] = new InjectMetaDataFilter(jsonDocInfo);
      filters[1] = new CMMPartsFilter(publication, partsList);
      filters[2] = new HTMLWriterFilter(baos, DataConstants.UTF_8);
      parser.setProperty(FILTERS, filters);
      XMLInputSource xis = new XMLInputSource(null, null, null, inStream, null);
      parser.parse(xis);
      baos.close();
      sDocument = baos.toString();
      inStream.close();
    } catch (JsonMappingException e) {
      log.error(
          DataConstants.LOGGER_GETCMMPARTS + " (" + DataConstants.LOGGER_JSONEXCEPTION + ")",
          e);
      throw new TechnicalException(
          DataConstants.LOGGER_GETCMMPARTS + " (" + DataConstants.LOGGER_JSONEXCEPTION + ")",
          e);
    } catch (IOException e) {
      log.error(DataConstants.LOGGER_GETCMMPARTS + " (" + publication + ")", e);
      throw new TechnicalException(
          DataConstants.LOGGER_GETCMMPARTS + " (" + publication + ")", e);
    } finally {
      IOUtils.closeQuietly(inStream);
    }
    return sDocument;
  }

  @Override
  @LogExecutionTime
  public Map<String, Object> getArtifact(String fileId) {
    Map<String, Object> artMap = null;
    MongoDatabase mongoDb = mongoClient.getDatabase(DataConstants.DB_TECHPUBS);
    GridFSDownloadStream downloadStream = null;
    try {
      ObjectId id = new ObjectId(fileId);
      GridFSBucket bucket = GridFSBuckets.create(mongoDb, DataConstants.COLLECTION_ARTIFACTS);
      GridFSFile file = bucket.find(new org.bson.Document(DataConstants.ARTIFACTS_ID, id))
          .first();
      downloadStream = bucket.openDownloadStream(id);
      int fileLength = (int) downloadStream.getGridFSFile().getLength();
      byte[] rtn = new byte[fileLength];
      // Note: read method reads one chunk at a time.
      int bytesRead = downloadStream.read(rtn);
      int bytesLeft = fileLength - bytesRead;
      if (bytesLeft > 0) {
        int chunkSize = bytesRead;
        int offset = bytesRead;
        do {
          int readSize = chunkSize < bytesLeft ? chunkSize : bytesLeft;
          bytesRead = downloadStream.read(rtn, offset, readSize);
          bytesLeft -= bytesRead;
          offset += bytesRead;
        } while (bytesLeft > 0);
      }
      artMap = new HashMap<>();
      artMap.put(DataConstants.METADATA_FILENAME, file.getFilename());
      Map<String, Object> metaData = new HashMap<>();
      for (Map.Entry<String, Object> entry : file.getMetadata().entrySet()) {
        metaData.put(entry.getKey(), entry.getValue());
      }
      artMap.put(DataConstants.METADATA, metaData);
      artMap.put("content", rtn);
    } catch (Exception e) {
      log.error(DataConstants.LOGGER_GETARTIFACT + " (" + fileId + ")", e);
      throw new TechnicalException(DataConstants.LOGGER_GETARTIFACT + " (" + fileId + ")", e);
    }
    finally {
      downloadStream.close();
    }
    return artMap;
  }

  @Override
  @LogExecutionTime
  public byte[] getLogo(String portalId) {
    byte[] rtn = null;
    String logoResource = ("gehonda".equalsIgnoreCase(portalId) ? "graphics/hondalogo.png"
        : "graphics/logo.png");

    try {
      rtn = IOUtils
          .toByteArray(this.getClass().getClassLoader().getResourceAsStream(logoResource));
    } catch (IOException e) {
      log.error(DataConstants.LOGO_LOGGER + logoResource + ")", e);
      throw new TechnicalException(DataConstants.LOGO_LOGGER + logoResource + ")", e);
    }

    return rtn;
  }

  public List<Document> getDocumentsByTargetIndex(ProgramItemModel programItem, String manual,
      String target) {
    List<Document> documents = new ArrayList<>();
    if (target != null) {
      documents = lookupTarget(programItem, manual, target);
    }

    return documents;

  }


  public String getFilenameByBandwidthValue(String filename, ProgramItemModel programItem,
      String bookKey, String bandwidth, boolean multiBrowserDocumentRequired) {

    // Determine bandwidth file based on filename parameter...filename will
    // always be high bandwidth
    try {
      Element manualElement = (Element) programItem.getTocRoot()
          .selectSingleNode(DOC_NBR + bookKey + "']");
      // xpath = .//*[@file='m-71-00-00-01.htm' or @mfile='m-71-00-00-01.htm']
      String xpath = ".//*[@file='" + filename + "' or " + "@mfile='" + filename + "']";
      Element docElement = (Element) manualElement.selectSingleNode(xpath);
      if (docElement != null && ("low".equalsIgnoreCase(bandwidth) || (
          multiBrowserDocumentRequired
              && !"Y".equalsIgnoreCase(
              manualElement.attributeValue(DataConstants.DOCUMENTS_MULTIBROWSER))))) {
        filename = (docElement.attributeValue(DataConstants.MFILE) != null
            ? docElement.attributeValue(DataConstants.MFILE)
            : docElement.attributeValue(DataConstants.METADATA_FILE));
      }
      return filename;
    } catch (NullPointerException ex) {
      log.debug(
          "Bandwidth file information was not found. Default bandwidth file used for " + filename);
      return filename;
    }
  }

  public byte[] getDocumentNotFoundFile() {
    return displayDocumentNotFound().getBytes();
  }


  @Override
  @LogExecutionTime
  public byte[] getHTMLResourceTD(ProgramItemModel programItem, String manual,
      String filename,
      String bandwidth, boolean multiBrowserDocumentRequired,
      boolean userIsExplicitlyEnabledToViewFile) throws TechpubsException {

    if (CORTONA_3D_S3) {
      // if the filename is a cortona resource
      if (filename.contains("cortona3d")) {
        byte[] cortonaByte = getBinaryResourceTD(programItem, manual, filename);

        if (cortonaByte != null) {
          return cortonaByte;
        }
      }
    }
     // check to see if cortona3d file exists based off of htm name and return it if it exists
    else if (filename != null) {
      byte[] cortonaByte = getCortonaResourceTD(programItem, manual, filename);

      if (cortonaByte != null) {
        return cortonaByte;
      }
    }

    filename = getFilenameByBandwidthValue(filename, programItem, manual, bandwidth,
        multiBrowserDocumentRequired);

    String filePath = RESOURCE_DOC + manual;

    if (userIsExplicitlyEnabledToViewFile) {
      String smmFullPagerDirectory = "/" + manual + SMM_FULL_PAGE_DIRECTORY_SUFFIX + "/";
      filePath += smmFullPagerDirectory;
    }
    String queryParam = getQueryParam(bandwidth, multiBrowserDocumentRequired);
    AmazonS3 amazonS3Client = amazonS3ClientFactory.getS3Client();
    byte[] s3ObjectByteArray = null;
    String s3ObjKey = Util.createVersionFolderS3ObjKey(programItem, manual, filename);

    if (!amazonS3Client.doesObjectExist(s3Config.getS3Bucket().getBucketName(), s3ObjKey)) {
      s3ObjKey = Util.createProgramFolderS3ObjKey(programItem, manual, filename);
    }

    S3Object s3Object;

    try {
      s3Object = awsResourcesService.getS3Object(s3Config.getS3Bucket().getBucketName(), s3ObjKey);
    } catch (TechpubsException techpubsException) {
      return displayDocumentNotFound().getBytes();
    }

    try {
      s3ObjectByteArray = IOUtils.toByteArray(s3Object.getObjectContent());

    } catch (IOException e) {
      log.error(DataConstants.GET_BINARY_RESOURCE_LOGGER + s3ObjKey + ")", e);
      throw new TechnicalException(
          DataConstants.GET_BINARY_RESOURCE_LOGGER + s3ObjKey + ")", e);

    } catch (NullPointerException e) {
      log.error(DataConstants.GET_BINARY_RESOURCE_LOGGER + s3ObjKey + ")", e);
      s3ObjectByteArray = null;
    }

    return prepareDocumentTD(programItem, manual, filename, s3ObjectByteArray, queryParam,
        userIsExplicitlyEnabledToViewFile).getBytes();

  }

  @Deprecated
  @Override
  @LogExecutionTime
  //Remove this class during US478605 feature flag cleanup
  public byte[] getHTMLResourceTD(ProgramItemModel programItem, String manual, String target,
      String filename,
      String bandwidth, boolean multiBrowserDocumentRequired) throws TechpubsException {

    String tempFilename = filename;

    if (target != null) {
      List<PageblkLookupDto> entities = iResourceData
          .pageblkTargetLookup(programItem, manual, target);
      switch (entities.size()) {
        case 0:
          log.debug(DataConstants.DOCUMENT_LOGGER + target + ").");
          return displayDocumentNotFound().getBytes();
        case 1:
          tempFilename = entities.get(0).getOnlineFilename();
          break;
        default:
          return displaySelectDocument(programItem, entities, bandwidth,
              multiBrowserDocumentRequired).getBytes();
      }
    }

    // Determine bandwidth file based on filename parameter...filename will
    // always be high bandwidth
    Element manualElement = (Element) programItem.getTocRoot()
        .selectSingleNode(DOC_NBR + manual + "']");
    String xpath = ".//*[@file='" + tempFilename + "']";
    Element docElement = (Element) manualElement.selectSingleNode(xpath);
    if (docElement != null && ("low".equalsIgnoreCase(bandwidth) || (
        multiBrowserDocumentRequired
            && !"Y".equalsIgnoreCase(
            manualElement.attributeValue(DataConstants.DOCUMENTS_MULTIBROWSER))))) {
      tempFilename = (docElement.attributeValue(DataConstants.MFILE) != null
          ? docElement.attributeValue(DataConstants.MFILE)
          : docElement.attributeValue(DataConstants.METADATA_FILE));
    }

    // check to see if cortona3d file exists based off of htm name and return it if it exists
    if (filename != null) {

      byte[] cortonaByte = getCortonaResourceTD(programItem, manual, filename);

      if (cortonaByte != null) {
        return cortonaByte;
      }
    }

    AmazonS3 amazonS3Client = amazonS3ClientFactory.getS3Client();
    byte[] s3ObjectByteArray = null;
    String s3ObjKey = Util.createVersionFolderS3ObjKey(programItem, manual, filename);

    if (!amazonS3Client.doesObjectExist(s3Config.getS3Bucket().getBucketName(), s3ObjKey)) {
      s3ObjKey = Util.createProgramFolderS3ObjKey(programItem, manual, filename);
    }

    S3Object s3Object;
    try {
      s3Object = awsResourcesService.getS3Object(s3Config.getS3Bucket().getBucketName(), s3ObjKey);
    } catch (TechpubsException techpubsException) {
      log.info("File not found: " + techpubsException.getMessage());
      return displayDocumentNotFound().getBytes();
    }

    try {
      s3ObjectByteArray = IOUtils.toByteArray(s3Object.getObjectContent());

    } catch (IOException e) {
      log.error(DataConstants.GET_BINARY_RESOURCE_LOGGER + s3ObjKey + ")", e);
      throw new TechnicalException(
          DataConstants.GET_BINARY_RESOURCE_LOGGER + s3ObjKey + ")", e);

    } catch (NullPointerException e) {
      log.error(DataConstants.GET_BINARY_RESOURCE_LOGGER + s3ObjKey + ")", e);
      s3ObjectByteArray = null;
    }

    String queryParam = getQueryParam(bandwidth, multiBrowserDocumentRequired);

    return prepareDocumentTD(programItem, manual, filename, s3ObjectByteArray, queryParam, false)
        .getBytes();

  }

  @Override
  @LogExecutionTime
  public ResourceMetaDataModel getResourceNameTD(ProgramItemModel programItem, String manual,
      String target, String filename,
      String bandwidth, boolean multiBrowserDocumentRequired) throws TechpubsException {

    ResourceMetaDataModel fileData = new ResourceMetaDataModel();
    String contentid = "";
    String title = programItem.getTitle();

    String tempFilename = filename;

    if (target != null) {
      List<PageblkLookupDto> entities = iResourceData
          .pageblkTargetLookup(programItem, manual, target);
      switch (entities.size()) {
        case 0:
          log.debug(DataConstants.DOCUMENT_LOGGER + target + ").");
          return null;
        case 1:
          tempFilename = entities.get(0).getOnlineFilename();
          break;
        default:
          return null;
      }
    }

    // Determine bandwidth file based on filename parameter...filename will
    // always be high bandwidth
    Element manualElement = (Element) programItem.getTocRoot()
        .selectSingleNode(DOC_NBR + manual + "']");
    String xpath = ".//*[@file='" + filename + "' or " + "@mfile='" + filename + "']";
    Element docElement = (Element) manualElement.selectSingleNode(xpath);

    for (Object attr : docElement.attributes()) {
      DefaultAttribute currentAttr = (DefaultAttribute) attr;
      if (currentAttr.getName() == "parentnodeid") {
        contentid = currentAttr.getValue();
      }
      if (currentAttr.getName() == "title") {
        title = currentAttr.getValue();
      }
    }

    // check to see if cortona3d file exists based off of htm name and return its name and extension if it exists
    if (filename != null && cortonaCheck(programItem, manual, filename)) {
      fileData.setProgram(programItem.getProgramDocnbr());
      fileData.setManual(manual);
      fileData.setContentid(contentid);
      fileData.setFileName(filename);
      fileData.setTitle(title);
      fileData.setFileExtension(CORTONA_EXTENSION);
      fileData.setSuccess(true);
      return fileData;
    }

    File fDocument = null;
    AmazonS3 amazonS3Client = amazonS3ClientFactory.getS3Client();
    byte[] s3ObjectByteArray = null;
    String s3ObjKey = Util.createVersionFolderS3ObjKey(programItem, manual, filename);

    if (!amazonS3Client.doesObjectExist(s3Config.getS3Bucket().getBucketName(), s3ObjKey)) {
      s3ObjKey = Util.createProgramFolderS3ObjKey(programItem, manual, filename);
    }

    S3Object s3Object = awsResourcesService
        .getS3Object(s3Config.getS3Bucket().getBucketName(), s3ObjKey);

    try {
      s3ObjectByteArray = IOUtils.toByteArray(s3Object.getObjectContent());

    } catch (IOException e) {
      log.error(DataConstants.GET_BINARY_RESOURCE_LOGGER + s3ObjKey + ")", e);
      throw new TechnicalException(
          DataConstants.GET_BINARY_RESOURCE_LOGGER + s3ObjKey + ")", e);

    } catch (NullPointerException e) {
      log.error(DataConstants.GET_BINARY_RESOURCE_LOGGER + s3ObjKey + ")", e);
      s3ObjectByteArray = null;
    }

    if (s3ObjectByteArray != null) {
      fileData.setProgram(programItem.getProgramDocnbr());
      fileData.setManual(manual);
      fileData.setContentid(contentid);
      fileData.setFileName(filename);
      fileData.setTitle(title);
      fileData.setFileExtension("htm");
      fileData.setSuccess(false);
    }

    fileData.setProgram(programItem.getProgramDocnbr());
    fileData.setManual(manual);
    fileData.setContentid(contentid);
    fileData.setFileName(filename);
    fileData.setTitle(title);
    fileData.setFileExtension("htm");
    fileData.setSuccess(true);
    return fileData;
  }

  private String getQueryParam(String bandwidth, boolean multiBrowserDocumentRequired) {
    String queryParam = null;
    if (multiBrowserDocumentRequired || (bandwidth != null && bandwidth.length() > 0)) {
      queryParam = "?bw=" + bandwidth + (multiBrowserDocumentRequired ? "&mbdr=Y" : "");
    }

    return queryParam;
  }

  public String displayDocumentNotFound() {
    String rtn = null;

    try {
      rtn = IOUtils.toString(
          this.getClass().getClassLoader().getResourceAsStream("html/document-not-found.htm"),
          Charset.defaultCharset());
    } catch (IOException e) {
      log.error(DataConstants.DOCUMENT_HTML_NOT_FOUND, e);
      throw new TechnicalException(DataConstants.DOCUMENT_HTML_NOT_FOUND, e);
    }

    return rtn;
  }

  private List<Document> lookupTarget(ProgramItemModel programItemModel, String manual,
      String target) {
    IndexSearcher targetSearcher = programItemModel.getTargetSearcher();
    // lookup matching targets in the targets index
    BooleanQuery targetquery = new BooleanQuery();
    targetquery.add(new TermQuery(new Term(DataConstants.DOCUMENTS_DOCNBR, manual)),
        BooleanClause.Occur.MUST);
    targetquery.add(new TermQuery(new Term("target", target)), BooleanClause.Occur.MUST);

    final List<Document> hitList = new ArrayList<>();
    try {
      targetSearcher.search(targetquery, new Collector() {
        private IndexReader reader; // ignore scorer

        public void setScorer(Scorer scorer) {
          // Do nothing setScorer.
        }

        // accept docs out of order (for a BitSet it doesn't matter)
        public boolean acceptsDocsOutOfOrder() {
          return true;
        }

        public void collect(int doc) {
          try {
            hitList.add(reader.document(doc));
          } catch (IOException e) {
            log.error(DataConstants.LOOKUP_TARGET_LOGGER + doc + ")", e);
            throw new TechnicalException(DataConstants.LOOKUP_TARGET_LOGGER + doc + ")",
                e);
          }
        }

        public void setNextReader(IndexReader reader, int docBase) {
          this.reader = reader;
        }
      });
    } catch (IOException e) {
      log.error(DataConstants.LOOKUP_TARGET + targetquery.toString() + ")", e);
      throw new TechnicalException(DataConstants.LOOKUP_TARGET + targetquery.toString() + ")",
          e);
    }

    return hitList;
  }

  private String prepareDocumentTD(ProgramItemModel programItem, String manual, String filename, byte[] file,
      String queryParam, boolean isGraphicInSMMDirectory) {
    String sDocument = "";
    BufferedInputStream inStream = null;
    InputStream fileInputStream = null;
    XMLDocumentFilter[] filters;
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      // Ensure request file is contained in a subdirectory of the
      // program.
      DocumentInfoTDModel docInfo = new DocumentInfoTDModel();
      docInfo.setProgram(programItem.getProgramDocnbr());
      docInfo.setProgramTitle(programItem.getTitle());
      docInfo.setManual(manual);
      Element docElement = getTOCElement(programItem, manual, filename);
      if (docElement != null) {
        docInfo.setFile(docElement.attributeValue(DataConstants.METADATA_FILE));
        docInfo.setTitle(docElement.attributeValue(DataConstants.DOCUMENTS_TITLE));
        docInfo.setKey(docElement.attributeValue(DataConstants.METADATA_KEY));
        docInfo.setRevdate(docElement.attributeValue(DataConstants.DOCUMENTS_REVDATE));
        docInfo.setParentid(docElement.attributeValue(DataConstants.PARENT_NODE_ID));
        docInfo.setType(docElement.getName().toUpperCase());
        if (DataConstants.SB.equalsIgnoreCase(docElement.getName())) {
          docInfo.setSbalert((DataConstants.DOCUMENTS_ALERTS
              .equalsIgnoreCase(docElement.attributeValue(DataConstants.TYPE))
              || ALERT_COVER
              .equalsIgnoreCase(docElement.attributeValue(DataConstants.TYPE)) ? true
              : false));
          docInfo
              .setCategory(docElement.attributeValue(DataConstants.DOCUMENTS_CATEGORY));
          if (docElement.nodeCount() > 0) {
            docInfo.setSummaryUri(
                DataConstants.RESOURCE_URI_PGMS + programItem.getProgramDocnbr() + MANS
                    + docElement.attributeValue(DataConstants.DOCUMENTS_DOCNBR)
                    + "/summary/"
                    + docElement.attributeValue(DataConstants.METADATA_FILE));
          }
        }
        docInfo.setDocumentItemList(getRelatedDocuments(programItem, docInfo));
      }
      ObjectMapper mapper = new ObjectMapper();
      String jsonDocInfo = mapper.writeValueAsString(docInfo);

      HTMLConfiguration parser = new HTMLConfiguration();
      // Disable augmentations.
      parser.setFeature(AUGMENTATIONS, false);
      // Disable tag balancing.
      parser.setFeature(BALANCETAGS, false);
      // Leave element names as is
      parser.setProperty(ELEMNAMES, DataConstants.ELEMNAMES_MATCH);
      // Leave attribute names as is
      parser.setProperty(ATTRNAMES, DataConstants.NO_CHANGE);

      String ext = FilenameUtils.getExtension(filename);
      String mimeType = getContentType(filename);

      if (!CORTONA_EXTENSION.equalsIgnoreCase(ext)) {
        if (!"html".equalsIgnoreCase(ext) && !"htm".equalsIgnoreCase(ext)) {
          inStream = new BufferedInputStream(
              this.getClass().getClassLoader()
                  .getResourceAsStream(HTML_BIN_WRAPPER_HTM));
          String embedDocument =
              "/services/techpubs/techdocs/pgms/" + programItem.getProgramDocnbr()
                  + DataConstants.VERSIONS_URI_PATH
                  + programItem.getProgramOnlineVersion() + MANS + manual + "/res/" + filename;
          filters = new XMLDocumentFilter[3];
          filters[0] = new UpdateEmbedDocumentFilter(embedDocument, mimeType);
          filters[1] = new InjectMetaDataFilter(jsonDocInfo);
          filters[2] = new HTMLWriterFilter(baos, DataConstants.UTF_8);
        } else {
          fileInputStream = new ByteArrayInputStream(file);
          inStream = new BufferedInputStream(fileInputStream);
          filters = new XMLDocumentFilter[6];
          filters[0] = new ReplaceStyleSheetFilter("/css/",
              DataConstants.SERVICE_URL_CSS);
          filters[1] = new UpdateTargetAnchorsFilter(lazyLoadingImages, DataConstants.SERVICE_URL, programItem, manual,
              filename, queryParam,
              this.iResourceData, this.iBookcaseVersionData,
              iPageblkLookupData, this.log);
          filters[2] = new UpdateResourceFilter(DataConstants.SERVICE_URL, programItem,
              manual, isGraphicInSMMDirectory, docInfo.getFile());

          filters[3] = new InjectMetaDataFilter(jsonDocInfo);
          filters[4] = new BrowserSupportFilter();
          filters[5] = new HTMLWriterFilter(baos, DataConstants.UTF_8);
        }
        parser.setProperty(FILTERS, filters);
        XMLInputSource xis = new XMLInputSource(null, null, null, inStream, null);
        parser.parse(xis);
        baos.close();
        sDocument = baos.toString();
      }
    } catch (IOException e) {
      log.error(DataConstants.PREPARE_DOCUMENT_TD + filename + ")", e);
      throw new TechnicalException(
          DataConstants.PREPARE_DOCUMENT_TD + filename + ")", e);
    } finally {
      if (fileInputStream != null) {
        try {
          fileInputStream.close();
        } catch (IOException e) {
          log.error(DataConstants.LOGGER_GETTPDOCS, e);
        }
      }
      if (inStream != null) {
        try {
          inStream.close();
        } catch (IOException e) {
          log.error(DataConstants.LOGGER_GETTPDOCS, e);
        }
        IOUtils.closeQuietly(inStream);
      }
    }
    return sDocument;
  }


  private String getContentType(String filename) {
    String type = URLConnection.guessContentTypeFromName(filename);
    if (type != null) {
      return type;
    }
    String ext = FilenameUtils.getExtension(filename).toLowerCase();
    switch (ext) {
      case "svg":
        return "image/svg+xml";
      case "css":
        return "text/css";
      case "js":
        return "text/javascript";
      case "wrl":
        return "model/vrml";
      case "cortona3d":
        return "model/cortona3d";
      case "cgm":
        return "image/cgm";
      case "ppt":
        return "application/vnd.ms-powerpoint";
      case "doc":
        return "application/msword";
      case "xls":
        return "application/vnd.ms-excel";
      case "wmv":
        return "video/x-ms-wmv";
      case "avi":
        return "video/avi";
      case "mp2":
      case "mp3":
      case "mp4":
        return "video/mpeg";
      case "m4v":
      case "m4a":
        return "video/mp4";
      case "xlsx":
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
      case "pptx":
        return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
      case "docx":
        return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
      default:
        return "application/octet-stream";
    }
  }

  /**
   * return a specific TOC item
   *
   * @param programItem - TD Program
   * @param manual - TD Manual
   * @param filename - File Name
   * @return TD TOC Entry
   */
  private Element getTOCElement(ProgramItemModel programItem, String manual, String filename) {
    Element docElement = null;
    if (programItem != null) {
      Element manualElement = (Element) programItem.getTocRoot()
          .selectSingleNode(DOC_NBR + manual + "']");
      String xpath = ".//*[@file='" + filename + "'] | .//*[@mfile='" + filename + "']";
      docElement = (Element) manualElement.selectSingleNode(xpath);
    }

    return docElement;
  }

  /**
   * Return a list of related documents for TD Document (ICs, TRs..)
   *
   * @param programItem - TD Program
   * @param docInfo - TD Document
   * @return List<RelatedDocumentInfoModel> - List of related TD Documents
   */
  @SuppressWarnings("unchecked")
  private List<RelatedDocumentInfoModel> getRelatedDocuments(ProgramItemModel programItem,
      DocumentInfoTDModel docInfo) {
    List<RelatedDocumentInfoModel> relatedDocumentInfoList = new ArrayList<>();
    if (programItem != null) {
      Element manualElement = (Element) programItem.getTocRoot()
          .selectSingleNode(DOC_NBR + docInfo.getManual() + "']");
      String xpath =
          ".//ic[@file and @key='" + docInfo.getKey() + "'] | .//tr[@file and @key='"
              + docInfo.getKey() + "']";
      for (Node node : (List<Node>) manualElement.selectNodes(xpath)) {
        Element element = (Element) node;
        if (docInfo.compareRevDate(element.attributeValue(DataConstants.DOCUMENTS_REVDATE))
            <= 0
            && !docInfo.getFile()
            .equals((element.attributeValue(DataConstants.MFILE) != null
                ? element.attributeValue(DataConstants.MFILE)
                : element.attributeValue(DataConstants.METADATA_FILE)))) {
          RelatedDocumentInfoModel relatedDocumentInfo = new RelatedDocumentInfoModel();
          relatedDocumentInfo.setProgram(programItem.getProgramDocnbr());
          relatedDocumentInfo.setManual(docInfo.getManual());
          relatedDocumentInfo.setFile((element.attributeValue(DataConstants.MFILE) != null
              ? element.attributeValue(DataConstants.MFILE)
              : element.attributeValue(DataConstants.METADATA_FILE)));
          relatedDocumentInfo.setType(element.getName().toLowerCase());
          relatedDocumentInfo
              .setTitle(element.attributeValue(DataConstants.DOCUMENTS_TITLE));
          relatedDocumentInfo
              .setRevdate(element.attributeValue(DataConstants.DOCUMENTS_REVDATE));
          relatedDocumentInfoList.add(relatedDocumentInfo);
        }
      }
    }
    if (relatedDocumentInfoList.size() > 1) { // Sort list by revdate
      // (descending)
      Collections.sort(relatedDocumentInfoList, (RelatedDocumentInfoModel rd1,
          RelatedDocumentInfoModel rd2) -> rd2.getRevdate().compareTo(rd1.getRevdate()));
    }
    return relatedDocumentInfoList;
  }

  /**
   * Return Select Document HTML for TDSubsystem
   *
   * @param programItem - TD Program
   * @param documentList - List of TD documents for selection
   * @return - HTML File
   */
  public String displaySelectDocument(ProgramItemModel programItem, List<Document> documentList,
      String bandwidth,
      boolean multiBrowserDocumentRequired, String target) {

    List<Element> elementList = new ArrayList<>();
    for (Document document : documentList) {
      Element tocElement = getTOCElement(programItem,
          document.get(DataConstants.DOCUMENTS_DOCNBR),
          document.get(DataConstants.METADATA_FILENAME));
      if (tocElement != null) {
        elementList.add(tocElement);
      }
    }

    BufferedInputStream inStream = new BufferedInputStream(
        this.getClass().getClassLoader().getResourceAsStream(DataConstants.DOCUMENT_HTML));
    HTMLConfiguration parser = new HTMLConfiguration();
    // Disable augmentations.
    parser.setFeature(AUGMENTATIONS, false);
    // Disable tag balancing.
    parser.setFeature(BALANCETAGS, false);
    // Leave element names as is
    parser.setProperty(ELEMNAMES, DataConstants.ELEMNAMES_MATCH);
    // Leave attribute names as is
    parser.setProperty(ATTRNAMES, NO_CHANGE);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try {
      XMLDocumentFilter[] filters = new XMLDocumentFilter[2];
      filters[0] = new InjectDocumentSelectionsFilter(DataConstants.SERVICE_URL, programItem,
          elementList, "bw=" + bandwidth + "&mbdr="
          + (multiBrowserDocumentRequired ? "Y" : "N"), this.iResourceData,
          this.log);
      filters[1] = new HTMLWriterFilter(baos, DataConstants.UTF_8);

      parser.setProperty(FILTERS, filters);

      XMLInputSource xis = new XMLInputSource(null, null, null, inStream, null);
      parser.parse(xis);

      inStream.close();
      baos.close();
    } catch (IOException e) {
      log.error("displaySelectDocument (html/select-document.htm)", e);
      throw new TechnicalException("displaySelectDocument (html/select-document.htm)", e);
    } finally {
      IOUtils.closeQuietly(inStream);
      IOUtils.closeQuietly(baos);
    }

    return baos.toString();
  }

  /**
   * Return Select Document HTML for TDSubsystem
   *
   * @param programItem - TD Program
   * @param entities - List of entities for selection
   * @return - HTML File
   */
  public String displaySelectDocument(ProgramItemModel programItem, List<PageblkLookupDto> entities, String bandwidth, boolean multiBrowserDocumentRequired) {
    List<Element> elementList = new ArrayList<>();
    for (PageblkLookupDto entity : entities) {
      Element tocElement = getTOCElement(programItem, entity.getBookKey(), entity.getOnlineFilename());
      if (tocElement != null) {
        elementList.add(tocElement);
      }
    }

    String htmlFile;

    ClassLoader cLoader = this.getClass().getClassLoader();
    try (BufferedInputStream inStream = new BufferedInputStream(cLoader.getResourceAsStream(DataConstants.DOCUMENT_HTML))) {
      HTMLConfiguration parser = new HTMLConfiguration();
      parser.setFeature(AUGMENTATIONS, false); // Disable augmentations.
      parser.setFeature(BALANCETAGS, false); // Disable tag balancing.
      parser.setProperty(ELEMNAMES, DataConstants.ELEMNAMES_MATCH); // Leave element names as is
      parser.setProperty(ATTRNAMES, NO_CHANGE); // Leave attribute names as is

      try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        XMLDocumentFilter[] filters = new XMLDocumentFilter[2];
        filters[0] = new InjectDocumentSelectionsFilter(DataConstants.SERVICE_URL, programItem,
            elementList, "bw=" + bandwidth + "&mbdr="
            + (multiBrowserDocumentRequired ? "Y" : "N"), this.iResourceData, log);
        filters[1] = new HTMLWriterFilter(baos, DataConstants.UTF_8);

        parser.setProperty(FILTERS, filters);

        XMLInputSource xis = new XMLInputSource(null, null, null, inStream, null);
        parser.parse(xis);

        htmlFile = baos.toString();
      }
    } catch (IOException e) {
      throw new TechnicalException(e);
    }

    return htmlFile;
  }

  @Override
  public byte[] getCortonaResourceTD(ProgramItemModel programItem, String manual,
      String resourceName) throws TechpubsException {
    byte[] resource;

    String s3ObjKey = getCortonaFileName(programItem, manual, resourceName);

    if (s3ObjKey.isEmpty()) {
      return null;
    }

    S3Object s3Object = awsResourcesService
        .getS3Object(s3Config.getS3Bucket().getBucketName(), s3ObjKey);

    try {

      resource = IOUtils.toByteArray(s3Object.getObjectContent());

    } catch (IOException e) {
      log
          .error(DataConstants.GET_BINARY_RESOURCE_LOGGER + s3ObjKey + ")",
              e);
      throw new TechnicalException(
          DataConstants.GET_BINARY_RESOURCE_LOGGER + s3ObjKey + ")", e);
    }
    return resource;
  }

  @Override
  @LogExecutionTime
  public byte[] getBinaryResourceTDs3(ProgramItemModel programItem, String manual, String res)
      throws TechpubsException {

    AmazonS3 amazonS3Client = amazonS3ClientFactory.getS3Client();
    byte[] s3ObjectByteArray = null;
    String s3ObjKey = Util.createVersionFolderS3ObjKey(programItem, manual, res);

    if (!amazonS3Client.doesObjectExist(s3Config.getS3Bucket().getBucketName(), s3ObjKey)) {
      s3ObjKey = Util.createProgramFolderS3ObjKey(programItem, manual, res);
    }

    S3Object s3Object = awsResourcesService.getS3Object(s3Config.getS3Bucket().getBucketName(), s3ObjKey);
    long size = s3Object.getObjectMetadata().getContentLength();

    log.info("S3Object size in getBinaryResourceTDs3 : "+ size);

    try {
      s3ObjectByteArray = IOUtils.toByteArray(s3Object.getObjectContent());

    } catch (IOException e) {
      log.error(DataConstants.GET_BINARY_RESOURCE_LOGGER + s3ObjKey + ")", e);
      throw new TechnicalException(
          DataConstants.GET_BINARY_RESOURCE_LOGGER + s3ObjKey + ")", e);

    } catch (NullPointerException e) {
      log.error(DataConstants.GET_BINARY_RESOURCE_LOGGER + s3ObjKey + ")", e);
      s3ObjectByteArray = null;
    }

    return s3ObjectByteArray;
  }


  @Override
  public boolean cortonaCheck(ProgramItemModel programItem, String manual, String resourceName) {

    return cortonaFileCheck(resourceName, programItem.getProgramOnlineVersion(),
        programItem.getProgramDocnbr(), manual);
  }

  /**
   * This method connects to techlib DB to identify if the passed file is Cortona file and returns
   * true else returns false.
   */

  @Override
  public boolean cortonaFileCheck(String fileName, String version, String bookcase, String book) {

    if (fileName.startsWith("m-")) {
      fileName = fileName.substring(2);
    }

    if (!(fileName.toLowerCase().endsWith(".htm") || fileName.toLowerCase()
        .endsWith(".html"))) {
      return false;
    }
    CortonaLookupEntity cortonaLookupEntity = null;
    if (cortonaBookcases.contains(bookcase)) {
      try {
        cortonaLookupEntity = iCortonaLookup.findCortonaFile(fileName, version, bookcase, book);
      } catch (Exception ex) {
        log.error(String
            .format("Error occurred while checking Cortona files : %s ", ex.getMessage()));
        return false;
      }
    }

    if (cortonaLookupEntity == null) {
      return false;
    }

    String filePath = cortonaLookupEntity.getCortonaFilename();
    log.info(String.format("Cortona file path  : %s ", filePath));

    return !filePath.isEmpty();
  }

  @Override
  public List<CortonaTargetDto> cortonaTargetLookup(ProgramItemModel programItem, String manual,
      String target)
      throws TechpubsException {
    List<CortonaTargetDto> cortonaTargetDtos = new ArrayList<>();
    if (cortonaBookcases.contains(programItem.getProgramDocnbr())) {
      try {
        cortonaTargetDtos = iCortonaLookup.findCortonaFilesByTarget(programItem.getProgramDocnbr(),
            programItem.getProgramOnlineVersion(), manual, target.toLowerCase());
      } catch (Exception ex) {
        log.error(ex.getMessage());
      }
    }

    return cortonaTargetDtos;
  }

  @Override
  public List<String> cortonaBooks() throws TechpubsException {
    return cortonaBookcases;
  }

  private String getCortonaFileName(ProgramItemModel programItem, String manual,
      String resourceName)
      throws TechpubsException {
    if (resourceName.startsWith("m-")) {
      resourceName = resourceName.substring(2);
    }

    if (!(resourceName.toLowerCase().endsWith(".htm") || resourceName.toLowerCase()
        .endsWith(".html"))) {
      return "";
    }

    CortonaLookupEntity cortonaLookupEntity = null;
    if (cortonaBookcases.contains(programItem.getProgramDocnbr())) {
      try {
        cortonaLookupEntity = iCortonaLookup
            .findCortonaFile(resourceName, programItem.getProgramOnlineVersion(),
                programItem.getProgramDocnbr(), manual);

      } catch (Exception ex) {
        log.error(String
            .format("Error occurred while checking Cortona files : %s ", ex.getMessage()));
        return "";
      }
    }

    if (cortonaLookupEntity == null) {
      return "";
    }

    return cortonaLookupEntity.getCortonaFilename();
  }

  /**
   * Takes the html file name and maps it based on the unique identifying items to create a regex
   * expression to then relate that to the expected cortona file.
   * <p>
   * HTML name:
   * <modelIdentityCode>-<systemDiffCode>-<systemCode>-<subsystemCode>-<assyCode>-
   * <disassyCode><disassyCodeVariant>-<infoCode><infoCodeVariant>-<itemLocationCode>.htm
   * <p>
   * Media name: ICN-<modelIdentityCode>-<systemDiffCode>-<systemCode><subsystemCode><assyCode>-
   * <responsiblePartnerCompanyCode>-<originatorCode>-<uniqueIdentifier>-<variantCode>
   * -<issueNumber>-<securityClassification>.cortona3d
   */
  private String htmlToCortonaFileRegex(String htmlName) {
    String tempName = htmlName.replace("htm", "");
    int dashNum = 0;
    int charIndex = 0;

    if (tempName.startsWith("IC-")) {
      tempName = tempName.replaceFirst("IC-", "");
    }

    while (charIndex < tempName.length()) {
      if (tempName.charAt(charIndex) == '-') {
        dashNum++;
        /**
         * Matching to the Cortona file's dehyphened
         * <systemCode><subsystemCode><assyCode>
         */
        if (dashNum > 2 && dashNum < 6) {
          int tempSize = tempName.length();
          tempName = tempName.substring(0, charIndex) +
              tempName.substring(charIndex + 1, tempSize);
          charIndex--;
        }

        /**
         * Matching the unique identifier on the html to that on the cortona file with regex in between
         * to match to the cortona file name's irrelevant info.
         */
        if (dashNum == 5) {
          int tempSize = tempName.length();
          tempName = tempName.substring(0, charIndex + 1) +
              ".*00" + tempName.substring(charIndex + 1, tempSize);
          charIndex--;
        }

        //regexing the end string to match the end of the name.
        if (dashNum == 6) {
          tempName = tempName.substring(0, charIndex) + ".*";
          charIndex--;
        }
      }
      charIndex++;
    }

    // end the string with cortona3d to make it only match 3d files.
    return tempName + CORTONA_EXTENSION;
  }

  private String htmlToCortonaFileRegexIC(String htmlName) {

    //The regex ensures that the revision is at the location where we expect it to be.
    if (this.MatchPartial("^IC-[aA-zZ]{8}[0-9]{2}(-)A(-)[0-9]{2}(-)[0-9]{2}.*R[0-9]{3}.htm$",
        htmlName)) {

      int htmlNameLength = htmlName.length();

      String revision = htmlName
          .substring(htmlNameLength - 7, htmlNameLength - 4);

      //The html file and cortona files don't match. We're trimming off in order to match the important information. It's a little messy.
      String filePrefix = htmlName.substring(16, 25).replaceAll("-", "");

      String uniqueID = "00" + htmlName.substring(25, 28);

      //Full file regex with the revision included
      return
          "^ICN-[aA-zZ]{8}[0-9]{2}(-)A(-)" + filePrefix + "(-)0(-)[0-9]{5}(-)" + uniqueID + "(-)A-"
              + revision + "-01.cortona3d";
    }

    return htmlToCortonaFileRegex(htmlName);
  }

  /**
   * creates regex for finding sb documents given a sb number from Cortona.
   */
  public String getSbFileNameWithCortonaLinkTag(String program, String sbTag) {
    String sbRegex = ".*" + sbTag + "-r.*";
    File sbFolder = new File("/" + program + "/program/doc/sbs/");
    File matchedFile = getFirstMatchingFile(sbFolder, sbRegex);

    if (null != matchedFile) {
      return matchedFile.getName();
    } else {
      return null;
    }
  }

  /**
   * Finds a file given a path and regex expression. This is to enable pulling a file when we don't
   * have the explicit name passed to us.
   */
  @Deprecated
  private File getFirstMatchingFile(File root, String regex) {
    if (!root.isDirectory()) {
      return null;
    }
    final Pattern pattern = Pattern.compile(regex);
    final FileFinder fileFinder = new FileFinder(pattern);
    File[] possibleFiles = root.listFiles((File f) -> fileFinder.accept(f));
    if (possibleFiles == null || possibleFiles.length == 0) {
      log.error("Search for cortona documents returned no results for "
          + "Root: " + root + ", "
          + "Regex: " + regex + ", exiting search");
      return null;
    } else if (possibleFiles.length != 1) {
      log.error("Search for cortona documents returned multiple results for "
          + "Root: " + root + ", "
          + "Regex: " + regex + ", returning first document found");
      return possibleFiles[0];
    }

    return possibleFiles[0];
  }

  /**
   * Given a file look in directory for it.
   */
  class FileFinder implements FileFilter {

    private Pattern pattern;

    /**
     * constructor to accept a regex pattern
     */
    public FileFinder(Pattern pattern) {
      this.pattern = pattern;
    }

    /**
     * Accepts if file exists in current directory
     */
    @Override
    public boolean accept(File file) {
      return pattern.matcher(file.getName()).find();
    }
  }


  @Override
  @LogExecutionTime
  public byte[] getBinaryResourceTD(ProgramItemModel programItem, String manual, String res)
      throws TechpubsException {
    byte[] rtn = null;
    File fDocument;
    // If file is an IC pdf, reformat res from
    // Ex. {IC-PASSPORT20-A-73-31-04-050-941A-D-R002.pdf} format to
    // Ex. {PASSPORT20-A-73-31-04-050-941A-D.pdf} format
    // and search program location for the new format
    // but don't search the current version location
    if (res.length() == 44 && res.startsWith("IC") && res.endsWith(".pdf")) {
      System.out.println(res.length());

      byte[] s3ObjectByteArray = null;
      String s3ObjKey = Util.createProgramFolderS3ObjKey(programItem, manual, res);

      S3Object s3Object = awsResourcesService
          .getS3Object(s3Config.getS3Bucket().getBucketName(), s3ObjKey);

      try {
        s3ObjectByteArray = IOUtils.toByteArray(s3Object.getObjectContent());

      } catch (IOException e) {
        log.error(DataConstants.GET_BINARY_RESOURCE_LOGGER + s3ObjKey + ")", e);
        throw new TechnicalException(
            DataConstants.GET_BINARY_RESOURCE_LOGGER + s3ObjKey + ")", e);

      } catch (NullPointerException e) {
        log.error(DataConstants.GET_BINARY_RESOURCE_LOGGER + s3ObjKey + ")", e);
        s3ObjectByteArray = null;
      }

      return s3ObjectByteArray;

    } else {

      AmazonS3 amazonS3Client = amazonS3ClientFactory.getS3Client();
      byte[] s3ObjectByteArray = null;
      String s3ObjKey = Util.createVersionFolderS3ObjKey(programItem, manual, res);

      if (CORTONA_3D_S3) {
        // if the s3 key is cortona related, change the key to central cortona s3 folder
        if ((res.contains("res/solo") || res.contains("res/uniview"))) {
          s3ObjKey = res.replace("res", "cortona-solo");
        }
      }

      if (!amazonS3Client.doesObjectExist(s3Config.getS3Bucket().getBucketName(), s3ObjKey)) {
        s3ObjKey = Util.createProgramFolderS3ObjKey(programItem, manual, res);
      }

      S3Object s3Object = awsResourcesService
          .getS3Object(s3Config.getS3Bucket().getBucketName(), s3ObjKey);

      try {
        s3ObjectByteArray = IOUtils.toByteArray(s3Object.getObjectContent());

      } catch (IOException e) {
        log.error(DataConstants.GET_BINARY_RESOURCE_LOGGER + s3ObjKey + ")", e);
        throw new TechnicalException(
            DataConstants.GET_BINARY_RESOURCE_LOGGER + s3ObjKey + ")", e);

      } catch (NullPointerException e) {
        log.error(DataConstants.GET_BINARY_RESOURCE_LOGGER + s3ObjKey + ")", e);
        s3ObjectByteArray = null;
      }

      return s3ObjectByteArray;
    }
  }

  @Override
  @LogExecutionTime
  public String getHTMLResourceSummaryTD(ProgramItemModel programItem, String manual,
      String filename) {
    String sDocument = "";
    BufferedInputStream inStream = null;
    XMLDocumentFilter[] filters;
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      Element docElement = (Element) programItem.getTocRoot()
          .selectSingleNode(
              "descendant-or-self::node()[@docnbr='" + manual + "' and (@mfile='" + filename
                  + "' or @file='" + filename + "')]");
      if (docElement == null) {
        log.info(DataConstants.DOCUMENT_NOT_AVAILABLE + filename + ").");
        return displayDocumentNotFound();
      }
      String tempHTML = docElement.getText();
      String summaryHTML = tempHTML == null ? "" : tempHTML.trim();
      tempHTML = null;
      if (summaryHTML.length() == 0) {
        log.info(DataConstants.DOCUMENT_SUMMARY_NOT_AVAILABLE + filename + ").");
        return displayDocumentNotFound();
      }

      DocumentInfoTDModel docInfo = new DocumentInfoTDModel();
      docInfo.setProgram(programItem.getProgramDocnbr());
      docInfo.setProgramTitle(programItem.getTitle());
      docInfo.setManual(manual);
      docInfo.setFile((docElement.attributeValue(DataConstants.MFILE) != null
          ? docElement.attributeValue(DataConstants.MFILE)
          : docElement.attributeValue(DataConstants.METADATA_FILE)));
      docInfo.setTitle(docElement.attributeValue(DataConstants.DOCUMENTS_TITLE));
      docInfo.setKey(docElement.attributeValue(DataConstants.METADATA_KEY));
      docInfo.setRevdate(docElement.attributeValue(DataConstants.DOCUMENTS_REVDATE));
      docInfo.setParentid(docElement.attributeValue(DataConstants.PARENT_NODE_ID));
      docInfo.setType(docElement.getName().toUpperCase());
      if (DataConstants.SB.equalsIgnoreCase(docElement.getName())) {
        docInfo.setSbalert(
            (DataConstants.DOCUMENTS_ALERTS
                .equalsIgnoreCase(docElement.attributeValue(DataConstants.TYPE))
                || ALERT_COVER
                .equalsIgnoreCase(docElement.attributeValue(DataConstants.TYPE)) ? true
                : false));
        docInfo.setCategory(docElement.attributeValue(DataConstants.DOCUMENTS_CATEGORY));
      }
      docInfo.setDocumentItemList(getRelatedDocuments(programItem, docInfo));
      ObjectMapper mapper = new ObjectMapper();
      String jsonDocInfo = mapper.writeValueAsString(docInfo);
      HTMLConfiguration parser = new HTMLConfiguration();
      // Disable augmentations.
      parser.setFeature(AUGMENTATIONS, false);
      // Disable tag balancing.
      parser.setFeature(BALANCETAGS, false);
      // Leave element names as is
      parser.setProperty(ELEMNAMES, "match");
      // Leave attribute names as is
      parser.setProperty(ATTRNAMES, NO_CHANGE);

      inStream = new BufferedInputStream(
          new ByteArrayInputStream(summaryHTML.getBytes(DataConstants.UTF_8)));
      filters = new XMLDocumentFilter[6];
      filters[0] = new ReplaceStyleSheetFilter("/css/", DataConstants.SERVICE_URL_CSS);
      filters[1] = new UpdateTargetAnchorsFilter(DataConstants.SERVICE_URL, programItem, manual,
          null, null,
          iResourceData, iBookcaseVersionData,
          iPageblkLookupData, this.log);
      filters[2] = new UpdateResourceFilter(DataConstants.SERVICE_URL, programItem, manual);
      filters[3] = new InjectMetaDataFilter(jsonDocInfo);
      filters[4] = new BrowserSupportFilter();
      filters[5] = new HTMLWriterFilter(baos, DataConstants.UTF_8);

      parser.setProperty(FILTERS, filters);
      XMLInputSource xis = new XMLInputSource(null, null, null, inStream, null);
      parser.parse(xis);
      baos.close();
      sDocument = baos.toString();
      inStream.close();
    } catch (JsonGenerationException e) {
      log.error(
          DataConstants.GET_BINARY_RESOURCE_SUMMARY_LOGGER + "JsonGenerationException)", e);
      throw new TechnicalException(
          DataConstants.GET_BINARY_RESOURCE_SUMMARY_LOGGER + "JsonGenerationException)",
          e);
    } catch (JsonMappingException e) {
      log.error(DataConstants.GET_BINARY_RESOURCE_SUMMARY_LOGGER + "JsonMappingException)",
          e);
      throw new TechnicalException(
          DataConstants.GET_BINARY_RESOURCE_SUMMARY_LOGGER + "JsonMappingException)", e);
    } catch (IOException e) {
      log.error(DataConstants.GET_BINARY_RESOURCE_SUMMARY_LOGGER + filename + ")", e);
      throw new TechnicalException(
          DataConstants.GET_BINARY_RESOURCE_SUMMARY_LOGGER + filename + ")", e);
    } finally {
      IOUtils.closeQuietly(inStream);
    }

    return sDocument;
  }

  @Override
  @LogExecutionTime
  public String getCSSResource(String cssRes) {
    GridFSBucket gridFSBucket = null;
    GridFSFile gridFSFile = null;
    MongoDatabase mongoDb = mongoClient.getDatabase(DataConstants.DB_TECHPUBS);

    try {
      gridFSBucket = GridFSBuckets.create(mongoDb, DataConstants.COLLECTION_ARTIFACTS);
      gridFSFile = gridFSBucket
          .find(new org.bson.Document(DataConstants.METADATA_FILENAME, cssRes)
              .append(DataConstants.METADATA_TYPE, DataConstants.CSS)
              .append(DataConstants.METADATA_SUBSYSTEM, SubSystem.TD.toString())).first();
    } catch (Exception e) {
      log.error(
          DataConstants.LOGGER_GETCSSRESOURCE + cssRes + DataConstants.LOGGER_MONGOERROR, e);
      throw new TechnicalException(
          DataConstants.LOGGER_GETCSSRESOURCE + cssRes + DataConstants.LOGGER_MONGOERROR,
          e);
    }

    if (gridFSFile == null) {
      log.error(
          DataConstants.LOGGER_GETCSSRESOURCE + cssRes + DataConstants.LOGGER_NOCSSFILE);
      throw new TechnicalException(
          DataConstants.LOGGER_GETCSSRESOURCE + cssRes + DataConstants.LOGGER_NOCSSFILE,
          new Exception(DataConstants.LOGGER_NOCSSFILE));
    }

    String rtn = "";
    GridFSDownloadStream cssInputStream = null;

    try {
      cssInputStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
      rtn = IOUtils.toString(cssInputStream, DataConstants.UTF_8);
    } catch (Exception e) { // (IOException | MongoGridFSException)
      log.error(
          DataConstants.LOGGER_GETCSSRESOURCE + cssRes + DataConstants.LOGGER_NOSTREAMMONGO,
          e);
      throw new TechnicalException(
          DataConstants.LOGGER_GETCSSRESOURCE + cssRes + DataConstants.LOGGER_NOSTREAMMONGO,
          e);
    } finally {
      if (cssInputStream != null) {
        cssInputStream.close();
      }
    }

    return rtn;
  }

  @Override
  @LogExecutionTime
  public byte[] getPrintNotAvailable() {
    byte[] pdf;
    try {
      InputStream inputStream = getClass().getClassLoader()
          .getResourceAsStream("pdf/PrintNotAvailable.pdf");
      pdf = IOUtils.toByteArray(inputStream);
    } catch (IOException e) {
      log.error("getPrintNotAvailable (pdf/PrintNotAvailable.pdf)", e);
      throw new TechnicalException("getPrintNotAvailable (pdf/PrintNotAvailable.pdf)", e);
    }
    return pdf;
  }

  @Override
  @LogExecutionTime
  public String getStylesheet(String filename) {
    String style = "";
    try {
      InputStream css = this.getClass().getClassLoader()
          .getResourceAsStream("stylesheets/" + filename);
      style = IOUtils.toString(css, "UTF-8");
    } catch (IOException e) {
      log.error("getStylesheet exceptions", e);
      throw new TechnicalException("getStylesheet exceptions", e);
    }

    return style;
  }

  //The Match method only passes when the entire string matches. This allows for a partial match on the string.
  private boolean MatchPartial(String regex, String value) {
    Pattern patPackageDescription = Pattern.compile(regex);
    Matcher matPackageDescription = patPackageDescription.matcher(value);

    return matPackageDescription.find();
  }

  public List<PageblkLookupDto> pageblkTargetLookup(ProgramItemModel programItem, String bookKey, String target) {
    List<PageblkLookupDto> pageblkLookupDtos = new ArrayList<>();

    try {
      pageblkLookupDtos = iPageblkLookupData.lookupPageblkByTarget(programItem.getProgramDocnbr(), programItem.getProgramOnlineVersion(), bookKey, target.toLowerCase());
    } catch (Exception e) {
      log.error(e.getMessage());
    }

    return pageblkLookupDtos;
  }
}

