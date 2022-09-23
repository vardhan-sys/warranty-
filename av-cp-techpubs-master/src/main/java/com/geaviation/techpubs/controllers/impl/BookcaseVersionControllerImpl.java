package com.geaviation.techpubs.controllers.impl;

import com.geaviation.techpubs.controllers.util.ControllerUtil;
import com.geaviation.techpubs.data.util.SecurityEscape;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.services.api.IManualApp;
import com.geaviation.techpubs.services.impl.TechpubsPartDetailsExcelApp;
import com.geaviation.techpubs.services.impl.admin.AuthServiceImpl;
import com.geaviation.techpubs.services.util.AppConstants;
import com.geaviation.techpubs.services.util.StringUtils;
import com.geaviation.techpubs.services.util.TechpubsAppUtil;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.json.JSONArray;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.geaviation.techpubs.services.util.AppConstants.PORTAL_ID;
import static com.geaviation.techpubs.services.util.AppConstants.SM_SSOID;

@RestController
@RefreshScope
public class BookcaseVersionControllerImpl {

    private static final String DATE_FORMAT = "yyyyMMddhhmmssSSS";

    private static final String ATTACHMENT_FILENAME = "attachment; filename=\"";

    private static final Logger log = LogManager.getLogger(BookcaseVersionControllerImpl.class);

    @Value("${techpubs.services.sqlInjection}")
    private boolean sqlInjection;

    @Value("${PDF.HTMLDIRECTURL}")
    protected String directHtmlURL;

    @Value("${techpubs.services.partDetailsExportEndpoint}")
    private boolean partDetailsExportEndpoint;

    @Value("${techpubs.services.CORTONA_3D_S3}")
    private boolean CORTONA_3D_S3;

    @Autowired
    private TechpubsAppUtil techpubsAppUtil;

    @Autowired
    AuthServiceImpl authServiceImpl;

    @Autowired
    private IManualApp iManualApp;

    @Autowired
    private TechpubsPartDetailsExcelApp techpubsPartDetailsExcelApp;

    /**
     * techpubsResourceByFilename service returns the file for the particular version of a engine
     * program, engine manual, and filename selected by the user. This response returns the HTML
     * document based on filename.
     *
     * @param bookcase the bookcase of the engine program
     * @param version  the version of the bookcase
     * @param manual   the engine manual
     * @param filename the filename
     * @return Response the html file
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/pgms/{bookcase}/versions/{version}/mans/{manual}/file/{filename}", produces = {MediaType.TEXT_HTML_VALUE, "model/cortona3d"})
    public ResponseEntity<byte[]> techpubsResourceByFilename(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "bookcase", value = "eg. gek114118") @PathVariable("bookcase") String bookcase,
            @ApiParam(name = "version", value = "eg. 1.2") @PathVariable("version") String version,
            @ApiParam(name = "manual", value = "eg. sbs") @PathVariable("manual") String manual,
            @ApiParam(name = "filename", value = "eg. genx-2b-sb-72-0249-r00.htm") @PathVariable("filename") String filename,
            @RequestParam Map<String, String> queryParams,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            bookcase = SecurityEscape.cleanString(bookcase);
            manual = SecurityEscape.cleanString(manual);
            filename = SecurityEscape.cleanString(filename);
            queryParams = SecurityEscape.cleanMap(queryParams);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId, "review-overlay", request);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String bandwidth = "high".equalsIgnoreCase(queryParams.get("bw")) ? "high" : "low";
        boolean multiBrowserDocumentRequired = "Y".equalsIgnoreCase(queryParams.get("mbdr"));

        byte[] html = iManualApp.getHTMLResourceTD(ssoId, portalId, bookcase, version, manual, filename,
                bandwidth, multiBrowserDocumentRequired);
        return ResponseEntity
                .ok()
                .cacheControl(ControllerUtil.getSpringCacheControl())
                .body(html);
    }

    /**
     * techpubsPartDetailsByFilename service returns an excel download of materials parts data
     * for the particular version of an engine program, engine manual, and filename selected by the user.
     *
     * @param bookcase the bookcase of the engine program
     * @param version  the version of the bookcase
     * @param manual   the engine manual
     * @param filename the filename
     * @return Excel sheet of parts data for service bulletin, OR nothing if no parts data exists for that SB
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/pgms/{bookcase}/versions/{version}/mans/{manual}/part-details/{filename}",
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity techpubsPartDetailsByFilename(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "bookcase", value = "eg. gek114118") @PathVariable("bookcase") String bookcase,
            @ApiParam(name = "version", value = "eg. 1.2") @PathVariable("version") String version,
            @ApiParam(name = "manual", value = "eg. sbs") @PathVariable("manual") String manual,
            @ApiParam(name = "filename", value = "eg. genx-2b-sb-72-0249-r00.htm") @PathVariable("filename") String filename,
            HttpServletRequest request) throws TechpubsException, IOException {

        if (!partDetailsExportEndpoint) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String fullUrl = request.getRequestURI();
        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            filename = SecurityEscape.cleanString(filename);
            fullUrl = SecurityEscape.cleanString(fullUrl);
        }

        List<Map<String, Object>> results = techpubsPartDetailsExcelApp.getPsvcSearchResultsForFilepathUrl(fullUrl, ssoId, portalId);

        if (results.size() == 0) {
            log.info("No results returned from Elasticsearch for document: " + bookcase + " version " + version + " filename " + filename);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No results found for document");
        }

        // We should have only ONE result for this unique ID
        if (results.size() > 1) {
            log.error("Multiple results returned from Elasticsearch for document: " + bookcase + " version " + version + " filename " + filename);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        JSONArray materialsParts = (JSONArray) results.get(0).get("materialsParts");

        try {
            File excelFile = techpubsPartDetailsExcelApp.createPartDetailsExcelFile(materialsParts);
            if (excelFile == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No parts data to download");
            }
            String downloadFileName = filename.replace(".htm", "") + "_PartDetailsExport.xlsx";

            //Response headers
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + downloadFileName + "\"");

            // build the response
            ResponseEntity response = ResponseEntity.ok()
                    .headers(responseHeaders)
                    .contentType(MediaType.valueOf(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                    .body(FileUtils.readFileToByteArray(excelFile));

            // cleans up our temporary file after response is made
            boolean deletedFile = excelFile.delete();

            if (!deletedFile) {
                log.error("excel file not deleted");
            }

            return response;

        } catch (IOException e) {
            log.error("IOException while Exporting excel document", e);
            throw (e);
        }
    }

    /**
     * getPrintHTMLResource service returns the html print resource file by engine program , engine
     * manual and filename. This response returns the print html resource file.
     *
     * @param bookcase the engine program
     * @param version  the version of the bookcase
     * @param manual   manual the engine manual
     * @param filename the filename
     * @return Response the prints the HTML resource
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/pgms/{bookcase}/versions/{version}/mans/{manual}/file/{filename}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getPrintHTMLResource(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "bookcase", value = "eg. gek108748") @PathVariable("bookcase") String bookcase,
            @ApiParam(name = "version", value = "eg. 1.2") @PathVariable("version") String version,
            @ApiParam(name = "manual", value = "eg. sbs") @PathVariable("manual") String manual,
            @ApiParam(name = "filename", value = "eg. cf6-80e1-sb-80-0012-r02.htm") @PathVariable("filename") String filename,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            bookcase = SecurityEscape.cleanString(bookcase);
            version = SecurityEscape.cleanString(version);
            manual = SecurityEscape.cleanString(manual);
            filename = SecurityEscape.cleanString(filename);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId,"review-overlay", request);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        byte[] pdfByte = iManualApp
                .getPrintHTMLResourceTD(directHtmlURL, ssoId, portalId, bookcase, version, manual, filename);
        String pdfFilename =
                bookcase + "_" + manual + "_" + filename.replaceFirst("\\.[^\\.]*$", "") + "_"
                        + StringUtils.getFormattedTimestamp(DATE_FORMAT) + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + pdfFilename + "\"");

        return ResponseEntity
                .ok()
                .headers(headers)
                .cacheControl(ControllerUtil.getSpringCacheControl())
                .body(pdfByte);
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
     * @param request  the httpservlet request
     * @return Response the resource file
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = { "/techdocs/pgms/{bookcase}/versions/{version}/mans/{manual}/res/**", "/techdocs/pgms/{bookcase}/versions/{version}/mans/{manual}/file/res/**" })
    public ResponseEntity<byte[]> techpubsBinaryResource(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "bookcase", value = "eg. gek108786") @PathVariable("bookcase") String bookcase,
            @ApiParam(name = "version", value = "eg. 2.3") @PathVariable("version") String version,
            @ApiParam(name = "manual", value = "eg. gek109993") @PathVariable("manual") String manual,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            bookcase = SecurityEscape.cleanString(bookcase);
            version = SecurityEscape.cleanString(version);
            manual = SecurityEscape.cleanString(manual);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId, "review-overlay", request);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String res = ControllerUtil.getRemainingPath(request);

        if (CORTONA_3D_S3) {
            // if the request endpoint is /file/res this is coming from the cortona lib
            if (request.getRequestURI().contains("/file/")) {
                res = "res/" + res;
            }
        }

        byte[] binaryResource = iManualApp.getBinaryResourceTD(ssoId, portalId, bookcase, version, manual, res);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, AppConstants.EQ_FILENAME + res + "\"");

        return ResponseEntity
                .ok()
                .headers(headers)
                .cacheControl(ControllerUtil.getSpringCacheControl())
                .contentType(ControllerUtil.getSpringResourceContentType(res))
                .body(binaryResource);
    }

    /**
     * techpubsSbResourceByFilename service returns the file for the particular engine program, engine
     * manual and service bulletin selected by the user. This response returns the HTML document based on
     * filename.
     *
     * @param bookcase the bookcase of the engine program
     * @param version  the version of the bookcase
     * @param manual   the engine manual
     * @param sbnbr    the service bulletin number
     * @return Response the html file
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/pgms/{bookcase}/versions/{version}/mans/{manual}/sb/{sbnbr}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<byte[]> techpubsSbResourceByFilename(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "bookcase", value = "eg. gek114118") @PathVariable("bookcase") String bookcase,
            @ApiParam(name = "version", value = "eg. 1.2") @PathVariable("version") String version,
            @ApiParam(name = "manual", value = "eg. sbs") @PathVariable("manual") String manual,
            @ApiParam(name = "sbnbr", value = "eg. 72-0249") @PathVariable("sbnbr") String sbnbr,
            @RequestParam Map<String, String> queryParams,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            bookcase = SecurityEscape.cleanString(bookcase);
            version = SecurityEscape.cleanString(version);
            manual = SecurityEscape.cleanString(manual);
            sbnbr = SecurityEscape.cleanString(sbnbr);
            queryParams = SecurityEscape.cleanMap(queryParams);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId, "review-overlay", request);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String bandwidth = "high".equalsIgnoreCase(queryParams.get("bw")) ? "high" : "low";
        boolean multiBrowserDocumentRequired = "Y".equalsIgnoreCase(queryParams.get("mbdr"));

        byte[] sbnbrFile = iManualApp.getSbResource(ssoId, portalId, manual, bookcase, version, sbnbr,
                bandwidth, multiBrowserDocumentRequired);

        return ResponseEntity.ok(sbnbrFile);
    }

    /**
     * techpubsResourceByTarget service returns the list of document for the particular engine
     * program, engine manual and target selected by the user. This response returns the document
     * based on target based on engine program and engine manual.
     *
     * @param bookcase the bookcase of the engine program
     * @param version  the version of the bookcase
     * @param manual   the engine manual
     * @param target   the target
     * @return Response the resource by target
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/pgms/{bookcase}/versions/{version}/mans/{manual}/trg/{target}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<byte[]> techpubsResourceByTarget(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "bookcase", value = "eg. gek114118") @PathVariable("bookcase") String bookcase,
            @ApiParam(name = "version", value = "eg. 1.2") @PathVariable("version") String version,
            @ApiParam(name = "manual", value = "eg. sbs") @PathVariable("manual") String manual,
            @ApiParam(name = "target", value = "eg. 72-32-00-01-050") @PathVariable("target") String target,
            @RequestParam Map<String, String> queryParams,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            bookcase = SecurityEscape.cleanString(bookcase);
            version = SecurityEscape.cleanString(version);
            manual = SecurityEscape.cleanString(manual);
            target = SecurityEscape.cleanString(target);
            queryParams = SecurityEscape.cleanMap(queryParams);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId, "review-overlay", request);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String bandwidth = "high".equalsIgnoreCase(queryParams.get("bw")) ? "high" : "low";
        boolean multiBrowserDocumentRequired = "Y".equalsIgnoreCase(queryParams.get("mbdr"));

        byte[] html = iManualApp.getHTMLResourceTDByTargetIndex(ssoId, portalId, bookcase, version, manual, target,
                bandwidth, multiBrowserDocumentRequired);

        return ResponseEntity.ok(html);
    }

}
