package com.geaviation.techpubs.controllers.impl;

import com.geaviation.techpubs.controllers.util.Constants;
import com.geaviation.techpubs.data.util.SecurityEscape;
import com.geaviation.techpubs.data.util.log.LogExecutionTimeWithArgs;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.DocumentModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.services.api.IDocCMMApp;
import com.geaviation.techpubs.services.api.IDocFHApp;
import com.geaviation.techpubs.services.api.IDocLLApp;
import com.geaviation.techpubs.services.api.IDocSMApp;
import com.geaviation.techpubs.services.api.IDocSubSystemApp;
import com.geaviation.techpubs.services.impl.DocAppRegServices;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.geaviation.techpubs.services.util.AppConstants.PORTAL_ID;
import static com.geaviation.techpubs.services.util.AppConstants.SM_SSOID;

@RestController
@RefreshScope
public class DocumentTypeControllerImpl {

    private static final Logger log = LogManager.getLogger(DocumentTypeControllerImpl.class);

    @Autowired
    private TechpubsAppUtil techpubsAppUtil;

    @Autowired
    private DocAppRegServices docAppRegServices;

    private static final String DATE_FORMAT = "yyyyMMddhhmmssSSS";

    @Value("${PDF.HTMLDIRECTURL}")
    protected String directHtmlURL;

    @Value("${techpubs.services.sqlInjection}")
    private boolean sqlInjection;

    /**
     * getResource service returns the resource for the particular file id and document type cmms.
     * This response contains document uri , src and content-type.
     *
     * @param fileId the file id
     * @return Response the resource
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/docs/cmms/{fileId}", produces = MediaType.TEXT_HTML_VALUE)
    @LogExecutionTimeWithArgs
    public ResponseEntity<String> getResourceDocsCmms(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "fileId", value = "eg. 574bfe699a5b8d110088ff26", allowMultiple = false, required = false) @PathVariable("fileId") String fileId,
            @RequestParam Map<String, String> requestParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            fileId = SecurityEscape.cleanString(fileId);
            requestParams = SecurityEscape.cleanMap(requestParams);
        }

        SubSystem subSystem = SubSystem.CMM;
        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(subSystem);
        String html = iDocSubSystemApp
                .getResource(ssoId, portalId, null, fileId, requestParams);
        return ResponseEntity.ok(html);
    }

    /**
     * getResource service returns the resource for the particular file id and document type
     * selected by the user. This response contains document uri , src and content-type.
     *
     * @param fileId the file id
     * @return Response the resource
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/docs/fhs/{fileId}", produces = MediaType.TEXT_HTML_VALUE)
    @LogExecutionTimeWithArgs
    public ResponseEntity<String> getLinkerDocsFhs(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "fileId", value = "eg. 574bfe699a5b8d110088ff26", allowMultiple = false, required = false) @PathVariable("fileId") String fileId,
            @RequestParam Map<String, String> requestParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            fileId = SecurityEscape.cleanString(fileId);
            requestParams = SecurityEscape.cleanMap(requestParams);
        }

        SubSystem subSystem = SubSystem.FH;
        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(subSystem);
        String html = iDocSubSystemApp
                .getResource(ssoId, portalId, null, fileId, requestParams);
        return ResponseEntity.ok(html);
    }

    /**
     * getResourcePrint service returns the print resource for the particular file id and document
     * type selected by the user. This response returns the PDF print document.
     *
     * @param fileId the file id
     * @param type   the document type
     * @return Response the CMM resource print
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/docs/fhs/{fileId}/pdf", produces = {MediaType.APPLICATION_PDF_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity<byte[]> getResourcePrintLinkerFHS(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "fileId", value = "eg. 57dc06f99a5b8d088c6816b4", allowMultiple = false, required = false) @PathVariable("fileId") String fileId,
            @ApiParam(name = "type", value = "eg. fh", allowMultiple = false, required = false) @RequestParam("type") String type) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            fileId = SecurityEscape.cleanString(fileId);
            type = SecurityEscape.cleanString(type);
        }

        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(SubSystem.FH);
        Map<String, Object> artifact = iDocSubSystemApp.getArtifact(ssoId, portalId, fileId);
        String fileName = (String) artifact.get("filename");
        String contentType = (String) ((Map<String, Object>) (artifact.get(Constants.METADATA))).get(Constants.TYPE);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, Constants.EQ_FILENAME + fileName + "\"");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .contentType(MediaType.valueOf(contentType))
                .body((byte[]) artifact.get(Constants.CONTENT));

    }

    /**
     * getCMMResourceTR service returns the CMM TR resource for the particular file id selected by
     * the user. This response contains document uri , src and content-type.
     *
     * @param fileId the file id
     * @return Response the CMM resource TR
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/cmms/tr/{fileId}", produces = {MediaType.TEXT_HTML_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity<String> getCMMResourceTR(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "fileId", value = "eg. 579b951c50a6476d7569c083", allowMultiple = false, required = false) @PathVariable("fileId") String fileId,
            @RequestParam Map<String, String> requestParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            fileId = SecurityEscape.cleanString(fileId);
            requestParams = SecurityEscape.cleanMap(requestParams);
        }

        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(SubSystem.CMM);
        String html = iDocSubSystemApp.getResource(ssoId, portalId, null, fileId, requestParams);

        return ResponseEntity.ok(html);

    }

    /**
     * getResource service returns the resource for the particular file id and document type tps.
     * This response contains document uri , src and content-type.
     *
     * @param fileId the file id
     * @return Response the resource
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/docs/tps/{fileId}", produces = MediaType.TEXT_HTML_VALUE)
    @LogExecutionTimeWithArgs
    public ResponseEntity<String> getResourceDocsTps(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "fileId", value = "eg. 574bfe699a5b8d110088ff26", allowMultiple = false, required = false) @PathVariable("fileId") String fileId,
            @RequestParam Map<String, String> requestParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            fileId = SecurityEscape.cleanString(fileId);
            requestParams = SecurityEscape.cleanMap(requestParams);
        }

        SubSystem subSystem = SubSystem.TP;
        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(subSystem);
        String html = iDocSubSystemApp.getResource(ssoId, portalId, null, fileId, requestParams);
        return ResponseEntity.ok(html);
    }

    /**
     * getCMMParts service return the html document for the particular file id and document type
     * selected by the user. This response contains the html document with CMM Parts Number list.
     *
     * @param fileId the file id
     * @return Response the CMM parts
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/cmms/{fileId}/parts", produces = MediaType.TEXT_HTML_VALUE)
    @LogExecutionTimeWithArgs
    public ResponseEntity<String> getCMMParts(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "fileId", value = "eg. 57c58e8b9a5b8d110051baf9", allowMultiple = false, required = false) @PathVariable("fileId") String fileId) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            fileId = SecurityEscape.cleanString(fileId);
        }

        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(SubSystem.CMM);
        String html = ((IDocCMMApp) iDocSubSystemApp).getCMMParts(ssoId, portalId, fileId);
        return ResponseEntity.ok(html);
    }

    /**
     * getCMMPartsPrint service returns the CMM print resource for the particular file id selected
     * by the user. This response returns the CMM PDF print document.
     *
     * @param fileId the file id
     * @return Response the CMM parts print
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/cmms/{fileId}/parts/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @LogExecutionTimeWithArgs
    public ResponseEntity<byte[]> getCMMPartsPrint(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "fileId", value = "eg. 57c58e8b9a5b8d110051baf9", allowMultiple = false, required = false)
            @PathVariable("fileId") String fileId) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            fileId = SecurityEscape.cleanString(fileId);
        }

        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(SubSystem.CMM);
        byte[] pdfByte = ((IDocCMMApp) iDocSubSystemApp).getCMMPartsPDF(directHtmlURL, ssoId, portalId, fileId);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION,
                Constants.EQ_FILENAME + fileId + "_" + TechpubsAppUtil.getFormattedTimestamp(DATE_FORMAT) + ".pdf\"");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body((pdfByte));

    }

    /**
     * getAssociatedDocumentsSM service returns the list of associated SM document for the
     * particular category selected by the user. This response returns the list of associated SM
     * document.
     *
     * @param category the SM category
     * @return Response the associated documents SM
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/sm/sct/{category}/associated", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity<DocumentModel> getAssociatedDocumentsSM(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "category", value = "eg. GEAC Airworthiness Directives", allowMultiple = false, required = false) @PathVariable("category") String category,
            @RequestParam Map<String, String> requestParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            category = SecurityEscape.cleanString(category);
            requestParams = SecurityEscape.cleanMap(requestParams);
        }

        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(SubSystem.SM);
        DocumentModel documentModel = ((IDocSMApp) iDocSubSystemApp).getAssociatedDocumentsSM(ssoId, portalId, category, requestParams);
        return ResponseEntity.ok(documentModel);
    }

    /**
     * getDocumentSMInit service returns the list of associated SM document for the particular
     * category selected by the user. This response returns the SM init document.
     *
     * @param category the SM category
     * @return response the document SM init
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/sm/sct/{category}/init", produces = MediaType.TEXT_HTML_VALUE)
    @LogExecutionTimeWithArgs
    public ResponseEntity<String> getDocumentSMInit(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "category", value = "eg. Illustrated Tools & Equipment Manuals", allowMultiple = false, required = false) @PathVariable("category") String category,
            @RequestParam Map<String, String> requestParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            category = SecurityEscape.cleanString(category);
            requestParams = SecurityEscape.cleanMap(requestParams);
        }

        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(SubSystem.SM);
        String html = ((IDocSMApp) iDocSubSystemApp).getSMResourceInit(ssoId, portalId, category, requestParams);

        return ResponseEntity.ok(html);
    }

    /**
     * getFHResourceSCT service returns the download list for the overall fileId and Sct fileId
     * selected by the user. This response returns the list of downloadable FH documents.
     *
     * @param mongoId the overall file id
     * @param fileId  the file id
     * @return Response list of downloadable FH document
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/fhs/{mongoId}/sct/{fileId}", produces = MediaType.TEXT_HTML_VALUE)
    @LogExecutionTimeWithArgs
    public ResponseEntity<String> getFHResourceSCT(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "mongoId", value = "eg. 581b45389a5b8d2d1336c31b", allowMultiple = false, required = false) @PathVariable("mongoId") String mongoId,
            @ApiParam(name = "fileId", value = "eg. 581b45389a5b8d2d1336c32a", allowMultiple = false, required = false) @PathVariable("fileId") String fileId,
            @RequestParam Map<String, String> requestParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            mongoId = SecurityEscape.cleanString(mongoId);
            fileId = SecurityEscape.cleanString(fileId);
            requestParams = SecurityEscape.cleanMap(requestParams);
        }

        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(SubSystem.FH);
        String html = ((IDocFHApp) iDocSubSystemApp).getFHResourceSCT(ssoId, portalId, mongoId, fileId, requestParams);

        return ResponseEntity.ok(html);
    }

    /**
     * getFHResourceSCTPrint service returns the Pdf Print resource for the overall fileId and Sct
     * fileId selected by the user. This response returns FH Pdf print document.
     *
     * @param mongoId the overall id of the mongo entry
     * @param fileId  the file id
     * @return Response the FHS print resource
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/fhs/{mongoId}/sct/{fileId}/pdf")
    @LogExecutionTimeWithArgs
    public ResponseEntity<Object> getFHResourceSCTPrint(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "mongoId", value = "eg. 581b45389a5b8d2d1336c31b", allowMultiple = false, required = false) @PathVariable("mongoId") String mongoId,
            @ApiParam(name = "fileId", value = "eg. 581b45389a5b8d2d1336c32a", allowMultiple = false, required = false) @PathVariable("fileId") String fileId) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            mongoId = SecurityEscape.cleanString(mongoId);
            fileId = SecurityEscape.cleanString(fileId);
        }

        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(SubSystem.FH);

        Map<String, Object> artifact = ((IDocFHApp) iDocSubSystemApp).getFHArtifactSCT(ssoId, portalId, mongoId, fileId);

        String fileName = (String) artifact.get(Constants.FILENAME);
        String contentType = (String) ((Map<String, Object>) (artifact.get(Constants.METADATA))).get(Constants.TYPE);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, Constants.EQ_FILENAME + fileName + "\"");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .contentType(MediaType.valueOf(contentType))
                .body(artifact.get(Constants.CONTENT));

    }

    /**
     * getFHResourceART service returns the download list for the overall fileId and Art fileId
     * selected by the user. This response returns the list of downloadable FH documents.
     *
     * @param mongoId the overall id of the mongo entry
     * @param fileId  the file id
     * @return Response list of downloadable FH document
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/fhs/{mongoId}/art/{fileId}", produces = MediaType.TEXT_HTML_VALUE)
    @LogExecutionTimeWithArgs
    public ResponseEntity<String> getFHResourceART(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "mongoId", value = "eg. 58c19f309a5b8d2d139db83d", allowMultiple = false, required = false) @PathVariable("mongoId") String mongoId,
            @ApiParam(name = "fileId", value = "eg. 58c19f309a5b8d2d139db845", allowMultiple = false, required = false) @PathVariable("fileId") String fileId,
            @RequestParam Map<String, String> requestParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            mongoId = SecurityEscape.cleanString(mongoId);
            fileId = SecurityEscape.cleanString(fileId);
            requestParams = SecurityEscape.cleanMap(requestParams);
        }

        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(SubSystem.FH);
        String html = ((IDocFHApp) iDocSubSystemApp).getFHResourceART(ssoId, portalId, mongoId, fileId, requestParams);

        return ResponseEntity.ok(html);
    }

    /**
     * getFHResourceARTPrint service returns the Pdf Print resource for the overall fileId and Sct
     * fileId selected by the user. This response returns FH Pdf print document.
     *
     * @param mongoId the overall id of the mongo entry
     * @param fileId  the file id
     * @return Response the FHS print resource
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/fhs/{mongoId}/art/{fileId}/pdf")
    @LogExecutionTimeWithArgs
    public ResponseEntity<byte[]> getFHResourceARTPrint(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "mongoId", value = "eg. 58c19f309a5b8d2d139db83d", allowMultiple = false, required = false) @PathVariable("mongoId") String mongoId,
            @ApiParam(name = "fileId", value = "eg. 58c19f309a5b8d2d139db845", allowMultiple = false, required = false) @PathVariable("fileId") String fileId,
            @RequestParam Map<String, String> requestParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            mongoId = SecurityEscape.cleanString(mongoId);
            fileId = SecurityEscape.cleanString(fileId);
            requestParams = SecurityEscape.cleanMap(requestParams);
        }

        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(SubSystem.FH);
        Map<String, Object> artifact = ((IDocFHApp) iDocSubSystemApp)
                .getFHArtifactART(ssoId, portalId, mongoId, fileId);

        String fileName = (String) artifact.get(Constants.FILENAME);
        String contentType = (String) ((Map<String, Object>) (artifact.get(Constants.METADATA))).get(Constants.TYPE);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, Constants.EQ_FILENAME + fileName + "\"");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .contentType(MediaType.valueOf(contentType))
                .body((byte[]) artifact.get(Constants.CONTENT));

    }

    /**
     * getAssociatedDocumentsLL service returns the list of associated LL document for the
     * particular category selected by the user. This response returns the list of associated LL
     * document.
     *
     * @param category the LL category
     * @return Response the LL associated documents
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/ll/sct/{category}/associated", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity<DocumentModel> getAssociatedDocumentsLL(
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
        DocumentModel documentModel = ((IDocLLApp) iDocSubSystemApp).getAssociatedDocumentsLL(ssoId, portalId, category, requestParams);

        return ResponseEntity.ok(documentModel);
    }

    /**
     * getDocumentLLInit service returns the list of associated LL document for the particular
     * category selected by the user. This response returns the LL init document.
     *
     * @param category the LL category
     * @return Response the LL init document
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/ll/sct/{category}/init", produces = MediaType.TEXT_HTML_VALUE)
    @LogExecutionTimeWithArgs
    public ResponseEntity<String> getDocumentLLInit(
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
        String html = ((IDocLLApp) iDocSubSystemApp).getLLResourceInit(ssoId, portalId, category, requestParams);

        return ResponseEntity.ok(html);
    }
}
