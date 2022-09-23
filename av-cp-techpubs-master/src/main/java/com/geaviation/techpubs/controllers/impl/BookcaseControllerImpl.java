package com.geaviation.techpubs.controllers.impl;

import com.geaviation.techpubs.controllers.util.ControllerUtil;
import com.geaviation.techpubs.data.impl.BookcaseDataImpl;
import com.geaviation.techpubs.data.util.SecurityEscape;
import com.geaviation.techpubs.data.util.log.LogExecutionTimeWithArgs;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.DocumentModel;
import com.geaviation.techpubs.models.ManualModel;
import com.geaviation.techpubs.models.ProgramModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.models.techlib.dto.BookcaseBookcaseVersionDto;
import com.geaviation.techpubs.services.api.IDocSubSystemApp;
import com.geaviation.techpubs.services.api.IDocTPApp;
import com.geaviation.techpubs.services.api.IManualApp;
import com.geaviation.techpubs.services.api.IProgramApp;
import com.geaviation.techpubs.services.impl.DocAppRegServices;
import com.geaviation.techpubs.services.util.AppConstants;
import com.geaviation.techpubs.services.util.StringUtils;
import com.geaviation.techpubs.services.util.TechpubsAppUtil;
import io.swagger.annotations.ApiParam;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.geaviation.techpubs.services.util.AppConstants.PORTAL_ID;
import static com.geaviation.techpubs.services.util.AppConstants.SM_SSOID;

@RestController
@RefreshScope
public class BookcaseControllerImpl {

    @Value("${techpubs.services.sqlInjection}")
    private boolean sqlInjection;

    @Value("${PDF.HTMLDIRECTURL}")
    protected String directHtmlURL;

    private static final String DATE_FORMAT = "yyyyMMddhhmmssSSS";

    @Autowired
    private TechpubsAppUtil techpubsAppUtil;

    @Autowired
    private IManualApp iManualApp;

    @Autowired
    private IProgramApp iProgramApp;

    @Autowired
    private DocAppRegServices docAppRegServices;

    @Autowired
    private BookcaseDataImpl bookcaseDataImpl;

    /**
     * techpubsResourceByFilename service returns the file for the particular engine program, engine
     * manual and filename selected by the user. This response returns the HTML document based on
     * filename.
     *
     * @param program  the engine program
     * @param manual   the engine manual
     * @param filename the filename
     * @return ResponseEntity the html file
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/docs/pgms/{program}/mans/{manual}/file/{filename}", produces = MediaType.TEXT_PLAIN_VALUE)
    @LogExecutionTimeWithArgs
    public ResponseEntity<byte[]> techpubsResourceByFilenameLinker(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "program", value = "eg. gek114118", allowMultiple = false, required = false) @PathVariable("program") String program,
            @ApiParam(name = "manual", value = "eg. sbs", allowMultiple = false, required = false) @PathVariable("manual") String manual,
            @ApiParam(name = "filename", value = "eg. genx-2b-sb-72-0249-r00.htm", allowMultiple = false, required = false) @PathVariable("filename") String filename,
            @RequestParam Map<String, String> queryParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            program = SecurityEscape.cleanString(program);
            manual = SecurityEscape.cleanString(manual);
            filename = SecurityEscape.cleanString(filename);
            queryParams = SecurityEscape.cleanMap(queryParams);
        }

        String bandwidth = "high".equalsIgnoreCase(queryParams.get("bw")) ? "high" : "low";
        boolean multiBrowserDocumentRequired = "Y".equalsIgnoreCase(queryParams.get("mbdr")) ? true : false;

        byte[] html = iManualApp.getHTMLResourceTD(ssoId, portalId, program, manual, filename, bandwidth, multiBrowserDocumentRequired);

        return ResponseEntity.ok(html);
    }

    /**
     * techpubsBinaryResource service returns the resource file for the particular engine program
     * and engine manual selected by the user. This response returns the resource file.
     * <p>
     * Produces({"application/pdf","image/png","image/gif","image/jpeg","image/tiff","model/vrml",
     * "image/svg+xml","image/cgm","video/mp4","video/avi"}) Note: The 'res' variable is separated
     * by ':' from a regular expression matching 1 to many characters This allows the '/' (slash)
     * character to be passed to the 'res' path param
     *
     * @param program the engine program
     * @param manual  the engine manual
     * @param request the httpservlet request
     * @return ResponseEntity the resource file
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/docs/pgms/{program}/mans/{manual}/res/**")
    @LogExecutionTimeWithArgs
    public ResponseEntity<byte[]> techpubsBinaryResourceDocs(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "program", value = "eg. gek108786", allowMultiple = false, required = false) @PathVariable("program") String program,
            @ApiParam(name = "manual", value = "eg. gek109993", allowMultiple = false, required = false) @PathVariable("manual") String manual,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            program = SecurityEscape.cleanString(program);
            manual = SecurityEscape.cleanString(manual);
        }

        String res = ControllerUtil.getRemainingPath(request);
        byte[] binaryResource = iManualApp.getBinaryResourceTD(ssoId, portalId, program, null, manual, res);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, AppConstants.EQ_FILENAME + res + "\"");
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(ControllerUtil.getSpringResourceContentType(res))
                .body(binaryResource);
    }

    /**
     * getManuals service returns the list of engine manuals as per the criteria. This response
     * returns the object with list of manuals.
     *
     * @param queryParams the form parameters
     * @return ResponseEntity the list of manuals
     * @throws TechpubsException the techpubs exceptions
     * @deprecated
     */
    @PostMapping(value = "/manual", produces = MediaType.APPLICATION_JSON_VALUE)
    @LogExecutionTimeWithArgs
    @Deprecated
    public ResponseEntity<ManualModel> getManuals(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @RequestParam Map<String, String> queryParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            queryParams = SecurityEscape.cleanMap(queryParams);
        }

        ManualModel manualModel = iManualApp.getManuals(ssoId, portalId, queryParams);

        return ResponseEntity.ok(manualModel);
    }

    /**
     * getPrograms service returns the list of programs for the subsystem selected by the user. This
     * response returns the list of program with associated data.
     *
     * @return ResponseEntity the list of programs
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/pgms", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity<ProgramModel> getPrograms(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @RequestParam Map<String, String> queryParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            queryParams = SecurityEscape.cleanMap(queryParams);
        }

        ProgramModel programModel = iProgramApp.getPrograms(ssoId, portalId, queryParams);

        return ResponseEntity.ok(programModel);
    }

    /**
     * getProgramsByAircraft service returns the list of programs for the engine aircraft selected
     * by the user. This response returns the list of program with associated data based on engine
     * aircraft.
     *
     * @param aircraft the engine aircraft
     * @return ResponseEntity the programs by aircraft
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/pgms/aircrafts/{aircraft}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity<ProgramModel> getProgramsByAircraft(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "aircraft", value = "eg. 767-300ER", allowMultiple = false, required = false) @PathVariable("aircraft") String aircraft,
            @RequestParam Map<String, String> queryParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            aircraft = SecurityEscape.cleanString(aircraft);
            queryParams = SecurityEscape.cleanMap(queryParams);
        }

        ProgramModel programModel = iProgramApp.getPrograms(ssoId, portalId, null, null, aircraft, queryParams);

        return ResponseEntity.ok(programModel);
    }

    /**
     * getProgramsByAircraft service returns the list of programs for the engine aircraft tail
     * selected by the user. This response returns the list of program with associated data based on
     * engine aircraft and engine tail.
     *
     * @param aircraft the engine aircraft
     * @param tail     the engine tail
     * @return ResponseEntity the programs by aircraft tail
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/pgms/aircrafts/{aircraft}/tails/{tail}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity<ProgramModel> getProgramsByAircraftTail(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "aircraft", value = "eg. 767-300ER", allowMultiple = false, required = false) @PathVariable("aircraft") String aircraft,
            @ApiParam(name = "tail", value = "eg. OO-JNL", allowMultiple = false, required = false) @PathVariable("tail") String tail,
            @RequestParam Map<String, String> queryParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            aircraft = SecurityEscape.cleanString(aircraft);
            tail = SecurityEscape.cleanString(tail);
            queryParams = SecurityEscape.cleanMap(queryParams);
        }

        ProgramModel programModel = iProgramApp.getPrograms(ssoId, portalId, null, null, aircraft, tail, queryParams);

        return ResponseEntity.ok(programModel);
    }

    /**
     * getProgramsByEsnlist service returns the list of programs for the engine esnlist selected by
     * the user. This response returns the list of program with associated data based on esn list.
     *
     * @param esns the esns
     * @return ResponseEntity the programs by esnlist
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/pgms/esns/{esnlist}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity<ProgramModel> getProgramsByEsnlist(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "esnlist", value = "eg. 872995|950996|994996", allowMultiple = false, required = false) @PathVariable("esnlist") String esns,
            @RequestParam Map<String, String> queryParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            esns = SecurityEscape.cleanString(esns);
            queryParams = SecurityEscape.cleanMap(queryParams);
        }

        List<String> esnList = new ArrayList<>(new HashSet<>(Arrays.asList(esns.split("\\|"))));

        ProgramModel programModel = iProgramApp.getPrograms(ssoId, portalId, null, null, null, null, esnList, queryParams);

        return ResponseEntity.ok(programModel);
    }

    /**
     * getProgramsByFamily service returns the list of programs for the engine family selected by
     * the user. This response returns the list of program with associated data based on engine
     * family.
     *
     * @param family the engine family
     * @return ResponseEntity the list of programs by family
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/pgms/fams/{family}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity<ProgramModel> getProgramsByFamily(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "family", value = "eg. GE90", allowMultiple = false, required = false) @PathVariable("family") String family,
            @RequestParam Map<String, String> queryParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            family = SecurityEscape.cleanString(family);
            queryParams = SecurityEscape.cleanMap(queryParams);
        }

        ProgramModel programModel = iProgramApp.getPrograms(ssoId, portalId, family, queryParams);

        return ResponseEntity.ok(programModel);
    }

    /**
     * getProgramsByModel service returns the list of programs for the engine family and engine
     * model selected by the user. This response returns the list of program with associated data
     * based on engine family and engine model.
     *
     * @param family the engine family
     * @param model  the engine model
     * @return ResponseEntity the programs by family and model
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/pgms/fams/{family}/mods/{model}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity<ProgramModel> getProgramsByModel(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "family", value = "eg. GE90", allowMultiple = false, required = false) @PathVariable("family") String family,
            @ApiParam(name = "model", value = "eg. GE90-100", allowMultiple = false, required = false) @PathVariable("model") String model,
            @RequestParam Map<String, String> queryParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            family = SecurityEscape.cleanString(family);
            model = SecurityEscape.cleanString(model);
            queryParams = SecurityEscape.cleanMap(queryParams);
        }

        ProgramModel programModel = iProgramApp.getPrograms(ssoId, portalId, family, model, queryParams);

        return ResponseEntity.ok(programModel);
    }

    /**
     * getProgramsByModel service returns the list of programs for the engine model selected by the
     * user. This response returns the list of program with associated data based on engine model.
     *
     * @param model the engine model
     * @return ResponseEntity the programs by model
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/pgms/mods/{model}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity<ProgramModel> getProgramsByModel(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "model", value = "eg. GE90-100", allowMultiple = false, required = false) @PathVariable("model") String model,
            @RequestParam Map<String, String> queryParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            model = SecurityEscape.cleanString(model);
            queryParams = SecurityEscape.cleanMap(queryParams);
        }

        ProgramModel programModel = iProgramApp.getPrograms(ssoId, portalId, null, model, queryParams);

        return ResponseEntity.ok(programModel);
    }

    /**
     * getProgramsByTail service returns the list of programs for the engine aircraft selected by
     * the user. This response returns the list of program with associated data based on engine
     * tail.
     *
     * @param tail the engine tail
     * @return ResponseEntity the programs by tail
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/pgms/tails/{tail}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity<ProgramModel> getProgramsByTail(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "tail", value = "eg. OO-JNL", allowMultiple = false, required = false) @PathVariable("tail") String tail,
            @RequestParam Map<String, String> queryParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            tail = SecurityEscape.cleanString(tail);
            queryParams = SecurityEscape.cleanMap(queryParams);
        }

        ProgramModel programModel = iProgramApp.getPrograms(ssoId, portalId, null, null, null, tail, queryParams);

        return ResponseEntity.ok(programModel);
    }

    /**
     * techpubsResourceByFilename service returns the file for the particular engine program, engine
     * manual and filename selected by the user. This response returns the HTML document based on
     * filename.
     *
     * @param program  the engine program
     * @param manual   the engine manual
     * @param filename the filename
     * @return ResponseEntity the html file
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/pgms/{program}/mans/{manual}/file/{filename}", produces = {MediaType.TEXT_PLAIN_VALUE, "model/cortona3d"})
    @LogExecutionTimeWithArgs
    public ResponseEntity<byte[]> techpubsResourceByFilename(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "program", value = "eg. gek114118", allowMultiple = false, required = false) @PathVariable("program") String program,
            @ApiParam(name = "manual", value = "eg. sbs", allowMultiple = false, required = false) @PathVariable("manual") String manual,
            @ApiParam(name = "filename", value = "eg. genx-2b-sb-72-0249-r00.htm", allowMultiple = false, required = false) @PathVariable("filename") String filename,
            @RequestParam Map<String, String> queryParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            program = SecurityEscape.cleanString(program);
            manual = SecurityEscape.cleanString(manual);
            filename = SecurityEscape.cleanString(filename);
            queryParams = SecurityEscape.cleanMap(queryParams);
        }

        String bandwidth = "high".equalsIgnoreCase(queryParams.get("bw")) ? "high" : "low";
        boolean multiBrowserDocumentRequired = "Y".equalsIgnoreCase(queryParams.get("mbdr")) ? true : false;

        byte[] html = iManualApp.getHTMLResourceTD(ssoId, portalId, program, manual, filename, bandwidth, multiBrowserDocumentRequired);

        return ResponseEntity.ok(html);
    }

    /**
     * getPrintHTMLResource service returns the html print resource file by engine program , engine
     * manual and filename. This response returns the print html resource file.
     *
     * @param program  program the engine program
     * @param manual   manual the engine manual
     * @param filename the filename
     * @return ResponseEntity the prints the HTML resource
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/pgms/{program}/mans/{manual}/file/{filename}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @LogExecutionTimeWithArgs
    public ResponseEntity<byte[]> getPrintHTMLResource(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "program", value = "eg. gek108748", allowMultiple = false, required = false) @PathVariable("program") String program,
            @ApiParam(name = "manual", value = "eg. sbs", allowMultiple = false, required = false) @PathVariable("manual") String manual,
            @ApiParam(name = "filename", value = "eg. cf6-80e1-sb-80-0012-r02.htm", allowMultiple = false, required = false) @PathVariable("filename") String filename)
            throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            program = SecurityEscape.cleanString(program);
            manual = SecurityEscape.cleanString(manual);
            filename = SecurityEscape.cleanString(filename);
        }

        byte[] pdfByte = iManualApp.getPrintHTMLResourceTD(directHtmlURL, ssoId, portalId, program, null, manual, filename);
        String pdfFilename =
                program + "_" + manual + "_" + filename.replaceFirst("\\.[^\\.]*$", "") + "_"
                        + StringUtils.getFormattedTimestamp(DATE_FORMAT) + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, AppConstants.EQ_FILENAME + pdfFilename + "\"");
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfByte);
    }

    /**
     * techpubsResourceSummaryByFilename service returns the summary file for the particular engine
     * program, engine manual and filename selected by the user. This response returns the summary
     * HTML document based on filename.
     *
     * @param program  the engine program
     * @param manual   the engine manual
     * @param filename the filename
     * @return ResponseEntity the summary file
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/pgms/{program}/mans/{manual}/summary/{filename}", produces = MediaType.TEXT_HTML_VALUE)
    @LogExecutionTimeWithArgs
    public ResponseEntity<String> techpubsResourceSummaryByFilename(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "program", value = "eg. gek114118", allowMultiple = false, required = false) @PathVariable("program") String program,
            @ApiParam(name = "manual", value = "eg. sbs", allowMultiple = false, required = false) @PathVariable("manual") String manual,
            @ApiParam(name = "filename", value = "eg. genx-2b-sb-72-0249-r00.htm", allowMultiple = false, required = false) @PathVariable("filename") String filename) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            program = SecurityEscape.cleanString(program);
            manual = SecurityEscape.cleanString(manual);
            filename = SecurityEscape.cleanString(filename);
        }

        String html = iManualApp.getHTMLResourceSummaryTD(ssoId, portalId, program, manual, filename);

        return ResponseEntity.ok(html);
    }

    /**
     * getPrintHTMLResourceSummary service returns the html resource summary file by engine program
     * , engine manual and filename. This response returns the resource summary file.
     *
     * @param program  the engine program
     * @param manual   the engine manual
     * @param filename the filename
     * @return ResponseEntity the prints the HTML resource summary
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/pgms/{program}/mans/{manual}/summary/{filename}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @LogExecutionTimeWithArgs
    public ResponseEntity<byte[]> getPrintHTMLResourceSummary(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "program", value = "eg. gek108748", allowMultiple = false, required = false) @PathVariable("program") String program,
            @ApiParam(name = "manual", value = "eg. sbs", allowMultiple = false, required = false) @PathVariable("manual") String manual,
            @ApiParam(name = "filename", value = "eg. cf6-80e1-sb-80-0012-r02.htm", allowMultiple = false, required = false) @PathVariable("filename") String filename)
            throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            program = SecurityEscape.cleanString(program);
            manual = SecurityEscape.cleanString(manual);
            filename = SecurityEscape.cleanString(filename);
        }

        byte[] pdfByte = iManualApp.getPrintHTMLResourceSummaryTD(directHtmlURL, ssoId, portalId, program, manual, filename);

        String pdfFilename =
                program + "_" + manual + "_" + filename.replaceFirst("\\.[^\\.]*$", "") + "_"
                        + StringUtils.getFormattedTimestamp(DATE_FORMAT) + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, AppConstants.EQ_FILENAME + pdfFilename + "\"");
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfByte);
    }

    /**
     * techpubsResourceByTarget service returns the list of document for the particular engine
     * program, engine manual and target selected by the user. This response returns the document
     * based on target based on engine program and engine manual.
     *
     * @param program the engine program
     * @param manual  the engine manual
     * @param target  the target
     * @return ResponseEntity the resource by target
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/pgms/{program}/mans/{manual}/trg/{target}", produces = MediaType.TEXT_HTML_VALUE)
    @LogExecutionTimeWithArgs
    public ResponseEntity<byte[]> techpubsResourceByTarget(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "program", value = "eg. gek114118", allowMultiple = false, required = false) @PathVariable("program") String program,
            @ApiParam(name = "manual", value = "eg. sbs", allowMultiple = false, required = false) @PathVariable("manual") String manual,
            @ApiParam(name = "target", value = "eg. 72-32-00-01-050", allowMultiple = false, required = false) @PathVariable("target") String target,
            @RequestParam Map<String, String> queryParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            program = SecurityEscape.cleanString(program);
            manual = SecurityEscape.cleanString(manual);
            target = SecurityEscape.cleanString(target);
            queryParams = SecurityEscape.cleanMap(queryParams);
        }

        String bandwidth = "high".equalsIgnoreCase(queryParams.get("bw")) ? "high" : "low";
        boolean multiBrowserDocumentRequired = "Y".equalsIgnoreCase(queryParams.get("mbdr")) ? true : false;

        byte[] html = iManualApp
                .getHTMLResourceTDByTargetIndex(ssoId, portalId, program, null, manual, target, bandwidth, multiBrowserDocumentRequired);

        return ResponseEntity.ok(html);
    }

    /**
     * getAssociatedDocumentsTP service returns the list of associated TP document for the
     * particular category selected by the user. This response returns the list of associated TP
     * document.
     *
     * @param program  the engine program
     * @param category the TP category
     * @return ResponseEntity the associated documents TP
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/tps/pgm/{program}/sct/{category}/associated", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity<DocumentModel> getAssociatedDocumentsTP(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "program", value = "eg. CF34-10", allowMultiple = false, required = false) @PathVariable("program") String program,
            @ApiParam(name = "category", value = "eg. 2", allowMultiple = false, required = false) @PathVariable("category") String category,
            @RequestParam Map<String, String> queryParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            program = SecurityEscape.cleanString(program);
            category = SecurityEscape.cleanString(category);
            queryParams = SecurityEscape.cleanMap(queryParams);
        }

        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(SubSystem.TP);
        DocumentModel documentModel = ((IDocTPApp) iDocSubSystemApp).getAssociatedDocumentsTP(ssoId, portalId, program, category, queryParams);

        return ResponseEntity.ok(documentModel);
    }

    /**
     * getDocumentTPInit service returns the list of associated TP document for the particular
     * category selected by the user. This response returns the TP init document.
     *
     * @param program  the engine program
     * @param category the TP category
     * @return ResponseEntity the document TP init
     * @throws TechpubsException the techpubs exceptions
     */
    @GetMapping(value = "/techdocs/tps/pgm/{program}/sct/{category}/init", produces = MediaType.TEXT_HTML_VALUE)
    @LogExecutionTimeWithArgs
    public ResponseEntity<String> getDocumentTPInit(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "program", value = "eg. CF34-10", allowMultiple = false, required = false) @PathVariable("program") String program,
            @ApiParam(name = "category", value = "eg. 2", allowMultiple = false, required = false) @PathVariable("category") String category,
            @RequestParam Map<String, String> queryParams) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            program = SecurityEscape.cleanString(program);
            category = SecurityEscape.cleanString(category);
            queryParams = SecurityEscape.cleanMap(queryParams);
        }

        IDocSubSystemApp iDocSubSystemApp = docAppRegServices.getSubSystemService(SubSystem.TP);
        String html = ((IDocTPApp) iDocSubSystemApp).getTPResourceInit(ssoId, portalId, program, category, queryParams);

        return ResponseEntity.ok(html);
    }

    /**
     * Get the online bookcase versions. Used in pa-searchldr.
     */
    // TODO: Dates are different, "2020-02-14T23:59:47.000+00:00" vs 1581706787000
    @GetMapping(value = "/bookcases/online", produces = MediaType.APPLICATION_JSON_VALUE)
    @LogExecutionTimeWithArgs
    public ResponseEntity<List<BookcaseBookcaseVersionDto>> getOnlineBookcases() {
        return ResponseEntity.ok(bookcaseDataImpl.getBookcasesOnlineVersions());
    }

}
