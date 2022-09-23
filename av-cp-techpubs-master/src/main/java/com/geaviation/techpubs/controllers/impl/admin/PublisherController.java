package com.geaviation.techpubs.controllers.impl.admin;

import com.geaviation.techpubs.controllers.util.Constants;
import com.geaviation.techpubs.data.util.SecurityEscape;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.techlib.dto.BookcaseEngineModelsDto;
import com.geaviation.techpubs.models.techlib.dto.BookcaseVersionUpdateDto;
import com.geaviation.techpubs.models.techlib.response.PublisherBookcaseVersionsResponse;
import com.geaviation.techpubs.models.techlib.response.PublisherSummaryResponse;
import com.geaviation.techpubs.services.api.admin.IPublisherApp;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.FileWithBytes;
import com.geaviation.techpubs.services.impl.admin.AuthServiceImpl;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import static com.geaviation.techpubs.services.util.AppConstants.SM_SSOID;


@RestController
@RefreshScope
@RequestMapping("/admin/publisher")
public class PublisherController {

    @Value("${techpubs.services.sqlInjection}")
    private boolean sqlInjection;

    @Autowired
    private IPublisherApp iPublisherApp;

    @Autowired
    AuthServiceImpl authServiceImpl;

    /**
     * Gets a list of paginated current bookcase versions
     *
     * @param sortBy sort by column (i.e. bookcaseKey, family, engineModel, bookcaseVersion)
     * @param searchTerm sort direction (i.e. asc, desc)
     * @return The page of companies
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PublisherSummaryResponse> getPublisherSummmary(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "sortBy", value = "eg. bookcaseKey|desc") @RequestParam(value = "sortBy", defaultValue = "bookcaseTitle|asc", required = false) String sortBy,
            @ApiParam(name = "searchTerm", value = "eg. GEnx") @RequestParam(value = "searchTerm", required = false) String searchTerm,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            sortBy = SecurityEscape.cleanString(sortBy);
            searchTerm = SecurityEscape.cleanString(searchTerm);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId, "publisher", request);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(iPublisherApp.getPublisherSummary(ssoId, sortBy, searchTerm));
    }

    /**
     * Gets a list of online bookcases to download
     *
     * @return Excel file of the list of online bookcases
     */
    @GetMapping(value = "/download", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<byte[]> downloadBookcases(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "searchTerm", value = "eg. GENX") @RequestParam(value = "searchTerm", required = false) String searchTerm,
            HttpServletRequest request) {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            searchTerm = SecurityEscape.cleanString(searchTerm);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId, "publisher", request);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ResponseEntity<byte[]> response;

        try {
            FileWithBytes fileWithBytes = iPublisherApp.downloadPublisherSummary(ssoId, searchTerm);
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
     * Gets a list of engine models
     *
     * @param bookcaseKey GEK of the bookcase
     * @return List of engine models
     */
    @GetMapping(value = "/bookcase/{bookcaseKey}/engine-models", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookcaseEngineModelsDto> getBookcaseEngineModels(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "bookcase", value = "eg. gek111111") @PathVariable("bookcaseKey") String bookcaseKey,
            HttpServletRequest request) {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            bookcaseKey = SecurityEscape.cleanString(bookcaseKey);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId, "publisher", request);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(iPublisherApp.getBookcaseEngineModels(bookcaseKey));
    }

    /**
     * Gets a list of bookcase versions
     *
     * @param bookcaseKey GEK of the bookcase
     * @return List of versions and version status
     */
    @GetMapping(value = "/bookcase/{bookcaseKey}/versions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PublisherBookcaseVersionsResponse> getBookcaseVersions(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "bookcase", value = "eg. gek111111") @PathVariable("bookcaseKey") String bookcaseKey,
            HttpServletRequest request) {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            bookcaseKey = SecurityEscape.cleanString(bookcaseKey);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId, "publisher", request);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(iPublisherApp.getBookcaseVersions(bookcaseKey));
    }

    /**
     * Update statuses of bookcase versions
     *
     * @param bookcaseVersionUpdateDTO the body of the request
     * @param bookcaseKey the key of the bookcase to update
     * @return A 200 response if successful.
     */
    @PostMapping(value = "/bookcase/{bookcaseKey}/versions", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateBookcaseVersionStatus(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "bookcaseKey", value = "eg. gek111111") @PathVariable("bookcaseKey") String bookcaseKey,
            @RequestBody BookcaseVersionUpdateDto bookcaseVersionUpdateDTO,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            bookcaseKey = SecurityEscape.cleanString(bookcaseKey);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId, "publisher", request);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        iPublisherApp.updateBookcaseVersionsStatus(ssoId, bookcaseKey, bookcaseVersionUpdateDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
