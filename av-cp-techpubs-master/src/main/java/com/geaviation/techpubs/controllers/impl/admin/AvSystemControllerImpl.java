package com.geaviation.techpubs.controllers.impl.admin;

import com.geaviation.techpubs.controllers.requests.EnableStatusBody;
import com.geaviation.techpubs.controllers.requests.SortBy;
import com.geaviation.techpubs.controllers.util.Constants;
import com.geaviation.techpubs.data.api.techlib.ISalesforceCompanyData;
import com.geaviation.techpubs.data.util.SearchLoaderUtil;
import com.geaviation.techpubs.data.util.SecurityEscape;
import com.geaviation.techpubs.data.util.log.LogExecutionTimeWithArgs;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.techlib.SystemDocumentEntity;
import com.geaviation.techpubs.models.techlib.SystemDocumentSiteLookupEntity;
import com.geaviation.techpubs.models.techlib.SystemDocumentTypeLookupEntity;
import com.geaviation.techpubs.models.techlib.dto.*;
import com.geaviation.techpubs.services.api.admin.IAvSystemUploaderApp;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;
import com.geaviation.techpubs.services.impl.SalesforceSvcImpl;
import com.geaviation.techpubs.services.impl.UserService;
import com.geaviation.techpubs.services.impl.admin.AuthServiceImpl;
import com.geaviation.techpubs.services.util.admin.AvSystemAuditLogService;
import io.swagger.annotations.ApiParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.geaviation.techpubs.services.util.AppConstants.PORTAL_ID;
import static com.geaviation.techpubs.services.util.AppConstants.SM_SSOID;

@RestController
@RefreshScope
@RequestMapping("/admin/avsystem")
public class AvSystemControllerImpl {

    private static final Logger log = LogManager.getLogger(AvSystemControllerImpl.class);

    @Autowired
    private IAvSystemUploaderApp iAvsystemUploader;

    @Autowired
    private AuthServiceImpl authServiceImpl;

    @Autowired
    private UserService userService;

    @Autowired
    private AvSystemAuditLogService avSystemAuditLogService;

    @Autowired
    private SearchLoaderUtil searchLoaderUtil;

    @Value("${techpubs.services.avsystemAuditTrailActive}")
    private boolean avsystemAuditTrailActive;

    @Value("${techpubs.services.sqlInjection}")
    private boolean sqlInjection;

    @Value("${techpubs.services.salesforceEndpoints}")
    private boolean salesforceEndpointsActive;

    @Value("${techpubs.services.avSystemAdminDownload}")
    private boolean downloadActive;

    @Value("${techpubs.services.avSystemAdminGetDocumentData}")
    private boolean getDocumentDataActive;

    @Value("${techpubs.services.avSystemAdminUploadDocumentData}")
    private boolean uploadDocumentDataActive;

    @Value("${techpubs.services.avsystemInvokeSearchLoader}")
    private boolean avsystemInvokeSearchLoaderFlag;

    @Autowired
    private SalesforceSvcImpl salesforceSvc;

    @Autowired
    private ISalesforceCompanyData iSalesforceCompanyData;

    /**
     * Gets the list of all Salesforce companies.
     *
     * @return The list of Salesforce companies.
     */
    @GetMapping(value = "/companies", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SalesforceCompanyDto>> getSalesforceCompanies(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestParam(value = "airframe", required = false) List<String> airframe,
            @RequestParam(value = "entitlementType", required = false) List<String> entitlementType,
            HttpServletRequest request) throws TechpubsException {

        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }

        if (salesforceEndpointsActive) {
            try {
                authServiceImpl.checkResourceAccessForProduct(ssoId, "uploader", request, "avsystems");
            } catch (TechpubsException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            return ResponseEntity.ok(salesforceSvc.getSalesforceCompanies(airframe, entitlementType));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Gets the list of all Salesforce airframes.
     *
     * @return The list of Salesforce airframes.
     */
    @GetMapping(value = "/airframes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AirframeDto>> getSalesforceAirframes(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            HttpServletRequest request) throws TechpubsException {
        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }

        if (salesforceEndpointsActive) {
            try {
                authServiceImpl.checkResourceAccessForProduct(ssoId, "uploader", request, "avsystems");
            } catch (TechpubsException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            return ResponseEntity.ok(salesforceSvc.getSalesforceAirframes());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Gets the list of all System Document Type.
     *
     * @return The list of System Document Types.
     */
    @GetMapping(value = "/system-document-types", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SystemDocumentTypeLookupEntity>> getAllSystemDocumentsType(
            @RequestHeader(SM_SSOID) String ssoId,
            final HttpServletRequest request
    ) throws TechpubsException {

        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }

        if (getDocumentDataActive) {
            try {
                authServiceImpl.checkResourceAccessForProduct(ssoId, "uploader", request, "avsystems");
            } catch (TechpubsException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            return ResponseEntity.ok(iAvsystemUploader.getSystemDocumentType());
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    /**
     * Gets the list of all System Document Site.
     *
     * @return The list of System Document Sites.
     */
    @GetMapping(value = "/system-document-sites", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SystemDocumentSiteLookupEntity>> getSystemDocumentSite(
            @RequestHeader(SM_SSOID) String ssoId,
            final HttpServletRequest request
    ) throws TechpubsException {

        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }

        if (getDocumentDataActive) {
            try {
                authServiceImpl.checkResourceAccessForProduct(ssoId, "uploader", request, "avsystems");
            } catch (TechpubsException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.ok(iAvsystemUploader.getSystemDocumentSite());
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    /**
     * Post all the system document data for Add New AvSystems Document
     *
     * @param ssoId                 the ssoID of the user
     * @param request               request
     * @param addSystemDocumentsDto the body of the request
     * @return A 200 response if successful.
     */
    @PostMapping(value = "/system-document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity addNewDocument(
            @RequestHeader(SM_SSOID) String ssoId,
            @ModelAttribute AddSystemDocumentsDto addSystemDocumentsDto,
            final HttpServletRequest request
    ) {

        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }

        if (uploadDocumentDataActive) {
            try {
                authServiceImpl.checkResourceAccessForProduct(ssoId, "uploader", request, "avsystems");
            } catch (TechpubsException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            try {
                String docId = iAvsystemUploader.addSystemDocuments(addSystemDocumentsDto);

                if (avsystemAuditTrailActive) {
                    avSystemAuditLogService.writeAvSystemEditUploadAuditLog(ssoId, addSystemDocumentsDto.getDocumentTitle(), docId,"AvSystem Document Upload", addSystemDocumentsDto.getDocumentRevision());
                }

                if (avsystemInvokeSearchLoaderFlag) {
                    searchLoaderUtil.invokeAvsystemSearchLoader(docId);
                }

                return ResponseEntity.status(HttpStatus.CREATED).body(docId);
            } catch (TechpubsException techpubsException) {
                if (techpubsException.getTechpubsAppError().equals(TechpubsException.TechpubsAppError.INVALID_DOCUMENT_IDENTIFIERS)){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A document already exists with this site, type and document number");
                }
                else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        return ResponseEntity.notFound().build();

    }

    /**
     * Post all the system document data for Add New AvSystems Document
     *
     * @param ssoId                 the ssoID of the user
     * @param addSystemDocumentsDto the body of the request
     * @return A 200 response if successful.
     */
    @PutMapping(value = "/system-document/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity updateSystemDocument(
            @RequestHeader(SM_SSOID) String ssoId,
            @ModelAttribute AddSystemDocumentsDto addSystemDocumentsDto,
            @PathVariable String id,
            final HttpServletRequest request
    ) {

        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            id = SecurityEscape.cleanString(id);
        }

        if (uploadDocumentDataActive) {
            try {
                authServiceImpl.checkResourceAccessForProduct(ssoId, "uploader", request, "avsystems");
            } catch (TechpubsException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            try {
                iAvsystemUploader.updateSystemDocument(addSystemDocumentsDto, id);

                if (avsystemAuditTrailActive) {
                    avSystemAuditLogService.writeAvSystemEditUploadAuditLog(ssoId, addSystemDocumentsDto.getDocumentTitle(), id,"AvSystem Document Edit", addSystemDocumentsDto.getDocumentRevision());
                }

                if (avsystemInvokeSearchLoaderFlag) {
                    searchLoaderUtil.invokeAvsystemSearchLoader(id);
                }
            } catch (TechpubsException techpubsException) {
                if (techpubsException.getTechpubsAppError().equals(TechpubsException.TechpubsAppError.INVALID_DOCUMENT_IDENTIFIERS)){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A document already exists with this site, type and document number");
                } else if (techpubsException.getTechpubsAppError().equals(TechpubsException.TechpubsAppError.DATA_NOT_FOUND)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
                else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    /**
     * @param ssoId   the ssoID of the user
     * @param ssoId   the ssoID of the user
     * @param request request
     * @return The list of System Document Types
     * @throws TechpubsException throws an exception for unauthorized request
     */
    @GetMapping(value = "/system-documents", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<SystemDocumentDTO>> getAllSystemDocuments(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestParam(value = "searchTerm", required = false) String searchTerm,
            @RequestParam(value = "documentType", required = false) String documentType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy") String sortBy,
            final HttpServletRequest request
    ) throws TechpubsException {

        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            sortBy = SecurityEscape.cleanString(sortBy);
        }
        SortBy sort = new SortBy(sortBy);

        if (getDocumentDataActive) {
            try {
                authServiceImpl.checkResourceAccessForProduct(ssoId, "uploader", request, "avsystems");
            } catch (TechpubsException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            return ResponseEntity.ok(iAvsystemUploader.getSystemDocumentsByDocumentTypeAndPartNumber(searchTerm, documentType, page, size, sort));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * @param ssoId   the ssoID of the user
     * @param request request
     * @return System Documents for particular id
     * @throws TechpubsException throws an exception for unauthorized request
     */
    @GetMapping(value = "/system-documents/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SystemDocumentByIdDTO> getSystemDocumentById(
            @PathVariable String id,
            @RequestHeader(SM_SSOID) String ssoId,
            final HttpServletRequest request
    ) throws TechpubsException {

        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }

        if (getDocumentDataActive) {
            try {
                authServiceImpl.checkResourceAccessForProduct(ssoId, "uploader", request, "avsystems");
            } catch (TechpubsException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.ok(iAvsystemUploader.getSystemDocumentById(id));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * @param ssoId   the ssoID of the user
     * @param documentTypeId id of documentType
     * @param documentSiteId id of documentSite
     * @param documentNumber document number
     * @return boolean value - true if combination of document type, document number, and document site
     * already exists, false otherwise
     * @throws TechpubsException throws an exception for unauthorized request
     */
    @GetMapping(value = "/system-documents/exists", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> systemDocumentExists(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "documentTypeId", value = "eg. 2784f3ab-18ba-4482-ad06-94e8e6312f3f") @RequestParam(value = "documentTypeId", required = true) String documentTypeId,
            @ApiParam(name = "documentNumber", value = "eg. 212") @RequestParam(value = "documentNumber", required = true) String documentNumber,
            @ApiParam(name = "documentSiteId", value = "eg. 6c1e5311-948c-4a9b-9aa1-acf4c9b1045e") @RequestParam(value = "documentSiteId", required = true) String documentSiteId,
            HttpServletRequest request)
            throws TechpubsException {

        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }

        if (getDocumentDataActive) {
            try {
                authServiceImpl.checkResourceAccessForProduct(ssoId, "uploader", request, "avsystems");
            } catch (TechpubsException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Boolean exists = iAvsystemUploader.systemDocumentExists(documentTypeId, documentNumber, documentSiteId);
            return ResponseEntity.ok(exists);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * @param ssoId   the ssoID of the user
     * @param request request
     * @return PDF of document
     * @throws TechpubsException throws an exception for unauthorized request
     */
    @GetMapping(value = "/system-documents/download/pdf/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadSystemDocumentFile(
            @PathVariable String id,
            @RequestHeader(SM_SSOID) String ssoId,
            final HttpServletRequest request) throws TechpubsException {

        if (!downloadActive) {
            return ResponseEntity.notFound().build();
        }

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            id = SecurityEscape.cleanString(id);
        }

        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, "uploader", request, "avsystems");
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            SystemDocumentEntity systemDocument = iAvsystemUploader.getSystemDocumentEntityById(id);
            String filename = systemDocument.getFileName();
            byte[] file = iAvsystemUploader.getSystemDocumentDownload(systemDocument);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, Constants.ATTACHMENT_FILENAME + filename + "\"")
                    .contentType(MediaType.valueOf(MediaType.APPLICATION_PDF_VALUE))
                    .body(file);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get a list of Salesforce entitlements for a given company
     *
     * @param ssoId User making the request
     * @param icaoCode Company identifier
     * @param page Section of data to return
     * @param size Number of records to return
     * @param sortBy Field and direction to sort by e.g. airframe|asc
     * @return List of company entitlements
     */
    @LogExecutionTimeWithArgs
    @GetMapping(value = "/companies/{icaoCode}/entitlements", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<SalesforceCompanyAirframeEntitlementDto>> getCompanyEntitlements(
            @RequestHeader(SM_SSOID) String ssoId,
            @PathVariable("icaoCode") String icaoCode,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy") String sortBy,
            HttpServletRequest request) {

        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            icaoCode = SecurityEscape.cleanString(icaoCode);
            sortBy = SecurityEscape.cleanString(sortBy);
        }
        SortBy sort = new SortBy(sortBy);

        if (salesforceEndpointsActive) {
            try {
                authServiceImpl.checkResourceAccessForProduct(ssoId, "companies",request, "avsystems");
            } catch (TechpubsException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Page<SalesforceCompanyAirframeEntitlementDto> entitlements = salesforceSvc.getPaginatedEntitlements(icaoCode, page, size, sort);
            return ResponseEntity.ok(entitlements);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * Update the enabled status for a salesforce company in Techlib
     *
     * @param ssoId SSO of user making request
     * @param salesforceId Salesforce Company ID that will be updated
     * @param body Request body that contains toggle value
     * @return Void response with 204 http code
     */
    @LogExecutionTimeWithArgs
    @PatchMapping(value = "/companies/salesforce/enablement")
    public ResponseEntity<Void> toggleSalesforceEnableStatus(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestBody EnableStatusBody body,
            final HttpServletRequest request
    ) {
        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }

        if (salesforceEndpointsActive) {
            try {
                authServiceImpl.checkResourceAccessForProduct(ssoId, "companies", request, "avsystems");
            } catch (TechpubsException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            salesforceSvc.updateEnableStatus(body);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Downloads a lists of system documents
     *
     * @return Excel file of all system documents
     */
    @GetMapping(value = "/system-documents/excel-export-documents", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadUsers(
            @RequestHeader(SM_SSOID) String ssoId,
            HttpServletRequest request) throws ExcelException, TechpubsException {

       if (!downloadActive) {
           return ResponseEntity.notFound().build();
       }

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }

        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, "uploader", request, "avsystems");
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ResponseEntity<byte[]> response;

        try {
            FileWithBytes fileWithBytes = iAvsystemUploader.downloadSystemDocuments();
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
            log.error("Error downloading system documents data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return response;

    }

    /**
     * @param ssoId   the ssoID of the user
     * @param id ID of the document to be deleted
     * @return void
     * @throws TechpubsException throws an exception for unauthorized request
     */
    @DeleteMapping(value = "/system-documents/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SystemDocumentByIdDTO> deleteSystemDocumentById(@PathVariable String id,
                                                                          @RequestHeader(SM_SSOID) String ssoId,
                                                                          final HttpServletRequest request
    ) throws TechpubsException {

        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }

        if (uploadDocumentDataActive) {
            try {
                authServiceImpl.checkResourceAccessForProduct(ssoId, "uploader", request, "avsystems");
            } catch (TechpubsException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            try {
                iAvsystemUploader.deleteSystemDocument(id);

                if (avsystemInvokeSearchLoaderFlag) {
                    searchLoaderUtil.invokeAvsystemSearchLoader(id);
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
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
    @DeleteMapping(value = "/system-documents/hard-delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity hardDeleteSystemDocumentById(@PathVariable String id, @RequestHeader(SM_SSOID) String ssoId,
                                                       @RequestHeader(PORTAL_ID) String portalId, final HttpServletRequest request) throws TechpubsException {
        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
        }

        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, "uploader", request, "avsystems");
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            iAvsystemUploader.hardDeleteSystemDocument(id);
            return new ResponseEntity<>(HttpStatus.OK);
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
     * Get a list of Salesforce Company Lookup DTO given a ICAO Code
     *
     * @param ssoId    User making the request
     * @param icaoCode Company identifier
     * @return List of SalesforceCompanyLookup DTO
     */
    @LogExecutionTimeWithArgs
    @GetMapping(value = "/companies/{icaoCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SalesforceCompanyLookupDTO>> getSalesforceCompanyLookup(@RequestHeader(SM_SSOID) String ssoId, @PathVariable("icaoCode") String icaoCode, final HttpServletRequest request) throws TechpubsException {

        if (sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            icaoCode = SecurityEscape.cleanString(icaoCode);
        }

        if (salesforceEndpointsActive) {
            try {
                authServiceImpl.checkResourceAccessForProduct(ssoId, "companies", request, "avsystems");
            } catch (TechpubsException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            List<SalesforceCompanyLookupDTO> createCompany = iSalesforceCompanyData.getSalesforceCompanyLookup(icaoCode);
            return ResponseEntity.ok(createCompany);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

