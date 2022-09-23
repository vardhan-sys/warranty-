package com.geaviation.techpubs.controllers.impl.admin;

import com.geaviation.techpubs.controllers.util.Constants;
import com.geaviation.techpubs.data.util.SecurityEscape;
import com.geaviation.techpubs.data.util.log.LogExecutionTimeWithArgs;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.cwcadmin.dto.CompanyListDto;
import com.geaviation.techpubs.models.techlib.BookEntity;
import com.geaviation.techpubs.models.techlib.CompanyEngineModelEntity;
import com.geaviation.techpubs.models.techlib.dto.AddCompanyEngineModelDto;
import com.geaviation.techpubs.models.techlib.dto.AddCompanyEngineTechLevelDto;
import com.geaviation.techpubs.models.techlib.dto.AddEngineSMMDocsDto;
import com.geaviation.techpubs.models.techlib.dto.SMMDocsDto;
import com.geaviation.techpubs.models.techlib.dto.TechLevelEngineResponse;
import com.geaviation.techpubs.models.techlib.response.CompanyMdmEngineModelResponse;
import com.geaviation.techpubs.services.api.admin.ICompanyApp;
import com.geaviation.techpubs.services.api.admin.IEngineApp;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;
import com.geaviation.techpubs.services.impl.admin.AuthServiceImpl;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.geaviation.techpubs.services.util.AppConstants.SM_SSOID;

@RestController
@RefreshScope
@RequestMapping("/admin/companies")
public class CompaniesController {

    @Value("${techpubs.services.sqlInjection}")
    private boolean sqlInjection;

    @Autowired
    private ICompanyApp iCompanyApp;

    @Autowired
    private IEngineApp iEngineApp;

    @Autowired
    AuthServiceImpl authServiceImpl;

    /**
     * Gets a list of paginated companies
     *
     * @param page the page number in results (defaults to 0)
     * @param sortBy sort by column (i.e. company, icaoCode, dunsNum)
     * @param searchTerm sort direction (i.e. asc, desc)
     * @return The page of companies
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<CompanyListDto>> getPortalCompanies(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "page", value = "eg. 0") @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @ApiParam(name = "sortBy", value = "eg. company|desc") @RequestParam(value = "sortBy", defaultValue = "company|asc", required = false) String sortBy,
            @ApiParam(name = "searchTerm", value = "eg. DAL") @RequestParam(value = "searchTerm", required = false) String searchTerm,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            sortBy = SecurityEscape.cleanString(sortBy);
            searchTerm = SecurityEscape.cleanString(searchTerm);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId, "companies", request);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(iCompanyApp.getCompanies(page, sortBy, searchTerm));
    }

    /**
     * Gets a list of companies to download
     *
     * @return Excel file of the list of companies
     */
    // TODO: Correct the status code on failure
    @GetMapping(value = "/download", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<byte[]> downloadCompanies(
            @RequestHeader(SM_SSOID) String ssoId,
            HttpServletRequest request) throws ExcelException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId, "companies", request);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ResponseEntity<byte[]> response;

        try {
            FileWithBytes fileWithBytes = iCompanyApp.downloadCompanies();
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
        } catch (ExcelException e) {
            response = ResponseEntity.ok(Constants.NO_DATA_TO_DOWNLOAD.getBytes());
        }

        return response;
    }

    /**
     * Get the full list of Family / Models of all engines, and a boolean flag of whether the
     * selected company has access to them.
     *
     * @param company The ompany name to check which engine models they are entitled to
     * @return list of all family -> models
     */
    @Deprecated
    @GetMapping(value = "/{company}/mdm-engine-models", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyMdmEngineModelResponse> getAllEngineModels(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "company", value = "eg. delta air lines") @PathVariable("company") String company,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            company = SecurityEscape.cleanString(company);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId, "companies", request);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(iEngineApp.getCompanyMdmEngineModels(ssoId, company));
    }

    /**
     * Get list of Engine Models a user can enable documents for a specified company name.
     *
     * @param icaoCode Unique company identifier to check which engine models to return
     * @return list of org engine models with boolean field whether they have
     * documents previously enabled.
     */
    @Deprecated
    @GetMapping(value = "/{icaoCode}/engine-models", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompanyEngineModelEntity>> getEngineModels(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "icaoCode", value = "eg. DAL") @PathVariable("icaoCode") String icaoCode,
            HttpServletRequest request) {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            icaoCode = SecurityEscape.cleanString(icaoCode);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId, "companies", request);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(iEngineApp.getSavedCompanyEngineModels(ssoId, icaoCode));
    }

    /**
     *  Post the models the user can enable documents for with the specified icao code
     *
     * @param icaoCode Unique company identifier to check which engine models to return
     */
    @Deprecated
    @PostMapping(value = "/{icaoCode}/engine-models", consumes = MediaType.APPLICATION_JSON_VALUE)
    @LogExecutionTimeWithArgs
    public ResponseEntity<Void> saveCompanyEngineModels(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "icaoCode", value = "eg. DAL") @PathVariable("icaoCode") String icaoCode,
            @RequestBody AddCompanyEngineModelDto addCompanyEngineModelDto,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            icaoCode = SecurityEscape.cleanString(icaoCode);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId, "companies", request);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        iEngineApp.saveCompanyEngineModels(ssoId, icaoCode, addCompanyEngineModelDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Delete Company Engine Access and access company has to all documents cascaded from the engine.
     *
     * @param icaoCode Unique company identifier to delete engine from
     * @param engineModel Engine model to remove access from Company
     */
    @Deprecated
    @DeleteMapping(value = "/{icaoCode}/engine-models/{engineModel}")
    public ResponseEntity<Void> deleteCompanyEngineAccess (
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "icaoCode", value = "eg. DAL") @PathVariable("icaoCode") String icaoCode,
            @ApiParam(name = "engineModel", value = "eg. CF34-10") @PathVariable("engineModel") String engineModel,
            HttpServletRequest request)
            throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            icaoCode = SecurityEscape.cleanString(icaoCode);
            engineModel = SecurityEscape.cleanString(engineModel);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId, "companies", request);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        iEngineApp.deleteCompanyEngineModel(ssoId, icaoCode, engineModel);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Get list of documents for a engine model and icao code.
     *
     * @param icaoCode Unique company identifier to check which engine model documents to return
     * @param engineModel Engine model to retrieve documents for
     * @return list of books associated to the engine model / icao code
     */
    @Deprecated
    @GetMapping(value = "/{icaoCode}/engine-models/{engineModel}/books", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BookEntity>> getEngineDocuments (
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "icaoCode", value = "eg. DAL") @PathVariable("icaoCode") String icaoCode,
            @ApiParam(name = "engineModel", value = "eg. CF34-10") @PathVariable("engineModel") String engineModel,
            HttpServletRequest request)
            throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            icaoCode = SecurityEscape.cleanString(icaoCode);
            engineModel = SecurityEscape.cleanString(engineModel);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId, "companies", request);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(iEngineApp.getCompanyEngineModelBooks(ssoId, icaoCode, engineModel));
    }

    /**
     * Get list of documents for a engine model and icao code.
     *
     * @param icaoCode Unique company identifier to check which engine model documents to return
     * @param engineModel Engine model to retrieve documents for
     * @return list of SMM documents associated to the engine model / icao code
     */
    @Deprecated
    @GetMapping(value = "/{icaoCode}/engine-models/{engineModel}/smm-docs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SMMDocsDto> getSMMDocuments (
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "icaoCode", value = "eg. DAL") @PathVariable("icaoCode") String icaoCode,
            @ApiParam(name = "engineModel", value = "eg. CF34-10") @PathVariable("engineModel") String engineModel,
            HttpServletRequest request)
            throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            icaoCode = SecurityEscape.cleanString(icaoCode);
            engineModel = SecurityEscape.cleanString(engineModel);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId, "companies", request);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(iEngineApp.getCompanyEngineModelSMMDocuments(ssoId, icaoCode, engineModel));
    }

    /**
     * Save list of SMM Documents for an icao code.
     *
     * @param icaoCode Unique company identifier to save engines document enablements
     */
    @Deprecated
    @PostMapping(value = "/{icaoCode}/engine-models/smm-docs", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> saveSMMDocuments (
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "icaoCode", value = "eg. DAL") @PathVariable("icaoCode") String icaoCode,
            @ApiParam(name = "enable", value ="eg. TRUE") @RequestParam("enable") Boolean enable,
            @RequestBody AddEngineSMMDocsDto addEngineSMMDocsDto,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            icaoCode = SecurityEscape.cleanString(icaoCode);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId, "companies", request);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        iEngineApp.saveCompanyEngineModelSMMDocuments(ssoId, icaoCode, addEngineSMMDocsDto, enable);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Get list of technology levels for a engine model and icao code.
     *
     * @param icaoCode Unique company identifier to check which tech levels to return
     * @param engineModel Engine model to retrieve tech levels for
     * @return list of tech levels associated to the engine model / icao code
     */
    @Deprecated
    @GetMapping(value = "/{icaoCode}/engine-models/{engineModel}/technology-level", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TechLevelEngineResponse>> getTechnologyLevels (
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "icaoCode", value = "eg. DAL") @PathVariable("icaoCode") String icaoCode,
            @ApiParam(name = "engineModel", value = "eg. CF34-10") @PathVariable("engineModel") String engineModel,
            HttpServletRequest request)
            throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            icaoCode = SecurityEscape.cleanString(icaoCode);
            engineModel = SecurityEscape.cleanString(engineModel);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId, "companies", request);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(iEngineApp.getCompanyEngineModelTechLevel(ssoId, icaoCode, engineModel));
    }

    /**
     * Save list of Technology Levels for an ICAO Code and Engine Model.
     *
     * @param icaoCode Unique company identifier to Save Technology Level Enablements
     */
    @Deprecated
    @PostMapping(value = "/{icaoCode}/engine-models/technology-level", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> saveCompanyTechnologyLevels (
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "icaoCode", value = "eg. DAL") @PathVariable("icaoCode") String icaoCode,
            @RequestBody AddCompanyEngineTechLevelDto addCompanyEngineTechLevelDto,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            icaoCode = SecurityEscape.cleanString(icaoCode);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId, "companies", request);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        iEngineApp.saveCompanyEngineModelTechnologyLevels(ssoId, icaoCode, addCompanyEngineTechLevelDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
