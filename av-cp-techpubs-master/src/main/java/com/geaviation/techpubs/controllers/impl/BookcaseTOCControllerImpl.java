package com.geaviation.techpubs.controllers.impl;

import com.geaviation.techpubs.data.impl.BookcaseDataImpl;
import com.geaviation.techpubs.data.util.SecurityEscape;
import com.geaviation.techpubs.data.util.log.LogExecutionTimeWithArgs;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.BookcaseTocModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.services.api.IProgramApp;
import com.geaviation.techpubs.services.impl.BookcaseTOCApp;
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
import org.springframework.web.bind.annotation.RestController;

import static com.geaviation.techpubs.services.util.AppConstants.PORTAL_ID;
import static com.geaviation.techpubs.services.util.AppConstants.SM_SSOID;

@RestController
@RefreshScope
public class BookcaseTOCControllerImpl {

    private static final Logger log = LogManager.getLogger(BookcaseTOCControllerImpl.class);

    @Value("${techpubs.services.sqlInjection}")
    private boolean sqlInjection;

    @Autowired
    private IProgramApp iProgramApp;

    @Autowired
    private BookcaseTOCApp bookcaseTOCApp;

    @Autowired
    private BookcaseDataImpl bookcaseDataImpl;


    /**
     * getBookcaseTOC service returns the contents of the bookcase corresponding to the
     * particular engine program selected by the user.
     *
     * @return
     */
    @GetMapping(value = "/toc/bookcases/{bookcase}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @LogExecutionTimeWithArgs
    public ResponseEntity<BookcaseTocModel> getBookcaseTOC(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "program", value = "eg. gek114118", allowMultiple = false, required = false)
            @PathVariable("bookcase") String bookcaseKey) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            bookcaseKey = SecurityEscape.cleanString(bookcaseKey);
        }

        //Ensure user has access to bookcase
        if (!iProgramApp.getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(bookcaseKey)) {
            log.error("SSO " + ssoId + " in portal " + portalId + " does not have access to bookcase " + bookcaseKey);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(bookcaseTOCApp.getBookcaseTOC(bookcaseKey, "", Boolean.FALSE, null));
    }

}