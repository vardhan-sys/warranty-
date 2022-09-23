package com.geaviation.techpubs.controllers.impl;

import static com.geaviation.techpubs.services.util.AppConstants.PORTAL_ID;
import static com.geaviation.techpubs.services.util.AppConstants.SM_SSOID;

import com.geaviation.techpubs.controllers.filter.Authz;
import com.geaviation.techpubs.controllers.util.ControllerUtil;
import com.geaviation.techpubs.data.api.IEntitlementData;
import com.geaviation.techpubs.data.impl.ResourceDataImpl;
import com.geaviation.techpubs.data.util.SecurityEscape;
import com.geaviation.techpubs.data.util.log.LogExecutionTimeWithArgs;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.ResourceMetaDataModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.services.api.IManualApp;
import com.geaviation.techpubs.services.api.IProgramApp;
import com.geaviation.techpubs.services.impl.BookcaseContentModelApp;
import com.geaviation.techpubs.services.impl.BookcaseTOCApp;
import com.geaviation.techpubs.services.util.AppConstants;
import com.geaviation.techpubs.services.util.TechpubsAppUtil;
import io.swagger.annotations.ApiParam;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

@Path("/")
@Service
@RefreshScope
public class UnusedEndpointsControllerImpl {
  //Remove this class during US478605 feature flag cleanup
  private static final Logger log = LogManager.getLogger(UnusedEndpointsControllerImpl.class);
  private static final String CONTENT_DISPOSITION = "Content-Disposition";

  @Value("${techpubs.services.sqlInjection}")
  private boolean sqlInjection;

  @Autowired
  private BookcaseContentModelApp bookcaseContentModelApp;

  @Autowired
  private IEntitlementData iEntitlementData;

  @Autowired
  private TechpubsAppUtil techpubsAppUtil;

  @Autowired
  private IManualApp iManualApp;

  @Autowired
  private BookcaseVersionControllerImpl bookcaseVersionController;

  @Autowired
  private IProgramApp iProgramApp;

  @Autowired
  private BookcaseTOCApp bookcaseTOCApp;

  @Autowired
  private ControllerUtil controllerUtil;

  @Autowired
  private ResourceDataImpl resourceDataImpl;

  /**
   * Returns list of documents based on type - either books or pageblks for documents in a bookcase
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/documents")
  @LogExecutionTimeWithArgs
  public Response getBookcaseContent(
      MultivaluedMap<String, String> formParm, @Context HttpServletRequest request)
      throws TechpubsException {

    String ssoId = request.getHeader(SM_SSOID);
    String portalId = request.getHeader(PORTAL_ID);

    if(sqlInjection) {
      ssoId = SecurityEscape.cleanString(ssoId);
      portalId = SecurityEscape.cleanString(portalId);
    }

    if (TechpubsAppUtil.isNullOrEmpty(ssoId) || TechpubsAppUtil.isNullOrEmpty(portalId)) {
      log.error(AppConstants.ERROR_MESSAGE);
      throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
    }

    Map<String, String> inputParmsMap = TechpubsAppUtil.convertMultiToRegularMap(formParm);

    // validate queryparms
    TechpubsAppUtil.validateDatatableParameters(inputParmsMap);
    Map<String, String> searchFilters = TechpubsAppUtil.getFilterFields(inputParmsMap);

    return Response.ok(bookcaseContentModelApp
        .getBookcaseItemModel(ssoId, portalId, searchFilters, inputParmsMap)).build();
  }


  /**
   * getAdminEntitlements service returns the cwcadmin entitlement for the current user. This
   * response returns list of cwcadmin tokens.
   *
   * @param request the HttpServletRequest
   * @return Response list of cwcadmin tokens
   */
  @GET
  @Produces({MediaType.TEXT_PLAIN})
  @Path("/getadminentitlements")
  @LogExecutionTimeWithArgs
  public Response getAdminEntitlements(@Context HttpServletRequest request) {
    List<String> adminTokens = iEntitlementData
        .getAdminEntitlements(request.getHeader(SM_SSOID));
    return Response.ok().entity(adminTokens.toString()).build();
  }

  /**
   * techpubsBinaryResource service returns the resource file for the particular engine program and
   * engine manual selected by the user. This response returns the resource file.
   * <p>
   * Produces({"application/pdf","image/png","image/gif","image/jpeg","image/tiff","model/vrml",
   * "image/svg+xml","image/cgm","video/mp4","video/avi"}) Note: The 'res' variable is separated by
   * ':' from a regular expression matching 1 to many characters This allows the '/' (slash)
   * character to be passed to the 'res' path param
   *
   * @param bookcase the bookcase of the engine program
   * @param version  the bookcase version
   * @param manual   the engine manual
   * @param res      the resource filename
   * @param request  the httpservlet request
   * @return Response the resource file
   * @throws TechpubsException the techpubs exceptions
   */
  @Path("/docs/pgms/{bookcase}/versions/{version}/mans/{manual}/res/{res:.+}")
  @Authz(resource = "review-overlay")
  public Response techpubsBinaryResourceDocs(
      @ApiParam(name = "bookcase", value = "eg. gek108786") @PathParam("bookcase") String bookcase,
      @ApiParam(name = "version", value = "eg. 2.3") @PathParam("version") String version,
      @ApiParam(name = "manual", value = "eg. gek109993") @PathParam("manual") String manual,
      @ApiParam(name = "res", value = "eg. g1206774-00.tif.png") @PathParam("res") String res,
      @Context HttpServletRequest request) throws TechpubsException {

    if(sqlInjection) {
      bookcase = SecurityEscape.cleanString(bookcase);
      version = SecurityEscape.cleanString(version);
      manual = SecurityEscape.cleanString(manual);
      res = SecurityEscape.cleanString(res);
    }

    byte[] binaryResource = iManualApp.getBinaryResourceTD(request.getHeader(SM_SSOID),
        request.getHeader(PORTAL_ID), bookcase, version, manual, res);
    return Response.ok(binaryResource).cacheControl(techpubsAppUtil.getCacheControl())
        .header(CONTENT_DISPOSITION, AppConstants.EQ_FILENAME + res + "\"")
        .type(controllerUtil.getResourceContentType(res)).build();
  }

  /**
   * techpubsResourceByFilename service returns the file for the particular version of the engine
   * program, engine manual and filename selected by the user. This response returns the HTML
   * document based on filename.
   *
   * @param bookcase the bookcase of the engine program
   * @param version  the version of the bookcase
   * @param manual   the engine manual
   * @param filename the filename
   * @param request  the httpservlet request
   * @return Response the html file
   * @throws TechpubsException the techpubs exceptions
   */
//  @GET
//  @Produces(MediaType.TEXT_HTML)
//  @Path("/docs/pgms/{bookcase}/versions/{version}/mans/{manual}/file/{filename}")
//  @Authz(resource = "review-overlay")
//  public Response techpubsResourceByFilenameLinker(
//      @ApiParam(name = "bookcase", value = "eg. gek114118") @PathParam("bookcase") String bookcase,
//      @ApiParam(name = "verion", value = " eg. 1.2") @PathParam("version") String version,
//      @ApiParam(name = "manual", value = "eg. sbs") @PathParam("manual") String manual,
//      @ApiParam(name = "filename", value = "eg. genx-2b-sb-72-0249-r00.htm") @PathParam("filename") String filename,
//      @Context HttpServletRequest request) throws TechpubsException {
//    return bookcaseVersionController
//        .techpubsResourceByFilename(bookcase, version, manual, filename, request);
//  }

  /**
   * getBookTOC service returns the contents of the book -> section -> pageblk levels corresponding
   * to the particular engine program selected by the user.
   */
  @GET
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Path("/toc/bookcases/{bookcase}/{book}")
  @LogExecutionTimeWithArgs
  public Response getBookTOC(
      @ApiParam(name = "program", value = "eg. gek114118", allowMultiple = false, required = false)
      @PathParam("bookcase") String bookcaseKey,
      @PathParam("book") String bookKey,
      @Context HttpServletRequest request) throws TechpubsException {

    if(sqlInjection) {
      bookcaseKey = SecurityEscape.cleanString(bookcaseKey);
      bookKey = SecurityEscape.cleanString(bookKey);
    }

    String ssoId = request.getHeader(SM_SSOID);
    String portalId = request.getHeader(PORTAL_ID);

    //Ensure user has access to bookcase
    if (ssoId == null || ssoId.isEmpty() || portalId == null || portalId.isEmpty() ||
        !iProgramApp.getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(bookcaseKey)) {
      log.error("SSO " + ssoId + " in portal " + portalId + " does not have access to bookcase "
          + bookcaseKey);
      throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
    }

    return Response.ok(bookcaseTOCApp.getBookcaseTOC(bookcaseKey, bookKey, Boolean.FALSE, null))
        .build();
  }

  /**
   * techpubsResourceByFilename service returns the file for the particular engine program, engine
   * manual and filename selected by the user. This response returns the HTML document based on
   * filename.
   *
   * @param program the engine program
   * @param manual  the engine manual
   * @param sbTag   the attribute used to find the relevant service bulletin
   * @param request the httpservlet request
   * @return Response the html file
   * @throws TechpubsException the techpubs exceptions
   */
  @GET
  @Produces(MediaType.TEXT_HTML)
  @Path("/techdocs/pgms/{program}/mans/{manual}/solosb/{sbTag}")
  @LogExecutionTimeWithArgs
  public Response techpubsSoloSBLinkFile(
      @ApiParam(name = "program", value = "eg. gek114118", allowMultiple = false, required = false) @PathParam("program") String program,
      @ApiParam(name = "manual", value = "eg. sbs", allowMultiple = false, required = false) @PathParam("manual") String manual,
      @ApiParam(name = "sbTag", value = "72-0047", allowMultiple = false, required = false) @PathParam("sbTag") String sbTag,
      @Context HttpServletRequest request) throws TechpubsException {

    if(sqlInjection) {
      program = SecurityEscape.cleanString(program);
      manual = SecurityEscape.cleanString(manual);
      sbTag = SecurityEscape.cleanString(sbTag);
    }

    String filename = resourceDataImpl.getSbFileNameWithCortonaLinkTag(program, sbTag);

    if (null != filename) {
      byte[] html = iManualApp
          .getHTMLResourceTD(request.getHeader(SM_SSOID), request.getHeader(PORTAL_ID), program,
              "sbs", filename, "low", false);
      return Response.ok(html).build();
    }

    return Response.ok(resourceDataImpl.displayDocumentNotFound().getBytes()).build();
  }

  /**
   * techpubsResourceSummaryByFilename service returns the summary file for the particular version
   * of an engine program, engine manual and filename selected by the user. This response returns
   * the summary HTML document based on filename.
   *
   * @param bookcase the bookcase of the engine program
   * @param manual   the engine manual
   * @param filename the filename
   * @param request  the httpservlet request
   * @return Response the summary file
   * @throws TechpubsException the techpubs exceptions
   */
  @GET
  @Produces(MediaType.TEXT_HTML)
  @Path("/techdocs/pgms/{bookcase}/versions/{version}/mans/{manual}/summary/{filename}")
  @Authz(resource = "review-overlay")
  public Response techpubsResourceSummaryByFilename(
      @ApiParam(name = "bookcase", value = "eg. gek114118") @PathParam("bookcase") String bookcase,
      @ApiParam(name = "version", value = "eg. 1.2") @PathParam("version") String version,
      @ApiParam(name = "manual", value = "eg. sbs") @PathParam("manual") String manual,
      @ApiParam(name = "filename", value = "eg. genx-2b-sb-72-0249-r00.htm") @PathParam("filename") String filename,
      @Context HttpServletRequest request) throws TechpubsException {

    if(sqlInjection) {
      bookcase = SecurityEscape.cleanString(bookcase);
      version = SecurityEscape.cleanString(version);
      manual = SecurityEscape.cleanString(manual);
      filename = SecurityEscape.cleanString(filename);
    }

    String html = iManualApp
        .getHTMLResourceSummaryTD(request.getHeader(SM_SSOID), request.getHeader(PORTAL_ID),
            bookcase, version, manual, filename);
    return Response.ok(html).build();

  }

  /**
   * Identifies if the resource requested is actually a cortona file or not to assist in the UI's
   * identification of the file rather than having it assume it's an html file always getting
   * returned to the service
   *
   * @param program  the engine program
   * @param manual   the engine manual
   * @param filename the filename
   * @param request  the httpservlet request
   * @return Response the html file
   * @throws TechpubsException the techpubs exceptions
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/techdocs/pgms/{program}/mans/{manual}/fileName/{filename}")
  @LogExecutionTimeWithArgs
  public Response techpubsGetFileExtension(
      @ApiParam(name = "program", value = "eg. gek114118", allowMultiple = false, required = false) @PathParam("program") String program,
      @ApiParam(name = "manual", value = "eg. sbs", allowMultiple = false, required = false) @PathParam("manual") String manual,
      @ApiParam(name = "filename", value = "eg. genx-2b-sb-72-0249-r00.htm", allowMultiple = false, required = false) @PathParam("filename") String filename,
      @Context HttpServletRequest request) throws TechpubsException {

    if(sqlInjection) {
      program = SecurityEscape.cleanString(program);
      manual = SecurityEscape.cleanString(manual);
      filename = SecurityEscape.cleanString(filename);
    }

    ResourceMetaDataModel fileData = iManualApp
        .getResourceName(request.getHeader(SM_SSOID), request.getHeader(PORTAL_ID), program,
            manual, null, filename, techpubsAppUtil.getQueryParams(request));

    return Response.ok(fileData).build();
  }

  /**
   * techpubsBinaryResource service returns the resource file for the particular engine program and
   * engine manual selected by the user. This response returns the resource file.
   * <p>
   * Produces({"application/pdf","image/png","image/gif","image/jpeg","image/tiff","model/vrml",
   * "image/svg+xml","image/cgm","video/mp4","video/avi"}) Note: The 'res' variable is separated by
   * ':' from a regular expression matching 1 to many characters This allows the '/' (slash)
   * character to be passed to the 'res' path param
   *
   * @param program the engine program
   * @param manual  the engine manual
   * @param res     the resource filename
   * @param request the httpservlet request
   * @return Response the resource file
   * @throws TechpubsException the techpubs exceptions
   */
  @GET
  @Path("/techdocs/pgms/{program}/mans/{manual}/res/{res:.+}")
  @LogExecutionTimeWithArgs
  public Response techpubsBinaryResource(
      @ApiParam(name = "program", value = "eg. gek108786", allowMultiple = false, required = false) @PathParam("program") String program,
      @ApiParam(name = "manual", value = "eg. gek109993", allowMultiple = false, required = false) @PathParam("manual") String manual,
      @ApiParam(name = "res", value = "eg. g1206774-00.tif.png", allowMultiple = false, required = false) @PathParam("res") String res,
      @Context HttpServletRequest request) throws TechpubsException {

    if(sqlInjection) {
      program = SecurityEscape.cleanString(program);
      manual = SecurityEscape.cleanString(manual);
      res = SecurityEscape.cleanString(res);
    }

    byte[] binaryResource = iManualApp.getBinaryResourceTD(request.getHeader(SM_SSOID),
        request.getHeader(PORTAL_ID), program, null, manual, res);
    return Response.ok(binaryResource)
        .header(CONTENT_DISPOSITION, AppConstants.EQ_FILENAME + res + "\"")
        .type(controllerUtil.getResourceContentType(res)).build();
  }

  /**
   * techpubsResourceByFilename service returns the file for the particular engine program, engine
   * manual and filename selected by the user. This response returns the HTML document based on
   * filename.
   *
   * @param program the engine program
   * @param manual  the engine manual
   * @param sbnbr   the filename
   * @param request the httpservlet request
   * @return Response the html file
   * @throws TechpubsException the techpubs exceptions
   */
  @GET
  @Produces(MediaType.TEXT_HTML)
  @Path("/techdocs/pgms/{program}/mans/{manual}/sb/{sbnbr}")
  @LogExecutionTimeWithArgs
  public Response techpubsSbResourceByFilename(
      @ApiParam(name = "program", value = "eg. gek114118", allowMultiple = false, required = false) @PathParam("program") String program,
      @ApiParam(name = "manual", value = "eg. sbs", allowMultiple = false, required = false) @PathParam("manual") String manual,
      @ApiParam(name = "sbnbr", value = "eg. 72-0249", allowMultiple = false, required = false) @PathParam("sbnbr") String sbnbr,
      @Context HttpServletRequest request) throws TechpubsException {

    if(sqlInjection) {
      program = SecurityEscape.cleanString(program);
      manual = SecurityEscape.cleanString(manual);
      sbnbr = SecurityEscape.cleanString(sbnbr);
    }

    Map<String, String> queryParams = techpubsAppUtil.getQueryParams(request);
    String bandwidth = "high".equalsIgnoreCase(queryParams.get("bw")) ? "high" : "low";
    boolean multiBrowserDocumentRequired =
        "Y".equalsIgnoreCase(queryParams.get("mbdr")) ? true : false;

    byte[] sbnbrFile = iManualApp
        .getSbResource(request.getHeader(SM_SSOID), request.getHeader(PORTAL_ID),
            manual, program, sbnbr, bandwidth, multiBrowserDocumentRequired);

    return Response.ok(sbnbrFile).build();
  }

  /**
   * techpubsBinaryResource service returns the resource file for the particular engine program and
   * engine manual selected by the user. This response returns the resource file.
   * <p>
   * Produces({"application/pdf","image/png","image/gif","image/jpeg","image/tiff","model/vrml",
   * "image/svg+xml","image/cgm","video/mp4","video/avi"}) Note: The 'res' variable is separated by
   * ':' from a regular expression matching 1 to many characters This allows the '/' (slash)
   * character to be passed to the 'res' path param
   *
   * @param program the engine program
   * @param manual  the engine manual
   * @param res     the resource filename
   * @param request the httpservlet request
   * @return Response the resource file
   * @throws TechpubsException the techpubs exceptions
   */
  @GET
  @Path("/techdocs/pgms/{program}/versions/{version}/mans/{manual}/OnlineFileName/{onlineFileName}/res/{res:.+}")
  @LogExecutionTimeWithArgs
  public Response techpubsBinarySMMResource(
      @ApiParam(name = "program", value = "eg. gek108786", allowMultiple = false, required = false) @PathParam("program") String program,
      @ApiParam(name = "version", value = "eg. 1.1", allowMultiple = false, required = false) @PathParam("version") String version,
      @ApiParam(name = "manual", value = "eg. gek109993", allowMultiple = false, required = false) @PathParam("manual") String manual,
      @ApiParam(name = "onlineFileName", value = "eg. pb72-00-00-09-003", allowMultiple = false, required = true) @PathParam("onlineFileName") String onlineFileName,
      @ApiParam(name = "res", value = "eg. g1206774-00.tif.png", allowMultiple = false, required = false) @PathParam("res") String res,
      @Context HttpServletRequest request) throws TechpubsException {

    if(sqlInjection) {
      program = SecurityEscape.cleanString(program);
      version = SecurityEscape.cleanString(version);
      manual = SecurityEscape.cleanString(manual);
      onlineFileName = SecurityEscape.cleanString(onlineFileName);
      res = SecurityEscape.cleanString(res);
    }

    byte[] binaryResource = iManualApp.getSMMBinaryResourceTD(request.getHeader(SM_SSOID),
        request.getHeader(PORTAL_ID), program, version, manual, res, onlineFileName);
    return Response.ok(binaryResource)
        .header(CONTENT_DISPOSITION, AppConstants.EQ_FILENAME + res + "\"")
        .type(controllerUtil.getResourceContentType(res)).build();
  }


}
