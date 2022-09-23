package com.geaviation.techpubs.controllers.impl.admin;

import com.geaviation.techpubs.controllers.requests.SortBy;
import com.geaviation.techpubs.controllers.util.Constants;
import com.geaviation.techpubs.data.util.SecurityEscape;
import com.geaviation.techpubs.data.util.log.LogExecutionTimeWithArgs;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.techlib.dto.EngineDocumentAddReachDTO;
import com.geaviation.techpubs.models.techlib.dto.EngineDocumentByIdReachDTO;
import com.geaviation.techpubs.models.techlib.dto.EngineDocumentDTO;
import com.geaviation.techpubs.models.techlib.dto.EngineDocumentEditReachDTO;
import com.geaviation.techpubs.models.techlib.enginedoc.EngineDocumentEntity;
import com.geaviation.techpubs.services.api.admin.IEngineDocAdminApp;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;
import com.geaviation.techpubs.services.impl.admin.AuthServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.geaviation.techpubs.services.util.AppConstants.PORTAL_ID;
import static com.geaviation.techpubs.services.util.AppConstants.SM_SSOID;

@RestController
@RefreshScope
@RequestMapping("/admin/engine-documents")
public class EngineDocAdminController {

    private static final Logger log = LogManager.getLogger(EngineDocAdminController.class);

    @Autowired
    private IEngineDocAdminApp engineDocAdminApp;

    @Autowired
    private AuthServiceImpl authServiceImpl;

    @Value("${techpubs.services.sqlInjection}")
    private boolean sqlInjection;

    @Value("${techpubs.services.reachEndpoints}")
    private boolean reachEndpointsActive;

    @Value("${techpubs.services.reachMvp2}")
    private boolean reachMvp2Active;

    @GetMapping(value = "/document-types", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getEngineDocumentTypes(@RequestHeader(SM_SSOID) String ssoId, final HttpServletRequest request) throws TechpubsException {

        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }

        if (reachEndpointsActive) {
            try {
                authServiceImpl.checkResourceAccessForProduct(ssoId, "uploader", request, "enginemanuals");
            } catch (TechpubsException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.ok(engineDocAdminApp.getEngineDocumentTypes());
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping(value = "/{documentType}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity addNewDocument(@RequestHeader(SM_SSOID) String ssoId, @RequestHeader(PORTAL_ID) String portalId, @PathVariable String documentType, @ModelAttribute EngineDocumentAddReachDTO engineDocumentReachDTO, final HttpServletRequest request) {

        if (!reachEndpointsActive) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
        }

        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, "uploader", request, "enginemanuals");
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            EngineDocumentEntity engineDocument = engineDocAdminApp.addEngineDocuments(engineDocumentReachDTO, ssoId, documentType);
            return ResponseEntity.status(HttpStatus.CREATED).body(engineDocument);
        } catch (TechpubsException techpubsException) {
            if (techpubsException.getTechpubsAppError().equals(TechpubsException.TechpubsAppError.INVALID_PARAMETER)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(techpubsException.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * @param ssoId    the ssoID of the user
     * @param portalId
     * @param request  request
     * @return The list of Document Types
     * @throws TechpubsException throws an exception for unauthorized request
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<EngineDocumentDTO>> getAllEngineDocuments(@RequestHeader(SM_SSOID) String ssoId, @RequestHeader(PORTAL_ID) String portalId, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size, @RequestParam(value = "sortBy") String sortBy, @RequestParam(value = "type", required = false) String documentType, @RequestParam(value = "model", required = false) List<String> engineModel, @RequestParam(value = "searchTerm", required = false) String searchTerm, final HttpServletRequest request) throws TechpubsException {

        if (!reachEndpointsActive) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            sortBy = SecurityEscape.cleanString(sortBy);
        }
        SortBy sort = new SortBy(sortBy);

        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, "uploader", request, "enginemanuals");
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            return ResponseEntity.ok(engineDocAdminApp.getEngineDocuments(documentType, engineModel, searchTerm, page, size, sort));
        } catch (TechpubsException techpubsException) {
            if (techpubsException.getTechpubsAppError().equals(TechpubsException.TechpubsAppError.INVALID_PARAMETER)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }


    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteEngineDocumentById(@PathVariable String id, @RequestHeader(SM_SSOID) String ssoId, @RequestHeader(PORTAL_ID) String portalId, final HttpServletRequest request) throws TechpubsException {

        if (!reachEndpointsActive) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
        }

        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, "uploader", request, "enginemanuals");
        } catch (TechpubsException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            return ResponseEntity.ok(engineDocAdminApp.deleteEngineDocument(id).toString());
        } catch (TechpubsException techpubsException) {
            if (techpubsException.getTechpubsAppError().equals(TechpubsException.TechpubsAppError.INVALID_PARAMETER)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(techpubsException.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    /**
     * @param ssoId    the ssoID of the user
     * @param portalId
     * @return The pdf requested is downloaded on the browser
     * @throws TechpubsException throws an exception for unauthorized request
     * @pathVariable id - references the filename of the REACH engine manual to be downloaded from S3
     */

    @GetMapping(value = "/download/pdf/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    @LogExecutionTimeWithArgs
    public ResponseEntity getPDFfromS3(@PathVariable String id, @RequestHeader(SM_SSOID) String ssoId, @RequestHeader(PORTAL_ID) String portalId, HttpServletRequest request) {

        if (!reachEndpointsActive) {
            return ResponseEntity.notFound().build();
        }


        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            id = SecurityEscape.cleanString(id);
        }
        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, "uploader", request, "enginemanuals");
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> fileMap = null;
        try {
            fileMap = engineDocAdminApp.getFileInputStreamFromS3(id);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, Constants.ATTACHMENT_FILENAME + fileMap.get("filename") + "\"" + ".pdf");
            InputStreamResource inputStreamResource = new InputStreamResource((InputStream) fileMap.get("stream"));
            return new ResponseEntity(inputStreamResource, responseHeaders, HttpStatus.OK);

        } catch (TechpubsException techpubsException) {
            if (techpubsException.getTechpubsAppError().equals(TechpubsException.TechpubsAppError.INVALID_PARAMETER)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(techpubsException.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    /**
     * Downloads a lists of engine documents
     *
     * @return Excel file of all engine documents
     */
    @GetMapping(value = "/excel-export-documents", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadUsers(@RequestHeader(SM_SSOID) String ssoId, @RequestHeader(PORTAL_ID) String portalId, HttpServletRequest request) throws ExcelException, TechpubsException {

        if (!reachMvp2Active) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
        }

        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, "uploader", request, "enginemanuals");
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ResponseEntity<byte[]> response;

        try {
            FileWithBytes fileWithBytes = engineDocAdminApp.downloadEngineDocuments();
            if (fileWithBytes == null) {
                return ResponseEntity.ok(Constants.NO_DATA_TO_DOWNLOAD.getBytes());
            }
            byte[] content = fileWithBytes.getContents();
            String fileName = fileWithBytes.getFileName().split("\\.", 2)[0];
            String fileType = fileWithBytes.getFileName().split("\\.", 2)[1];
            String file = fileName + "_" + new SimpleDateFormat("yyyy_MM_dd_HH.mm.ss").format(new Date()) + "." + fileType;

            response = ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, Constants.ATTACHMENT_FILENAME + file + "\"").contentType(MediaType.APPLICATION_OCTET_STREAM).body(content);
        } catch (ExcelException | TechpubsException e) {
            log.error("Error downloading engine documents data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }

    /**
     * This endpoint is for postman testing only - the UI should use the soft delete.
     * This is so we can ensure that our postman tests don't fill the database & s3 bucket
     * with unnecessary fake documents
     *
     * @param ssoId    the ssoID of the user
     * @param portalId
     * @param request  request
     * @return The deleted document
     * @throws TechpubsException throws an exception for unauthorized request
     */
    @DeleteMapping(value = "/hard-delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity hardDeleteEngineDocumentById(@PathVariable String id, @RequestHeader(SM_SSOID) String ssoId, @RequestHeader(PORTAL_ID) String portalId, final HttpServletRequest request) throws TechpubsException {

        if (!reachMvp2Active) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
        }

        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, "uploader", request, "enginemanuals");
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            return ResponseEntity.ok(engineDocAdminApp.hardDeleteEngineDocument(id));
        } catch (TechpubsException techpubsException) {
            if (techpubsException.getTechpubsAppError().equals(TechpubsException.TechpubsAppError.INVALID_PARAMETER)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(techpubsException.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }


    /**
     * @param ssoId    the ssoID of the user
     * @param portalId
     * @return The EngineDocumentEditReachDto - MetaData
     * @throws TechpubsException throws an exception for unauthorized request
     * @pathVariable id - references the filename of the REACH engine manual MetaData
     */

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getEngineDocumentEditReach(@PathVariable String id, @RequestHeader(SM_SSOID) String ssoId, @RequestHeader(PORTAL_ID) String portalId, final HttpServletRequest request) throws TechpubsException {

        if (!reachMvp2Active) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            id = SecurityEscape.cleanString(id);
        }

        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, "uploader", request, "enginemanuals");
        } catch (TechpubsException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            EngineDocumentByIdReachDTO engineDocumentByIdReachDTO = engineDocAdminApp.getEngineDocumentById(id);
            return ResponseEntity.ok(engineDocumentByIdReachDTO);

        } catch (TechpubsException techpubsException) {
            if (techpubsException.getTechpubsAppError().equals(TechpubsException.TechpubsAppError.INVALID_PARAMETER)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(techpubsException.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
    
    /**
     * Post all the engine document data for Add New Engine Document
     *
     * @param ssoId                 the ssoID of the user
     * @param request               request
     * @param engineDocumentReachDTO the body of the request
     * @return A 200 response if successful.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)	
    public ResponseEntity updateEngineDocument(
	        @RequestHeader(SM_SSOID) String ssoId,
	        @RequestHeader(PORTAL_ID) String portalId,
	        @ModelAttribute EngineDocumentEditReachDTO engineDocumentEditReachDTO,
	        @PathVariable String id,
	        final HttpServletRequest request
    ) {
	    	
	    	if (!reachMvp2Active) {
		    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}

	        if (sqlInjection) {
	            ssoId = SecurityEscape.cleanString(ssoId);
	            portalId = SecurityEscape.cleanString(portalId);
	            id = SecurityEscape.cleanString(id);
	        }
	        
	            try {
	                authServiceImpl.checkResourceAccessForProduct(ssoId, "uploader", request, "enginemanuals");
	            } catch (TechpubsException e) {
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	            }

	            try {
	            	return ResponseEntity.ok(engineDocAdminApp.updateEngineDocument(engineDocumentEditReachDTO, id, ssoId));
	            } catch (TechpubsException techpubsException) {
	                if (techpubsException.getTechpubsAppError().equals(TechpubsException.TechpubsAppError.INVALID_DOCUMENT_IDENTIFIERS)){
	                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(techpubsException.getMessage());
	                } else if (techpubsException.getTechpubsAppError().equals(TechpubsException.TechpubsAppError.DATA_NOT_FOUND)) {
	                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	                } else if (techpubsException.getTechpubsAppError()
	                        .equals(TechpubsException.TechpubsAppError.INVALID_PARAMETER)) {
	                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(techpubsException.getMessage());
	                }
	                else {
	                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	                }
	            } catch (Exception e) {
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	            }
	            
	}


}