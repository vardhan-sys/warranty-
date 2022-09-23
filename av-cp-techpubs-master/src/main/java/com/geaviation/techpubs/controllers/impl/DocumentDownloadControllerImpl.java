package com.geaviation.techpubs.controllers.impl;

import com.geaviation.techpubs.controllers.util.Constants;
import com.geaviation.techpubs.controllers.util.ControllerUtil;
import com.geaviation.techpubs.data.impl.AwsResourcesService;
import com.geaviation.techpubs.data.util.SecurityEscape;
import com.geaviation.techpubs.data.util.log.LogExecutionTimeWithArgs;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.DocumentDataTableModel;
import com.geaviation.techpubs.models.DocumentDownloadModel;
import com.geaviation.techpubs.models.ManualItemModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.models.download.OverlayDownloadRequest;
import com.geaviation.techpubs.models.download.search.PsvcSearchRequestRestObj;
import com.geaviation.techpubs.models.download.search.PsvcSearchRequester;
import com.geaviation.techpubs.models.download.search.TechpubsExcelExporter;
import com.geaviation.techpubs.models.download.search.TechpubsSearchExcelEntry;
import com.geaviation.techpubs.services.api.IDocLLApp;
import com.geaviation.techpubs.services.api.IDocSMApp;
import com.geaviation.techpubs.services.api.IDocSubSystemApp;
import com.geaviation.techpubs.services.api.IDocTPApp;
import com.geaviation.techpubs.services.api.IDocVIApp;
import com.geaviation.techpubs.services.api.IManualApp;
import com.geaviation.techpubs.services.api.IProgramApp;
import com.geaviation.techpubs.services.impl.DocAppRegServices;
import com.geaviation.techpubs.services.impl.download.OverlayDownload;
import com.geaviation.techpubs.services.impl.download.PdfDownloadImpl;
import com.geaviation.techpubs.services.util.AppConstants;
import com.geaviation.techpubs.services.util.TechpubsAppUtil;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.geaviation.techpubs.services.util.AppConstants.PORTAL_ID;
import static com.geaviation.techpubs.services.util.AppConstants.SM_SSOID;

@RestController
@RefreshScope
public class DocumentDownloadControllerImpl {

    @Value("${DEPLOYEDVERSION}")
    private String deployedVersion;

    @Value("${PDF.HTMLDIRECTURL}")
    protected String directHtmlURL;

    @Value("${PSVC.SEARCH.URL}")
    private String searchUrl;

    @Value("${techpubs.services.sqlInjection}")
    private boolean sqlInjection;


    private static final String ATTACHMENT_FILENAME = "attachment; filename=\"";

    private static final String CONTENT_DISPOSITION = "Content-Disposition";

    private static final Logger log = LogManager.getLogger(DocumentDownloadControllerImpl.class);

    private static final String DATE_FORMAT = "yyyyMMddhhmmssSSS";

    private static final String MEDIA_TYPE_APPLICATION_ZIP = "application/zip";

    @Autowired
    private AwsResourcesService awsResourcesService;

    @Autowired
    private TechpubsAppUtil techpubsAppUtil;

    @Autowired
    private DocAppRegServices docAppRegServices;

    @Autowired
    private IProgramApp iProgramApp;

    @Autowired
    private IManualApp iManualApp;

    @Autowired
    private PdfDownloadImpl pdfDownload;

    @Autowired
    private OverlayDownload overlayDownload;

    @Autowired
    private ControllerUtil controllerUtil;


    /**
     * getDownloadResource service returns the zip file for the particular file id and document type
     * selected by the user. This response returns the Zip file document.
     *
     * @param files the file id
     * @param type  the document type
     * @return Response the download zip files
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/download/files", produces = {"application/zip", MediaType.APPLICATION_JSON_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity getDownloadResource(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "files", value = "eg. 579b907750a6476d75698430", allowMultiple = false, required = false) @RequestParam("files") String files,
            @ApiParam(name = "type", value = "eg. cmm", allowMultiple = false, required = false) @RequestParam("type") String type) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            files = SecurityEscape.cleanString(files);
            type = SecurityEscape.cleanString(type);
        }

        SubSystem subSystem = techpubsAppUtil.getSubSystem(type);
        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(subSystem);
        if (iDocSubSystemApp == null) {
            log.error("getDownloadResource (" + TechpubsException.TechpubsAppError.INVALID_PARAMETER
                    .getErrorCode()
                    + ") - " + TechpubsException.TechpubsAppError.INVALID_PARAMETER.getErrorMsg() + "("
                    + AppConstants.TYPE + "=" + subSystem + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }
        DocumentDownloadModel documentDownload = iDocSubSystemApp.getDownloadResource(ssoId, portalId, files);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + documentDownload.getZipFilename() + "\"");
        ResponseEntity response = null;
        if (documentDownload.getZipFileByteArray() != null) {
            response = ResponseEntity.ok()
                    .headers(responseHeaders)
                    .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_ZIP))
                    .body(documentDownload.getZipFileByteArray());
        } else {
            response = ResponseEntity.ok()
                    .contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE))
                    .body(documentDownload);
        }
        return response;
    }

    /**
     * getDownloadCSV service returns the csv file for the particular engine model and document type
     * selected by the user. This response returns the csv file document with list of downloadable
     * documents.
     *
     * @param model the engine model
     * @param type  the document type
     * @return Response the download csv files
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/download/pgms/mods/{model}/csv", produces = {"text/csv"})
    @LogExecutionTimeWithArgs
    public ResponseEntity<Object> getDownloadCSV(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "model", value = "eg. GE90", allowMultiple = false, required = false) @PathVariable("model") String model,
            @ApiParam(name = "type", value = "eg. cmm", allowMultiple = false, required = false) @RequestParam("type") String type,
            @RequestParam Map<String, String> requestParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            model = SecurityEscape.cleanString(model);
            type = SecurityEscape.cleanString(type);
            requestParams = SecurityEscape.cleanMap(requestParams);
        }

        SubSystem subSystem = techpubsAppUtil.getSubSystem(type);
        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(subSystem);
        File csvFile = iDocSubSystemApp
                .getDownloadCSV(ssoId, portalId, model,
                        null, null, requestParams);
        ResponseEntity response = null;

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + csvFile.getName() + "\"");
        String contentType = controllerUtil.getResourceContentType(csvFile.getName());

        if (csvFile != null) {
            response = ResponseEntity.ok()
                    .headers(responseHeaders)
                    .contentType(MediaType.valueOf(contentType))
                    .body(new FileSystemResource(csvFile));

        } else {
            response = ResponseEntity.ok(Constants.NO_DATA_TO_DOWNLOAD);
        }
        return response;
    }

    /**
     * getDownload service returns the download list for the particular engine model and document
     * type selected by the user. This response returns the list of downloadable documents.
     *
     * @param model the engine model
     * @param type  the document type
     * @return Response the list of downloadable document
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/download/pgms/mods/{model}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity<DocumentDataTableModel> getDownload(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "model", value = "eg. GE90", allowMultiple = false, required = false) @PathVariable("model") String model,
            @ApiParam(name = "type", value = "eg. fh", allowMultiple = false, required = false) @RequestParam("type") String type,
            @RequestParam Map<String, String> requestParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            model = SecurityEscape.cleanString(model);
            type = SecurityEscape.cleanString(type);
            requestParams = SecurityEscape.cleanMap(requestParams);
        }

        SubSystem subSystem = techpubsAppUtil.getSubSystem(type);
        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(subSystem);
        DocumentDataTableModel documentDataTableModel = iDocSubSystemApp
                .getDownload(ssoId, portalId, model, null, null, requestParams);

        return ResponseEntity.ok(documentDataTableModel);
    }

    /**
     * getDownloadVI service returns the download list for the particular family and VI document
     * type selected by the user. This response returns the list of downloadable VI documents.
     *
     * @param family  the engine family
     * @param doctype the document type
     * @return Response the list of VI documents
     * @throws TechpubsException the techpubs exceptions
     */
    //TODO TEST THIS ONCE YOU GET VALUES FROM SARITHA
    @GetMapping(value = "/techdocs/download/pgms/fams/{family}/{doctype}/vi", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity<DocumentDataTableModel> getDownloadVI(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "family", value = "eg. CFM56", allowMultiple = false, required = false) @PathVariable("family") String family,
            @ApiParam(name = "doctype", value = "eg. Contact Information", allowMultiple = false, required = false) @PathVariable("doctype") String doctype,
            @RequestParam Map<String, String> requestParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            family = SecurityEscape.cleanString(family);
            doctype = SecurityEscape.cleanString(doctype);
            requestParams = SecurityEscape.cleanMap(requestParams);
        }

        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(SubSystem.VI);
        DocumentDataTableModel documentDataTableModel = iDocSubSystemApp
                .getDownload(ssoId, portalId, null, family, doctype, requestParams);
        return ResponseEntity.ok(documentDataTableModel);

    }

    /**
     * getDownloadVICsv service returns the csv file for the particular family and VI document type
     * selected by the user. This response returns csv file with the list of downloadable VI
     * documents.
     *
     * @param family  the engine family
     * @param doctype the document type
     * @return Response the download VI csv
     * @throws TechpubsException the techpubs exceptions
     */
    //TODO GET VARIABLES FROM SARITHA
    @GetMapping(value = "/techdocs/download/pgms/fams/{family}/{doctype}/vi/csv", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity getDownloadVICsv(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "family", value = "eg. CFM56", allowMultiple = false, required = false) @PathVariable("family") String family,
            @ApiParam(name = "doctype", value = "eg. Contact Information", allowMultiple = false, required = false) @PathVariable("doctype") String doctype,
            @RequestParam Map<String, String> requestParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            family = SecurityEscape.cleanString(family);
            doctype = SecurityEscape.cleanString(doctype);
            requestParams = SecurityEscape.cleanMap(requestParams);
        }

        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(SubSystem.VI);
        File csvFile = iDocSubSystemApp
                .getDownloadCSV(ssoId, portalId, null,
                        family, doctype, requestParams);

        ResponseEntity response = null;
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + csvFile.getName() + "\"");
        String contentType = controllerUtil.getResourceContentType(csvFile.getName());

        if (csvFile != null) {
            response = ResponseEntity.ok()
                    .headers(responseHeaders)
                    .contentType(MediaType.valueOf(contentType))
                    .body(new FileSystemResource(csvFile));

        } else {
            response = ResponseEntity.ok(Constants.NO_DATA_TO_DOWNLOAD);
        }
        return response;

    }

    /**
     * getDownloadVITypes service returns the download types of Vi for particular family and VI
     * document type selected by the user. This response returns the download types of Vi.
     *
     * @param family the engine family
     * @return Response the list of VI types
     * @throws TechpubsException the techpubs exceptions
     */
    //TODO Not sure if this endpoint is valid anymore...I can't find any working values.
    @GetMapping(value = "/techdocs/download/pgms/fams/{family}/vi/doctypes", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity<DocumentDataTableModel> getDownloadVITypes(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "family", value = "eg. CFM56", allowMultiple = false, required = false) @PathVariable("family") String family,
            @RequestParam Map<String, String> requestParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            family = SecurityEscape.cleanString(family);
            requestParams = SecurityEscape.cleanMap(requestParams);
        }

        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(SubSystem.VI);
        DocumentDataTableModel documentDataTableModel = ((IDocVIApp) iDocSubSystemApp)
                .getDownloadVIDocTypes(ssoId, portalId, family, requestParams);

        return ResponseEntity.ok(documentDataTableModel);
    }

    /**
     * getDownloadTPCSV service returns the csv file for the particular engine family and TP
     * document type selected by the user. This response returns csv file with the list of
     * downloadable TP documents.
     *
     * @param model    the engine model
     * @param category the TP category
     * @return Response the download TP csv
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/download/pgms/mods/{model}/{category}/tp/csv", produces = {"text/csv", MediaType.TEXT_PLAIN_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity getDownloadTPCSV(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "model", value = "eg. GENX-1B", allowMultiple = false, required = false) @PathVariable("model") String model,
            @ApiParam(name = "category", value = "eg. 2", allowMultiple = false, required = false) @PathVariable("category") Integer category,
            @RequestParam Map<String, String> requestParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            model = SecurityEscape.cleanString(model);
            requestParams = SecurityEscape.cleanMap(requestParams);
        }

        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(SubSystem.TP);
        String categoryAsString = category.toString();
        File csvFile = ((IDocTPApp) iDocSubSystemApp).getDownloadTPCSV(ssoId, portalId, model, categoryAsString, requestParams);

        ResponseEntity response = null;
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + csvFile.getName() + "\"");
        String contentType = controllerUtil.getResourceContentType(csvFile.getName());


        if (csvFile != null) {
            response = response.ok()
                    .headers(responseHeaders)
                    .contentType(MediaType.valueOf(contentType))
                    .body(new FileSystemResource(csvFile));
        } else {
            response = response.ok(Constants.NO_DATA_TO_DOWNLOAD);
        }
        return response;
    }

    /**
     * getDownloadTP service returns the download list for the particular engine model and category
     * selected by the user. This response returns the list of downloadable TP documents.
     *
     * @param model    the engine model
     * @param category the TP category
     * @return Response list of downloadable document
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/download/pgms/mods/{model}/{category}/tp", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity<DocumentDataTableModel> getDownloadTP(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "model", value = "eg. GENX-1B", allowMultiple = false, required = false) @PathVariable("model") String model,
            @ApiParam(name = "category", value = "eg. 2", allowMultiple = false, required = false) @PathVariable("category") Integer category,
            @RequestParam Map<String, String> requestParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            model = SecurityEscape.cleanString(model);
            requestParams = SecurityEscape.cleanMap(requestParams);
        }

        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(SubSystem.TP);
        String categoryAsString = category.toString();
        DocumentDataTableModel documentDataTableModel = ((IDocTPApp) iDocSubSystemApp)
                .getDownloadTP(ssoId, portalId, model, categoryAsString, requestParams);

        return ResponseEntity.ok(documentDataTableModel);
    }

    /**
     * getDownloadSM service returns the download list for the particular engine model and doctype
     * selected by the user. This response returns the list of downloadable SM documents.
     *
     * @param model   the engine model
     * @param doctype the document type
     * @return Response the list of SM documents
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/download/pgms/mods/{model}/{doctype}/sm", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity<DocumentDataTableModel> getDownloadSM(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "model", value = "eg. GE90", allowMultiple = false, required = false) @PathVariable("model") String model,
            @ApiParam(name = "doctype", value = "eg. FSE TPP Scorecard", allowMultiple = false, required = false) @PathVariable("doctype") String doctype,
            @RequestParam Map<String, String> requestParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            model = SecurityEscape.cleanString(model);
            doctype = SecurityEscape.cleanString(doctype);
            requestParams = SecurityEscape.cleanMap(requestParams);
        }

        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(SubSystem.SM);
        DocumentDataTableModel documentDataTableModel = ((IDocSMApp) iDocSubSystemApp)
                .getDownloadSM(ssoId, portalId, model, doctype, requestParams);
        return ResponseEntity.ok(documentDataTableModel);
    }

    /**
     * getDownloadLL service returns the download list for the particular family and LL document
     * type selected by the user. This response returns the list of downloadable LL documents.
     *
     * @param category the LL category
     * @return Response the list of downloadable LL document
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/download/{category}/ll", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity<DocumentDataTableModel> getDownloadLL(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "category", value = "eg. 2", allowMultiple = false, required = false) @PathVariable("category") String category,
            @RequestParam Map<String, String> requestParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            category = SecurityEscape.cleanString(category);
            requestParams = SecurityEscape.cleanMap(requestParams);
        }

        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(SubSystem.LL);
        DocumentDataTableModel documentDataTableModel = ((IDocLLApp) iDocSubSystemApp)
                .getDownloadLL(ssoId, portalId, category, requestParams);

        return ResponseEntity.ok(documentDataTableModel);
    }

    /**
     * getDownloadSMCSV service returns the csv file for the particular engine family and SM
     * document type selected by the user. This response returns csv file with the list of
     * downloadable SM documents.
     *
     * @param model   the engine model
     * @param doctype the document type
     * @return Response the list of SM documents
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/download/pgms/mods/{model}/{doctype}/sm/csv", produces = {"text/csv", MediaType.TEXT_PLAIN_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity getDownloadSMCSV(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "model", value = "eg. GE90", allowMultiple = false, required = false) @PathVariable("model") String model,
            @ApiParam(name = "doctype", value = "eg. FSE TPP Scorecard", allowMultiple = false, required = false) @PathVariable("doctype") String doctype,
            @RequestParam Map<String, String> requestParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            model = SecurityEscape.cleanString(model);
            doctype = SecurityEscape.cleanString(doctype);
            requestParams = SecurityEscape.cleanMap(requestParams);
        }

        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(SubSystem.SM);
        File csvFile = iDocSubSystemApp.getDownloadCSV(ssoId, portalId, model, null, doctype, requestParams);

        ResponseEntity response = null;
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + csvFile.getName() + "\"");
        String contentType = controllerUtil.getResourceContentType(csvFile.getName());

        if (csvFile != null) {
            response = response.ok()
                    .headers(responseHeaders)
                    .contentType(MediaType.valueOf(contentType))
                    .body(new FileSystemResource(csvFile));
        } else {
            response = ResponseEntity.ok(Constants.NO_DATA_TO_DOWNLOAD);
        }
        return response;
    }

    /**
     * getDownloadDocuments service returns the download list by program, downloadtype (dvd,source),
     * type (manual,ic,tr,sb) for query params This response returns the list of downloadable
     * documents.
     *
     * @param program      the engine program
     * @param downloadtype the download type
     * @param type         the document type
     * @return Response the list of download documents
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/download/pgms/{program}/{downloadtype}/{type}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity<DocumentDataTableModel> getDownloadDocuments(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "program", value = "eg. gek112090", allowMultiple = false, required = false) @PathVariable("program") String program,
            @ApiParam(name = "downloadtype", value = "eg. source", allowMultiple = false, required = false) @PathVariable("downloadtype") String downloadtype,
            @ApiParam(name = "type", value = "eg. ic", allowMultiple = false, required = false) @PathVariable("type") String type,
            @RequestParam Map<String, String> requestParams) throws TechpubsException, InterruptedException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            program = SecurityEscape.cleanString(program);
            downloadtype = SecurityEscape.cleanString(downloadtype);
            type = SecurityEscape.cleanString(type);
            requestParams = SecurityEscape.cleanMap(requestParams);
        }

        DocumentDataTableModel documentDataTableModel = iProgramApp
                .getDownloadDocuments(ssoId, portalId, program, downloadtype, type, requestParams);

        return ResponseEntity.ok(documentDataTableModel);
    }

    /**
     * Gets the download documents CSV. Return CSV file of downloadable documents by program,
     * downloadtype (dvd,source), type (manual,ic,tr,sb,lr) for query params
     *
     * @param program      the engine program
     * @param downloadtype the download type
     * @param type         the document type
     * @return Response the download documents CSV
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/download/pgms/{program}/{downloadtype}/{type}/csv", produces = {"text/csv", MediaType.TEXT_PLAIN_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity<FileSystemResource> getDownloadDocumentsCSV(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "program", value = "eg. gek112865", allowMultiple = false, required = false) @PathVariable("program") String program,
            @ApiParam(name = "downloadtype", value = "eg. dvd", allowMultiple = false, required = false) @PathVariable("downloadtype") String downloadtype,
            @ApiParam(name = "type", value = "eg. ic", allowMultiple = false, required = false) @PathVariable("type") String type,
            @RequestParam Map<String, String> requestParams) throws TechpubsException, IOException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            program = SecurityEscape.cleanString(program);
            downloadtype = SecurityEscape.cleanString(downloadtype);
            type = SecurityEscape.cleanString(type);
            requestParams = SecurityEscape.cleanMap(requestParams);
        }

        File csvFile = iProgramApp
                .getDownloadDocumentsCSV(ssoId, portalId, program, downloadtype, type, requestParams);

        ResponseEntity<FileSystemResource> response = null;
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + csvFile.getName() + "\"");
        String contentType = ControllerUtil.getResourceContentType(csvFile.getName());

        if (sqlInjection) {
            SecurityEscape.unescapeFileContent(csvFile);
        }

        if (csvFile != null) {
            response = ResponseEntity.ok()
                    .headers(responseHeaders)
                    .contentType(MediaType.valueOf(contentType))
                    .body(new FileSystemResource(csvFile));
        } else {
            response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return response;
    }

    /**
     * getDownloadResource service returns the zip file by program, downloadtype (dvd,source), type
     * (manual,ic,tr,sb) for query params This response returns the zip file.
     *
     * @param program      the engine program
     * @param downloadtype the download type
     * @param type         the document type
     * @param files        the file name
     * @return Response the download resource
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/download/pgms/{program}/{downloadtype}/{type}/files", produces = {"application/zip", MediaType.APPLICATION_JSON_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity getDownloadResource(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "program", value = "eg. gek112865", allowMultiple = false, required = false) @PathVariable("program") String program,
            @ApiParam(name = "downloadtype", value = "eg. source", allowMultiple = false, required = false) @PathVariable("downloadtype") String downloadtype,
            @ApiParam(name = "type", value = "eg. ic", allowMultiple = false, required = false) @PathVariable("type") String type,
            @ApiParam(name = "files", value = "eg. ", allowMultiple = false, required = false) @RequestParam("files") String files) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            program = SecurityEscape.cleanString(program);
            downloadtype = SecurityEscape.cleanString(downloadtype);
            type = SecurityEscape.cleanString(type);
        }

        DocumentDownloadModel documentDownload = iProgramApp.getDownloadResourceTD(ssoId, portalId, program, downloadtype, type, files);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + documentDownload.getZipFilename() + "\"");
        ResponseEntity response = null;
        if (documentDownload.getZipFileByteArray() != null) {
            response = ResponseEntity.ok()
                    .headers(responseHeaders)
                    .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_ZIP))
                    .body(documentDownload.getZipFileByteArray());
        } else {
            response = ResponseEntity.ok()
                    .contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE))
                    .body(documentDownload);
        }
        return response;

    }

    /**
     * Retrieves selected files, zips them up, and returns them to the user.
     *
     * @param request Model of header params, path params, and query params
     * @return Response the download resource
     * @throws TechpubsException The TechpubsException
     */
    @GetMapping(value = "/v2/techdocs/download/pgms/{program}/{downloadType}/{type}/files", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity<StreamingResponseBody> getDownloadResourceV2(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            OverlayDownloadRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
        }

        // Shouldn't be able to call service when not requesting any files to download
        if (request.fileCount() == 0) {
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }

        StreamingResponseBody responseBody = overlayDownload.downloadResources(request);
        String fileName = overlayDownload.zipFileName(ssoId, request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename(fileName).build());

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(responseBody);
    }

    /**
     * getDownloadManuals service returns the list of downloadable manuals by engine program,
     * downloadtype (dvd,source), type (manual,ic,tr,sb) for query params This response returns the
     * list of manuals.
     *
     * @param program      the engine program
     * @param downloadtype the downloadtype
     * @param type         the type
     * @return Response the list of download manuals
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/manual/download/pgms/{program}/{downloadtype}/{type}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity<List<ManualItemModel>> getDownloadManuals(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "program", value = "eg. gek108748", allowMultiple = false, required = false) @PathVariable("program") String program,
            @ApiParam(name = "downloadtype", value = "eg. source", allowMultiple = false, required = false) @PathVariable("downloadtype") String downloadtype,
            @ApiParam(name = "type", value = "eg. vi", allowMultiple = false, required = false) @PathVariable("type") String type,
            @RequestParam Map<String, String> requestParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            program = SecurityEscape.cleanString(program);
            downloadtype = SecurityEscape.cleanString(downloadtype);
            type = SecurityEscape.cleanString(type);
            requestParams = SecurityEscape.cleanMap(requestParams);
        }

        List<ManualItemModel> manualList = iManualApp.getDownloadManuals(ssoId, portalId, program, downloadtype, type, requestParams);

        return ResponseEntity.ok(manualList);
    }

    /**
     * getDownloadLLCSV service returns the csv file for the particular family and LL document type
     * selected by the user. This response returns csv file with the list of downloadable LL
     * documents.
     *
     * @param category the LL category
     * @return Response download csv files
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/download/{category}/ll/csv", produces = {"text/csv", MediaType.TEXT_PLAIN_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity getDownloadLLCSV(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "category", value = "eg. 4", allowMultiple = false, required = false) @PathVariable("category") String category,
            @RequestParam Map<String, String> requestParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            category = SecurityEscape.cleanString(category);
            requestParams = SecurityEscape.cleanMap(requestParams);
        }

        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(SubSystem.LL);
        File csvFile = ((IDocLLApp) iDocSubSystemApp).getDownloadLLCSV(ssoId, portalId, category, requestParams);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + csvFile.getName() + "\"");
        ResponseEntity response = null;

        if (csvFile != null) {
            response = response.ok()
                    .headers(responseHeaders)
                    .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_ZIP))
                    .body(new FileSystemResource(csvFile));
        } else {
            response = ResponseEntity.ok("No data to download");
        }
        return response;
    }

    /**
     * getDVDFileList service returns the download list of dvd files by program, , revision(Current,
     * Previous,suspended,All) for query params This response returns the list of filenames.
     *
     * @param program the engine program * @param request the httpservlet request
     * @return Response the list of filenames
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/download/pgms/{program}/offlineviewer", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity<DocumentDataTableModel> getDVDFileList(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "program", value = "eg. gek112865", allowMultiple = false, required = false) @PathVariable("program") String program,
            @RequestParam Map<String, String> requestParams) throws TechpubsException, IOException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            program = SecurityEscape.cleanString(program);
            requestParams = SecurityEscape.cleanMap(requestParams);
        }

        DocumentDataTableModel dvdDataTableModel = iProgramApp.getDVDFileList(ssoId, portalId, program, requestParams);

        return ResponseEntity.ok(dvdDataTableModel);

    }

    /**
     * getCloudFrontCookies service returns signed cookies required for users to interact with the
     * Tech Pubs CloudFront Distribution.
     *
     * @param program the program to limit the scope of the cookie.
     * @return Response response object containing signed cookies for CloudFront as well as
     * encrypted cookie with user object-level permissions.
     */
    @GetMapping(value = "/techdocs/cloudfront/{program}", produces = MediaType.APPLICATION_JSON_VALUE)
    @LogExecutionTimeWithArgs
    public ResponseEntity getCloudFrontCookies(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "program", value = "eg. gek112865", allowMultiple = false, required = false) @PathVariable("program") String program,
            @RequestParam Map<String, String> requestParams) {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            program = SecurityEscape.cleanString(program);
        }

        ResponseEntity response = null;

        try {
            // Ensure user has access to program
            if (!iProgramApp.getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(program)) {
                log.error("getCloudFrontCookies - User does not have access to engine program.");
                throw new TechpubsException(
                        TechpubsException.TechpubsAppError.NO_PROGRAMS_AVAILABLE);
            }

            response = awsResourcesService.generateCloudFrontCookieResponse(portalId, program);

        } catch (TechpubsException e) {
            log.error("Error retrieving Signed CloudFront Cookies. " + e);
            response = ResponseEntity.ok("Error retrieving Signed CloudFront Cookies. " + e);
        }

        return response;
    }

//  /**
//   * downloadMultipleFiles service downloads one or multiple PDF files in zipped format.
//   * StreamingOutput is used to stream the output.
//   *
//   * @param pdfDocumentDownloadRequest contains the list of file information to download.
//   * @return Response response object contains downloaded PDFs in ZIP file format.
//   * @throws TechpubsException exception
//   */
//  @Deprecated
//  @PostMapping(value = "techdocs/download/pgms/files/pdf", consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/zip")
//  @LogExecutionTimeWithArgs
//  public ResponseEntity<StreamingResponseBody> downloadMultipleFiles(
//      @RequestHeader(SM_SSOID) String ssoId,
//      @RequestHeader(PORTAL_ID) String portalId,
//      @RequestBody List<PdfDocumentDownloadRequest> pdfDocumentDownloadRequest) throws TechpubsException {
//
//    if (TechpubsAppUtil.isNullOrEmpty(ssoId) || TechpubsAppUtil
//        .isNullOrEmpty(portalId)) {
//      log.error("User not Authorized error in downloadAndZipMultiplePdfFiles");
//      throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
//    }
//
//    if (pdfDocumentDownloadRequest.size() > 20) {
//      log.error("Reached maximum limit to download file");
//      throw new TechpubsException(
//          TechpubsException.TechpubsAppError.DOWNLOAD_MAX_LIMIT_REACHED_ERROR);
//    }
//
//    String fileName = String.format("PDF Download-%s.%s", StringUtils.getFormattedTimestamp(DATE_FORMAT), "zip");
//
//    StreamingResponseBody responseBody = pdfDownload.downloadAndZipMultiplePdfFiles(directHtmlURL,ssoId,portalId,pdfDocumentDownloadRequest);
//
//    HttpHeaders responseHeaders = new HttpHeaders();
//    responseHeaders.set(Constants.CONTENT_DISPSITION, ATTACHMENT_FILENAME + fileName + "\"");
//
//    return ResponseEntity.ok()
//            .headers(responseHeaders)
//            .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_ZIP))
//            .body(responseBody);
//
//
//  }

    /**
     * Returns excel sheet of results from an ElasticSearch query based on the following parameters
     *
     * @param psvcSearchRequestRestObj searchRequest is a JSON object in the form
     *                                 {"module":"inquiries","searchText":"blade",
     *                                 "facetQueries":[{"name":"status_exact","values":["In
     *                                 Process"]}],"limit":50,"sortField":{"field":null,"order":null}}
     *                                 Module refers to which Elasticsearch index is going to be
     *                                 searched searchText is what the user entered into the search
     *                                 box facetQueries are what have been selected from the check
     *                                 boxes or date pickers on the left in the UI
     * @return returns an excel sheet of search data
     */
    @PostMapping(value = "/techdocs/download/pgms/excel", consumes = MediaType.APPLICATION_JSON_VALUE)
    @LogExecutionTimeWithArgs
    public ResponseEntity searchExcelExport(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @RequestBody @Validated PsvcSearchRequestRestObj psvcSearchRequestRestObj) throws IOException, TechpubsException {

        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
        }

        List<Map<String, Object>> results = new PsvcSearchRequester().requestResults(psvcSearchRequestRestObj, ssoId, portalId, searchUrl);

        // convert results to array that's usable in creating the excel sheet
        ArrayList<TechpubsSearchExcelEntry> entriesToConvert = new ArrayList<>();

        boolean hasCategory = false;
        for (Map<String, Object> result : results) {
            // Sanitize date if null
            String date;
            String details;
            String category;
            if (null == result.get("date")) {
                date = "N/A";
            } else {
                date = result.get("date").toString();
            }

            if (null == result.get("details")) {
                details = "N/A";
            } else {
                details = result.get("details").toString();
            }

            if (null == result.get("category")) {
                category = "";
            } else {
                category = result.get("category").toString();
                hasCategory = true;
            }


            entriesToConvert.add(new TechpubsSearchExcelEntry(
                    result.get("title").toString(),
                    result.get("doc_type").toString(),
                    ((ArrayList<String>) result.get("engineFamilies")),
                    ((ArrayList<String>) result.get("engineModels")),
                    date,
                    category,
                    details));
        }

        // create excel file.
        ResponseEntity response = null;
        if (!entriesToConvert.isEmpty()) {

            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            String currentDateTime = dateFormatter.format(new Date());
            File excelFile = new File("./" + currentDateTime + "DocumentSearchExport.xlsx");
            FileOutputStream fileOutputStream = new FileOutputStream(excelFile);

            try {
                // creates exporter and exports document information into File
                TechpubsExcelExporter exporter = new TechpubsExcelExporter(entriesToConvert, hasCategory);
                exporter.excelExport(fileOutputStream);

                //Response headers
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "DocumentSearchExport_" + currentDateTime + ".xlsx\"");

                // build the response
                response = response.ok()
                        .headers(responseHeaders)
                        .contentType(MediaType.valueOf(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                        .body(FileUtils.readFileToByteArray(excelFile));

                // cleans up our temporary file after response is made
                boolean deletedFile = excelFile.delete();

                if (!deletedFile) {
                    log.error("excel file not deleted");
                }

            } catch (IOException e) {
                log.error("IOException while Exporting excel document", e);
                throw (e);
            } finally {
                fileOutputStream.close();
            }
        } else {
            // if there isn't a response body from elastic then we give the UI this message.
            response = ResponseEntity.ok("No data to download");
        }

        return response;
    }

}
