package com.geaviation.techpubs.controllers.impl;

import com.geaviation.techpubs.controllers.util.Constants;
import com.geaviation.techpubs.data.util.SecurityEscape;
import com.geaviation.techpubs.data.util.log.LogExecutionTimeWithArgs;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.techlib.dto.AirframeDto;
import com.geaviation.techpubs.services.api.IAvSystemDocumentApp;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;
import com.geaviation.techpubs.services.impl.SalesforceSvcImpl;
import com.geaviation.techpubs.services.impl.UserService;
import com.geaviation.techpubs.services.impl.admin.AuthServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.geaviation.techpubs.services.util.AppConstants.PORTAL_ID;
import static com.geaviation.techpubs.services.util.AppConstants.SM_SSOID;

@RestController
@RefreshScope
@RequestMapping("/avsystem")
public class AvSystemDocumentControllerImpl {

    private static final Logger log = LogManager.getLogger(AvSystemDocumentControllerImpl.class);

    @Autowired
    private IAvSystemDocumentApp avSystemDocumentApp;

    @Autowired
    private AuthServiceImpl authServiceImpl;

    @Autowired
    private UserService userService;

    @Value("${techpubs.services.sqlInjection}")
    private boolean sqlInjection;

    @Value("${techpubs.services.avsystemDocumentEndpoints}")
    private boolean avsystemDocumentEndpointsActive;

    @Value("${techpubs.services.searchEndpoint}")
    private boolean searchEndpoint;

    @Value("${techpubs.services.salesforceEndpoints}")
    private boolean salesforceEndpointsActive;

    @Autowired
    private SalesforceSvcImpl salesforceSvc;

    @LogExecutionTimeWithArgs
    @GetMapping(value = "/{docType}/{docSite}/{docNumber}/{fileName}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity getPdfFileForDocument(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @PathVariable String docType, @PathVariable String docSite,
            @PathVariable String docNumber, @PathVariable String fileName,
            HttpServletRequest request) throws TechpubsException {

        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            docType = SecurityEscape.cleanString(docType);
            docSite = SecurityEscape.cleanString(docSite);
            docNumber = SecurityEscape.cleanString(docNumber);
            fileName = SecurityEscape.cleanString(fileName);
        }

        if (!avsystemDocumentEndpointsActive) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            InputStreamResource inputStreamResource = avSystemDocumentApp.getDocumentFromS3(ssoId, portalId, docType, docSite, docNumber, fileName);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, Constants.EQ_FILENAME + fileName + "\"");
            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .contentType(MediaType.valueOf("application/pdf"))
                    .body(inputStreamResource);
        } catch (TechpubsException e) {
            if (e.getTechpubsAppError().equals(TechpubsException.TechpubsAppError.INVALID_PARAMETER)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Requested file does not exist");
            } else if (e.getTechpubsAppError().equals(TechpubsException.TechpubsAppError.DATA_NOT_FOUND)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else if (e.getTechpubsAppError().equals(TechpubsException.TechpubsAppError.NOT_AUTHORIZED)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @LogExecutionTimeWithArgs
    @PostMapping(value = "/excel-export", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> search(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @RequestBody String payload) throws TechpubsException {

        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
        }

        if (!searchEndpoint) {
            return ResponseEntity.notFound().build();
        }

        ResponseEntity<byte[]> response;

        try {
            FileWithBytes fileWithBytes = avSystemDocumentApp.getAvSystemExcelDocument(ssoId, portalId, payload);
            if (fileWithBytes == null) {
                return ResponseEntity.ok(Constants.NO_DATA_TO_DOWNLOAD.getBytes());
            }
            byte[] content = fileWithBytes.getContents();
            String fileName = fileWithBytes.getFileName().split("\\.", 2)[0];
            String fileType = fileWithBytes.getFileName().split("\\.", 2)[1];
            String file = fileName + "_" +
                    new SimpleDateFormat("yyyy_MM_dd_HH.mm.ss").format(new Date())
                    + "." + fileType;

            response = ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, Constants.ATTACHMENT_FILENAME + file + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(content);
        } catch (ExcelException | TechpubsException e) {
            log.error("Error downloading aviation system documents data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return response;

    }

    /**
     * Get a list of Entitled Airframes for a given SSO
     *
     * @param ssoId    the ssoID of the user
     * @param portalId the portalId
     */
    @LogExecutionTimeWithArgs
    @GetMapping(value = "/entitled-airframes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AirframeDto>> getEntitledAirframes(@RequestHeader(SM_SSOID) String ssoId, @RequestHeader(PORTAL_ID) String portalId, final HttpServletRequest request) throws TechpubsException {

        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
        }
        String icaoCode = userService.getIcaoCode(ssoId);
        if (salesforceEndpointsActive) {
            return ResponseEntity.ok(salesforceSvc.getEntitledAirframes(icaoCode));
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    /**
     * BE_Enforce_authorization_on_get_PDF_endpoint for a given SSO
     *
     * @param docId    the docId requested by the user
     * @param ssoId    the ssoID of the user
     * @param portalId the portalId
     */
    @LogExecutionTimeWithArgs
    @GetMapping(value = "/{docId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity getPdfFileForDocumentFromDocId(@RequestHeader(SM_SSOID) String ssoId, @RequestHeader(PORTAL_ID) String portalId, @PathVariable String docId, HttpServletRequest request) throws TechpubsException {

        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            docId = SecurityEscape.cleanString(docId);
        }

        if (!avsystemDocumentEndpointsActive) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {

            Map<String, Object> map=avSystemDocumentApp.getDocumentFromS3ById(ssoId, portalId, docId);
            InputStreamResource inputStreamResource = (InputStreamResource) map.get("inputstream");
            String fname=(String)map.get("filename");
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, Constants.EQ_FILENAME + fname);

            return ResponseEntity.ok().headers(responseHeaders).contentType(MediaType.valueOf("application/pdf")).body(inputStreamResource);

        } catch (TechpubsException e) {
            if (e.getTechpubsAppError().equals(TechpubsException.TechpubsAppError.INVALID_PARAMETER)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Requested file does not exist");
            } else if (e.getTechpubsAppError().equals(TechpubsException.TechpubsAppError.DATA_NOT_FOUND)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else if (e.getTechpubsAppError().equals(TechpubsException.TechpubsAppError.NOT_AUTHORIZED)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

    }
}
