package com.geaviation.techpubs.controllers.impl;

import com.geaviation.techpubs.data.impl.BookcaseDataImpl;
import com.geaviation.techpubs.data.util.SecurityEscape;
import com.geaviation.techpubs.data.util.log.LogExecutionTimeWithArgs;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.PageblkDetailsDAO;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.models.techlib.response.EngineModelListResponse;
import com.geaviation.techpubs.services.api.IProgramApp;
import com.geaviation.techpubs.services.impl.BookcaseApp;
import com.geaviation.techpubs.services.impl.admin.AuthServiceImpl;
import com.geaviation.techpubs.services.util.AppConstants;
import com.geaviation.techpubs.services.util.TechpubsAppUtil;
import io.swagger.annotations.ApiParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static com.geaviation.techpubs.services.util.AppConstants.PORTAL_ID;
import static com.geaviation.techpubs.services.util.AppConstants.SM_SSOID;

@RestController
@RefreshScope
@RequestMapping("/bookcases")
public class BookcasesControllerImpl {

    private static final Logger log = LogManager.getLogger(BookcasesControllerImpl.class);

    @Value("${techpubs.services.sqlInjection}")
    private boolean sqlInjection;

    @Value("${techpubs.services.detailsEndpointFix}")
    private boolean detailsEndpointFix;

    @Autowired
    private TechpubsAppUtil techpubsAppUtil;

    @Autowired
    private IProgramApp iProgramApp;

    @Autowired
    private BookcaseApp bookcaseApp;

    @Autowired
    private BookcaseDataImpl bookcaseDataImpl;

    @Autowired
    private AuthServiceImpl authServiceImpl;

    @GetMapping(value = "/{bookcase}/books/{book}/files/{file}/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PageblkDetailsDAO> getFileDetailsNoVersion(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "bookcase", value = "eg. gek114118") @PathVariable("bookcase") String bookcase,
            @ApiParam(name = "book", value = "eg. sbs") @PathVariable("book") String book,
            @ApiParam(name = "file", value = "eg. genx-2b-sb-72-0249-r00.htm") @PathVariable("file") String file,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            bookcase = SecurityEscape.cleanString(bookcase);
            book = SecurityEscape.cleanString(book);
            file = SecurityEscape.cleanString(file);
        }

        if (detailsEndpointFix) {
            if (!iProgramApp.getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(bookcase)) {
                log.error("SSO {} in portal {} does not have access to bookcase {}", ssoId, portalId, bookcase);
                throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
            }

            PageblkDetailsDAO pageblkDetailsDAO = bookcaseApp.getFileDetails(bookcase, null, book, file);

            return ResponseEntity.ok(pageblkDetailsDAO);
        } else {
            return getFileDetails(ssoId, portalId, bookcase, AppConstants.ONLINE, book, file, request);
        }
    }

    @GetMapping(value = "/{bookcase}/versions/{version}/books/{book}/files/{file}/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PageblkDetailsDAO> getFileDetails(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "bookcase", value = "eg. gek114118") @PathVariable("bookcase") String bookcase,
            @ApiParam(name = "version", value = "eg. 2.3") @PathVariable("version") String version,
            @ApiParam(name = "book", value = "eg. sbs") @PathVariable("book") String book,
            @ApiParam(name = "file", value = "eg. genx-2b-sb-72-0249-r00.htm") @PathVariable("file") String file,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            bookcase = SecurityEscape.cleanString(bookcase);
            book = SecurityEscape.cleanString(book);
            file = SecurityEscape.cleanString(file);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId,"review-overlay", request);
            //Ensure user has access to bookcase
            if (!iProgramApp.getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(bookcase)) {
                log.error("SSO {} in portal {} does not have access to bookcase {}", ssoId, portalId, bookcase);
                throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
            }
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (AppConstants.ONLINE.equals(version)) {
            version = null;
        }

        PageblkDetailsDAO pageblkDetailsDAO = bookcaseApp.getFileDetails(bookcase, version, book, file);

        return ResponseEntity.ok(pageblkDetailsDAO);
    }

    @GetMapping(value = "/{bookcase}/versions/{version}/books/{book}/trg/{trg}/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PageblkDetailsDAO> getFileDetailsTarget(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "bookcase", value = "eg. gek114118") @PathVariable("bookcase") String bookcase,
            @ApiParam(name = "version", value = "eg. 2.3") @PathVariable("version") String version,
            @ApiParam(name = "book", value = "eg. sbs") @PathVariable("book") String book,
            @ApiParam(name = "trg", value = "eg. tk72-00-30-200-801") @PathVariable("trg") String trg,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            bookcase = SecurityEscape.cleanString(bookcase);
            book = SecurityEscape.cleanString(book);
            trg = SecurityEscape.cleanString(trg);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId,"review-overlay", request);
            //Ensure user has access to bookcase
            if (!iProgramApp.getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(bookcase)) {
                log.error("SSO {} in portal {} does not have access to bookcase {}", ssoId, portalId, bookcase);
                throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
            }
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        PageblkDetailsDAO pageblkDetailsDAO = bookcaseApp.getFileDetailsTarget(bookcase, version, book, trg);

        return ResponseEntity.ok(pageblkDetailsDAO);
    }

    /**
     * Get the engine models given by the bookcase key.
     */
    @GetMapping(value = "/{bookcase}/engine-models", produces = MediaType.APPLICATION_JSON_VALUE)
    @LogExecutionTimeWithArgs
    public ResponseEntity<EngineModelListResponse> getEngineModelsByBookcase(@ApiParam(name = "bookcase", value = "eg. gek114118") @PathVariable("bookcase") String bookcaseKey) {

        if(sqlInjection) {
            bookcaseKey = SecurityEscape.cleanString(bookcaseKey);
        }

        return ResponseEntity.ok(bookcaseApp.getEngineModelsByBookcaseKey(bookcaseKey));
    }
}
