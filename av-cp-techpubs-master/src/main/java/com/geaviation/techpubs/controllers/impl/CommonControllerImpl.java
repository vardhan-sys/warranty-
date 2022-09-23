package com.geaviation.techpubs.controllers.impl;

import com.geaviation.techpubs.controllers.util.Constants;
import com.geaviation.techpubs.controllers.util.ControllerUtil;
import com.geaviation.techpubs.data.api.IResourceData;
import com.geaviation.techpubs.data.util.SecurityEscape;
import com.geaviation.techpubs.data.util.log.LogExecutionTime;
import com.geaviation.techpubs.data.util.log.LogExecutionTimeWithArgs;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.DocumentDataTableModel;
import com.geaviation.techpubs.models.DocumentModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.services.api.IDocSubSystemApp;
import com.geaviation.techpubs.services.api.IManualApp;
import com.geaviation.techpubs.services.impl.DocAppRegServices;
import com.geaviation.techpubs.services.util.AppConstants;
import com.geaviation.techpubs.services.util.TechpubsAppUtil;
import io.swagger.annotations.ApiParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import static com.geaviation.techpubs.services.util.AppConstants.PORTAL_ID;
import static com.geaviation.techpubs.services.util.AppConstants.SM_SSOID;

@RestController
@RefreshScope
public class CommonControllerImpl {

  @Value("${DEPLOYEDVERSION}")
  private String deployedVersion;

  @Value("${techpubs.services.sqlInjection}")
  private boolean sqlInjection;

  @Value("${techpubs.services.reachEndpoints}")
  private boolean reachEndpointsActive;

  private static final Logger log = LogManager.getLogger(CommonControllerImpl.class);

  @Autowired
  private TechpubsAppUtil techpubsAppUtil;

  @Autowired
  private DocAppRegServices docAppRegServices;

  @Autowired
  private IResourceData iResourceData;

  @Autowired
  private IManualApp iManualApp;

  /**
   * Simple service that returns a 200 response. Can be used as a basic check to signify that the
   * application is up and accepting requests. Additionally, shows the application version as well
   * as the number of Methods and method names in the class that hosts the service endpoints
   */
  @GetMapping(value = "/ping", produces = MediaType.APPLICATION_JSON_VALUE)
  @LogExecutionTime
  public ResponseEntity<String> ping() {
    StringBuilder pingResponse = new StringBuilder();
    pingResponse.append("Deployed Version " + deployedVersion + "\n\n");
    Method[] bookcaseControllerMethods = BookcaseControllerImpl.class.getDeclaredMethods();
    Method[] bookcaseVersionControllerMethods = BookcaseVersionControllerImpl.class.getDeclaredMethods();
    Method[] bookcasesControllerMethods = BookcasesControllerImpl.class.getDeclaredMethods();
    Method[] bookcaseTocControllerMethods = BookcaseTOCControllerImpl.class.getDeclaredMethods();
    Method[] bookcaseTocVersionControllerMethods = BookcaseTOCVersionControllerImpl.class.getDeclaredMethods();
    Method[] commonControllerMethods = CommonControllerImpl.class.getDeclaredMethods();
    Method[] documentDownloadControllerMethods = DocumentDownloadControllerImpl.class.getDeclaredMethods();
    Method[] documentTypeControllerMethods = DocumentTypeControllerImpl.class.getDeclaredMethods();
    ArrayList<Method[]> controllerMethodsList = new ArrayList<>();
    controllerMethodsList.add(bookcaseControllerMethods);
    controllerMethodsList.add(bookcaseVersionControllerMethods);
    controllerMethodsList.add(bookcasesControllerMethods);
    controllerMethodsList.add(bookcaseTocControllerMethods);
    controllerMethodsList.add(bookcaseTocVersionControllerMethods);
    controllerMethodsList.add(commonControllerMethods);
    controllerMethodsList.add(documentDownloadControllerMethods);
    controllerMethodsList.add(documentTypeControllerMethods);
    pingResponse.append("Total number of deployed bookcase services: " + bookcaseControllerMethods.length + "\n");
    pingResponse.append("Total number of deployed bookcase version services: " + bookcaseVersionControllerMethods.length + "\n");
    pingResponse.append("Total number of deployed bookcases services: " + bookcasesControllerMethods.length + "\n");
    pingResponse.append("Total number of deployed bookcase TOC services: " + bookcaseTocControllerMethods.length + "\n");
    pingResponse.append("Total number of deployed bookcase version TOC services: " + bookcaseTocVersionControllerMethods.length + "\n");
    pingResponse.append("Total number of deployed common services: " + commonControllerMethods.length + "\n");
    pingResponse.append("Total number of deployed document download services: " + documentDownloadControllerMethods.length + "\n");
    pingResponse.append("Total number of deployed document type services: " + documentTypeControllerMethods.length + "\n\n");
    pingResponse.append("Deployed Services list:\n");
    for (int i = 0; i < controllerMethodsList.size(); i++) {
      Method[] currentMethodArray = controllerMethodsList.get(i);
      for (int j = 0; j < currentMethodArray.length; j++) {
        pingResponse.append((j + 1) + ". " + currentMethodArray[j].getReturnType().getName() + " " + currentMethodArray[j].getName() + "\n");
      }
    }
    return ResponseEntity.ok(pingResponse.toString());
  }

  /**
   * getResource service returns the resource for the particular file id and document type
   * selected by the user. This response contains document uri , src and content-type.
   *
   * @param fileId  the file id
   * @param type    the document type
   * @param request the httpservlet request
   * @return Response the resource
   * @throws TechpubsException the techpubs exceptions
   */
  @GetMapping(value = "/techdocs/{id}", produces = MediaType.TEXT_HTML_VALUE)
  @LogExecutionTimeWithArgs
  public ResponseEntity<String> getResource(
          @RequestHeader(SM_SSOID) String ssoId,
          @RequestHeader(PORTAL_ID) String portalId,
          @ApiParam(name = "id", value = "eg. 574bfe699a5b8d110088ff26", allowMultiple = false, required = false) @PathVariable("id") String fileId,
          @ApiParam(name = "type", value = "eg. vi", allowMultiple = false, required = false) @RequestParam("type") String type,
          HttpServletRequest request) throws TechpubsException {

    if(sqlInjection) {
      ssoId = SecurityEscape.cleanString(ssoId);
      portalId = SecurityEscape.cleanString(portalId);
      fileId = SecurityEscape.cleanString(fileId);
      type = SecurityEscape.cleanString(type);
    }

    SubSystem subSystem = techpubsAppUtil.getSubSystem(type);
    IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(subSystem);
    String html = iDocSubSystemApp.getResource(ssoId, portalId, null, fileId, techpubsAppUtil.getQueryParams(request));
    return ResponseEntity.ok(html);
  }

  /**
   * getAssociatedDocuments service returns the associated document for the particular file id and
   * document type selected by the user. This response contains list of associated documents CMM
   * ,AOW, FH.
   *
   * @param fileId  the file id
   * @param type    the document type
   * @param request the httpservlet request
   * @return Response the associated documents CMM ,AOW, FH
   * @throws TechpubsException the techpubs exceptions
   */
  @GetMapping(value = "/techdocs/{id}/associated", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  @LogExecutionTimeWithArgs
  public ResponseEntity<DocumentModel> getAssociatedDocuments(
          @RequestHeader(SM_SSOID) String ssoId,
          @RequestHeader(PORTAL_ID) String portalId,
          @ApiParam(name = "id", value = "eg. 57ab74249a5b8d1100718bde", allowMultiple = false, required = false) @PathVariable("id") String fileId,
          @ApiParam(name = "type", value = "eg. fh", allowMultiple = false, required = false) @RequestParam("type") String type,
          HttpServletRequest request) throws TechpubsException {

    if(sqlInjection) {
      ssoId = SecurityEscape.cleanString(ssoId);
      portalId = SecurityEscape.cleanString(portalId);
      fileId = SecurityEscape.cleanString(fileId);
      type = SecurityEscape.cleanString(type);
    }

    SubSystem subSystem = techpubsAppUtil.getSubSystem(type);
    IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(subSystem);
    if (iDocSubSystemApp == null) {
      log.error("docs (" + TechpubsException.TechpubsAppError.INVALID_PARAMETER.getErrorCode()
              + ") - "
              + TechpubsException.TechpubsAppError.INVALID_PARAMETER.getErrorMsg() + "("
              + AppConstants.TYPE + "="
              + subSystem + ")");
      throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
    }
    return ResponseEntity.ok(iDocSubSystemApp.getAssociatedDocuments(ssoId, portalId, fileId, techpubsAppUtil.getQueryParams(request)));
  }

  /**
   * getResourcePrint service returns the print resource for the particular file id and document
   * type selected by the user. This response returns the PDF print document.
   *
   * @param fileId  the file id
   * @param type    the document type
   * @return Response the CMM resource
   * @throws TechpubsException the techpubs exceptions
   */
  @GetMapping(value = "/techdocs/{id}/bin")
  @LogExecutionTimeWithArgs
  public ResponseEntity<byte[]> getResourcePrintBin(
          @RequestHeader(SM_SSOID) String ssoId,
          @RequestHeader(PORTAL_ID) String portalId,
          @ApiParam(name = "id", value = "eg. 574bfe699a5b8d110088ff26", allowMultiple = false, required = false) @PathVariable("id") String fileId,
          @ApiParam(name = "type", value = "eg. vi", allowMultiple = false, required = false) @RequestParam("type") String type) throws TechpubsException {

    if(sqlInjection) {
      ssoId = SecurityEscape.cleanString(ssoId);
      portalId = SecurityEscape.cleanString(portalId);
      fileId = SecurityEscape.cleanString(fileId);
      type = SecurityEscape.cleanString(type);
    }

    SubSystem subSystem = techpubsAppUtil.getSubSystem(type);
    IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(subSystem);
    Map<String, Object> artifact = iDocSubSystemApp.getArtifact(ssoId, portalId, fileId);

    String fileName = (String) artifact.get(Constants.FILENAME);

    String contentType = (String) ((Map<String, Object>) (artifact.get(Constants.METADATA))).get(Constants.TYPE);
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, Constants.EQ_FILENAME + fileName + "\"");
    return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.valueOf(contentType))
            .body((byte[]) artifact.get(Constants.CONTENT));
  }

  /**
   * getResourcePrint service returns the print resource for the particular file id and document
   * type selected by the user. This response returns the PDF print document.
   *
   * @param fileId  the file id
   * @param type    the document type
   * @return Response the CMM resource print
   * @throws TechpubsException the techpubs exceptions
   */
  @GetMapping(value = "/techdocs/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
  @LogExecutionTimeWithArgs
  public ResponseEntity<byte[]> getResourcePrintPdf(
          @RequestHeader(SM_SSOID) String ssoId,
          @RequestHeader(PORTAL_ID) String portalId,
          @ApiParam(name = "id", value = "eg. 57dc06f99a5b8d088c6816b4", allowMultiple = false, required = false) @PathVariable("id") String fileId,
          @ApiParam(name = "type", value = "eg. fh", allowMultiple = false, required = false) @RequestParam("type") String type) throws TechpubsException {

    if(sqlInjection) {
      ssoId = SecurityEscape.cleanString(ssoId);
      portalId = SecurityEscape.cleanString(portalId);
      fileId = SecurityEscape.cleanString(fileId);
      type = SecurityEscape.cleanString(type);
    }

    SubSystem subSystem = null;
    if (!reachEndpointsActive && type.equalsIgnoreCase("reach")) {
      subSystem = SubSystem.INVALID;
    } else {
      subSystem = techpubsAppUtil.getSubSystem(type);
    }
    IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(subSystem);
    Map<String, Object> artifact = iDocSubSystemApp.getArtifact(ssoId, portalId, fileId);
    String fileName = (String) artifact.get("filename");

    String contentType = (String) ((Map<String, Object>) (artifact.get(Constants.METADATA))).get(Constants.TYPE);
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, Constants.EQ_FILENAME + fileName + "\"");
    return ResponseEntity
            .ok()
            .headers(headers)
            .contentType(MediaType.valueOf(contentType))
            .body((byte[]) artifact.get(Constants.CONTENT));
  }

  /**
   * techpubsCSSResource service returns the css file based on the document type selected by the
   * user. This response returns the css document based on resource name.
   *
   * @param res     the resource
   * @return String the CSS document
   */
  @GetMapping(value = "/techdocs/css/{res}", produces = "text/css")
  @LogExecutionTimeWithArgs
  public ResponseEntity<String> techpubsCSSResource(@ApiParam(name = "res", value = "eg. geae-ipc.css", allowMultiple = false, required = false) @PathVariable("res") String res) {

    if(sqlInjection) {
      res = SecurityEscape.cleanString(res);
    }

    return ResponseEntity
            .ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, Constants.EQ_FILENAME + res + "\"")
            .body(iResourceData.getCSSResource(res));
  }

  /**
   * docs service returns the list of docs for the engine family and engine model selected by the
   * user. This response returns the list of docs.
   *
   * @param requestParams the form parameters
   * @return Response the list of docs
   * @throws TechpubsException the techpubs exceptions
   */
  @PostMapping(value = "/techdocs/docs", produces = MediaType.APPLICATION_JSON_VALUE)
  @LogExecutionTimeWithArgs
  @Deprecated
  public ResponseEntity<DocumentDataTableModel> docs(
          @RequestHeader(SM_SSOID) String ssoId,
          @RequestHeader(PORTAL_ID) String portalId,
          @RequestParam Map<String, String> requestParams)
          throws TechpubsException {

    if(sqlInjection) {
      ssoId = SecurityEscape.cleanString(ssoId);
      portalId = SecurityEscape.cleanString(portalId);
      requestParams = SecurityEscape.cleanMap(requestParams);
    }

    SubSystem subSystem = null;
    if (!reachEndpointsActive && requestParams.get(AppConstants.TYPE).equalsIgnoreCase("reach")) {
      subSystem = SubSystem.INVALID;
    } else {
      subSystem = techpubsAppUtil.getSubSystem(requestParams.get(AppConstants.TYPE));

    }

    IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(subSystem);
    if (iDocSubSystemApp == null) {
      log.error("docs (" + TechpubsException.TechpubsAppError.INVALID_PARAMETER.getErrorCode()
              + ") - "
              + TechpubsException.TechpubsAppError.INVALID_PARAMETER.getErrorMsg() + "("
              + AppConstants.TYPE + "="
              + subSystem + ")");
      throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
    }

    return ResponseEntity.ok(iDocSubSystemApp.getDocuments(ssoId, portalId, requestParams));
  }

  /**
   * techpubsLogo service returns the logo for the selected portal id. This response returns the
   * techpubs logo.
   *
   * @return byte[] the logo
   */
  @GetMapping(value = "/techdocs/logo", produces = MediaType.IMAGE_PNG_VALUE)
  @LogExecutionTimeWithArgs
  public ResponseEntity<byte[]> techpubsLogo(@RequestHeader(PORTAL_ID) String portalId) {

    if(sqlInjection) {
      portalId = SecurityEscape.cleanString(portalId);
    }

    return ResponseEntity
            .ok()
            .cacheControl(ControllerUtil.getSpringCacheControl())
            .body(iResourceData.getLogo(portalId));
  }

  /**
   * techpubsBannerBinaryResource service returns the image file for the particular engine program
   * selected by the user. This response returns the image document based on engine program.
   *
   * @param program the engine program
   * @return Response the image file
   * @throws TechpubsException the techpubs exceptions
   */
  @GetMapping(value = "/techdocs/pgms/{program}/banner", produces = MediaType.IMAGE_JPEG_VALUE)
  @LogExecutionTimeWithArgs
  public ResponseEntity<byte[]> techpubsBannerBinaryResource(
          @RequestHeader(SM_SSOID) String ssoId,
          @RequestHeader(PORTAL_ID) String portalId,
          @ApiParam(name = "program", value = "eg. gek108749", allowMultiple = false, required = false) @PathVariable("program") String program) throws TechpubsException {

    if(sqlInjection) {
      ssoId = SecurityEscape.cleanString(ssoId);
      portalId = SecurityEscape.cleanString(portalId);
      program = SecurityEscape.cleanString(program);
    }

    byte[] binaryResource = iManualApp.getProgramBannerTD(ssoId, portalId, program);

    return ResponseEntity.ok(binaryResource);
  }
}
