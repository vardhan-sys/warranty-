package com.geaviation.techpubs.controllers.impl;

import static com.geaviation.techpubs.services.util.AppConstants.PORTAL_ID;
import static com.geaviation.techpubs.services.util.AppConstants.SM_SSOID;

import com.geaviation.techpubs.data.util.SecurityEscape;
import com.geaviation.techpubs.data.util.log.LogExecutionTimeWithArgs;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.DocumentModel;
import com.geaviation.techpubs.models.TocItemModelList;
import com.geaviation.techpubs.models.TocNodeModel;
import com.geaviation.techpubs.services.api.IManualApp;
import com.geaviation.techpubs.services.api.IProgramApp;
import com.geaviation.techpubs.services.util.TechpubsAppUtil;
import io.swagger.annotations.ApiParam;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

@Path("/")
@Service
@RefreshScope
public class OverlayTableContentControllerImpl {

  @Value("${techpubs.services.sqlInjection}")
  private boolean sqlInjection;

  @Autowired
  private IManualApp iManualApp;

  @Autowired
  private TechpubsAppUtil techpubsAppUtil;

  @Autowired
  private IProgramApp iProgramApp;


  /**
   * getDocumentsByParentTocId service returns the list of document for the particular engine
   * program, engine manual and parentNode Id selected by the user. This response returns the
   * document from toc.
   *
   * @param program      the engine program
   * @param manual       the engine manual
   * @param parentnodeid the parentnodeid
   * @param request      the httpservlet request
   * @return Response the documents by parent toc id
   * @throws TechpubsException the techpubs exceptions
   */
  @GET
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Path("/techdocs/pgms/{program}/mans/{manual}/toc/{parentnodeid}")
  @LogExecutionTimeWithArgs
  public Response getDocumentsByParentTocId(
      @ApiParam(name = "program", value = "eg. gek108748", allowMultiple = false, required = false) @PathParam("program") String program,
      @ApiParam(name = "manual", value = "eg. sbs", allowMultiple = false, required = false) @PathParam("manual") String manual,
      @ApiParam(name = "parentnodeid", value = "eg. 3299", allowMultiple = false, required = false) @PathParam("parentnodeid") String parentnodeid,
      @Context HttpServletRequest request) throws TechpubsException {

    if(sqlInjection) {
      program = SecurityEscape.cleanString(program);
      manual = SecurityEscape.cleanString(manual);
      parentnodeid = SecurityEscape.cleanString(parentnodeid);
    }

    DocumentModel documentModel = iManualApp
        .getDocumentsByParentTocIdTD(request.getHeader(SM_SSOID),
            request.getHeader(PORTAL_ID), program, manual, parentnodeid,
            techpubsAppUtil.getQueryParams(request));

    return Response.ok(documentModel).build();
  }

  /**
   * getContentByProgram service returns the list of document for the particular engine program
   * selected by the user. This response returns the document from toc based on engine program.
   *
   * @param program the engine program
   * @param ui      the ui parameters
   * @param request the httpservlet request
   * @return Response the content by program
   * @throws TechpubsException the techpubs exceptions
   */
  @GET
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Path("/toc/pgms/{program}")
  @LogExecutionTimeWithArgs
  public Response getContentByProgram(
      @ApiParam(name = "program", value = "eg. gek114118", allowMultiple = false, required = false) @PathParam("program") String program,
      @Context UriInfo ui, @Context HttpServletRequest request) throws TechpubsException {

    if(sqlInjection) {
      program = SecurityEscape.cleanString(program);
    }

    TocNodeModel tocNodeModel = iProgramApp.getContentByProgramTD(request.getHeader(SM_SSOID),
        request.getHeader(PORTAL_ID), program, techpubsAppUtil.getQueryParams(request));

    return Response.ok(tocNodeModel).build();
  }

  /**
   * getContentByManual service returns the list of document for the particular engine program and
   * engine manual selected by the user. This response returns the document from toc based on engine
   * program and engine manual.
   *
   * @param program the engine program
   * @param manual  the engine manual
   * @param ui      the ui parameters
   * @param request the httprequest request
   * @return Response the content by manual
   * @throws TechpubsException the techpubs exceptions
   */
  @GET
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Path("/toc/pgms/{program}/mans/{manual}")
  @LogExecutionTimeWithArgs
  public Response getContentByManual(
      @ApiParam(name = "program", value = "eg. gek114118", allowMultiple = false, required = false) @PathParam("program") String program,
      @ApiParam(name = "manual", value = "eg. sbs", allowMultiple = false, required = false) @PathParam("manual") String manual,
      @Context UriInfo ui, @Context HttpServletRequest request) throws TechpubsException {

    if(sqlInjection) {
      program = SecurityEscape.cleanString(program);
      manual = SecurityEscape.cleanString(manual);
    }

    TocItemModelList tocModel = iProgramApp.getContentByManualTD(request.getHeader(SM_SSOID),
        request.getHeader(PORTAL_ID), program, null, manual,
        techpubsAppUtil.getQueryParams(request));

    return Response.ok(tocModel).build();
  }

  /**
   * getContentByManual service returns the list of document for the particular engine program and
   * engine manual selected by the user. This response returns the document from toc based on engine
   * program and engine manual.
   *
   * @param program the engine program
   * @param version the program version
   * @param manual  the engine manual
   * @param ui      the ui parameters
   * @param request the httprequest request
   * @return Response the content by manual
   * @throws TechpubsException the techpubs exceptions
   */
  @GET
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Path("/toc/pgms/{program}/{version}/mans/{manual}")
//  @Authz(resource = "review-overlay")
  @LogExecutionTimeWithArgs
  public Response getContentByManual(
      @ApiParam(name = "program", value = "eg. gek114118", allowMultiple = false, required = false) @PathParam("program") String program,
      @ApiParam(name = "version", value = "eg. 1.2", allowMultiple = false, required = false) @PathParam("version") String version,
      @ApiParam(name = "manual", value = "eg. sbs", allowMultiple = false, required = false) @PathParam("manual") String manual,
      @Context UriInfo ui, @Context HttpServletRequest request) throws TechpubsException {

    if(sqlInjection) {
      program = SecurityEscape.cleanString(program);
      manual = SecurityEscape.cleanString(manual);
      version = SecurityEscape.cleanString(version);
    }

    TocItemModelList tocModel = iProgramApp.getContentByManualTD(request.getHeader(SM_SSOID),
        request.getHeader(PORTAL_ID), program, version, manual,
        techpubsAppUtil.getQueryParams(request));
    return Response.ok(tocModel).build();
  }

  /**
   * getContentByDocFile service returns the list of document for the particular engine program,
   * engine manual and filename selected by the user. This response returns the document from toc
   * based on engine program and engine manual.
   *
   * @param program the engine program
   * @param manual  the enigne manual
   * @param file    the file name
   * @param ui      the ui parameters
   * @param request the httprequest request
   * @return Response the content by doc file
   * @throws TechpubsException the techpubs exceptions
   */
  @GET
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Path("/toc/pgms/{program}/mans/{manual}/file/{file}")
  @LogExecutionTimeWithArgs
  public Response getContentByDocFile(
      @ApiParam(name = "program", value = "eg. gek114118", allowMultiple = false, required = false) @PathParam("program") String program,
      @ApiParam(name = "manual", value = "eg. sbs", allowMultiple = false, required = false) @PathParam("manual") String manual,
      @ApiParam(name = "file", value = "eg. genx-2b-sb-72-0249-r00.htm", allowMultiple = false, required = false) @PathParam("file") String file,
      @Context UriInfo ui, @Context HttpServletRequest request) throws TechpubsException {

    if(sqlInjection) {
      program = SecurityEscape.cleanString(program);
      manual = SecurityEscape.cleanString(manual);
      file = SecurityEscape.cleanString(file);
    }

    TocNodeModel tocNodeModel = iProgramApp.getContentByDocFile(request.getHeader(SM_SSOID),
        request.getHeader(PORTAL_ID), program, manual, file,
        techpubsAppUtil.getQueryParams(request));

    return Response.ok(tocNodeModel).build();
  }

  /**
   * getContentByDocFile service returns the list of documents for the particular engine program,
   * engine manual and filename selected by the user. This response returns the document from toc
   * based on engine program and engine manual.
   *
   * @param program the engine program
   * @param version the version of the program
   * @param manual  the enigne manual
   * @param file    the file name
   * @param ui      the ui parameters
   * @param request the httprequest request
   * @return Response the content by doc file
   * @throws TechpubsException the techpubs exceptions
   */
  @GET
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Path("/toc/pgms/{program}/{version}/mans/{manual}/file/{file}")
//  @Authz(resource = "review-overlay")
  @LogExecutionTimeWithArgs
  public Response getContentByDocFile(
      @ApiParam(name = "program", value = "eg. gek114118", allowMultiple = false, required = false) @PathParam("program") String program,
      @ApiParam(name = "version", value = "eg. 9.1", allowMultiple = false, required = false) @PathParam("version") String version,
      @ApiParam(name = "manual", value = "eg. sbs", allowMultiple = false, required = false) @PathParam("manual") String manual,
      @ApiParam(name = "file", value = "eg. genx-2b-sb-72-0249-r00.htm", allowMultiple = false, required = false) @PathParam("file") String file,
      @Context UriInfo ui, @Context HttpServletRequest request) throws TechpubsException {

    if(sqlInjection) {
      program = SecurityEscape.cleanString(program);
      version = SecurityEscape.cleanString(version);
      manual = SecurityEscape.cleanString(manual);
      file = SecurityEscape.cleanString(file);
    }

    TocNodeModel tocNodeModel = iProgramApp.getContentByDocFile(request.getHeader(SM_SSOID),
        request.getHeader(PORTAL_ID), program, version, manual, file,
        techpubsAppUtil.getQueryParams(request));

    return Response.ok(tocNodeModel).build();
  }

  /**
   * getContentByTocNodeId service returns the list of document for the particular engine program,
   * engine manual and parentNode Id selected by the user. This response returns the document from
   * toc based on engine program,manual and parentNodeId.
   *
   * @param program      the engine program
   * @param manual       the engine manual
   * @param parentnodeid the parentnodeid
   * @param ui           the ui parameters
   * @param request      the httpservlet request
   * @return Response the content by toc node id
   * @throws TechpubsException the techpubs exceptions
   */
  @GET
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Path("/toc/pgms/{program}/mans/{manual}/toc/{parentnodeid}")
  @LogExecutionTimeWithArgs
  public Response getContentByTocNodeId(
      @ApiParam(name = "program", value = "eg. gek114118", allowMultiple = false, required = false) @PathParam("program") String program,
      @ApiParam(name = "manual", value = "eg. sbs", allowMultiple = false, required = false) @PathParam("manual") String manual,
      @ApiParam(name = "parentnodeid", value = "eg. 3299", allowMultiple = false, required = false) @PathParam("parentnodeid") String parentnodeid,
      @Context UriInfo ui, @Context HttpServletRequest request) throws TechpubsException {

    if(sqlInjection) {
      program = SecurityEscape.cleanString(program);
      manual = SecurityEscape.cleanString(manual);
      parentnodeid = SecurityEscape.cleanString(parentnodeid);
    }

    TocNodeModel tocNodeModel = iProgramApp.getContentByTocNodeId(request.getHeader(SM_SSOID),
        request.getHeader(PORTAL_ID), program, manual, parentnodeid,
        techpubsAppUtil.getQueryParams(request));

    return Response.ok(tocNodeModel).build();
  }

}
