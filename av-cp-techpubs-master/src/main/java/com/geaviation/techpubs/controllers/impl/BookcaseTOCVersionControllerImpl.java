package com.geaviation.techpubs.controllers.impl;

import com.geaviation.techpubs.data.api.techlib.IBookData;
import com.geaviation.techpubs.data.api.techlib.IPageBlkData;
import com.geaviation.techpubs.data.impl.AwsResourcesService;
import com.geaviation.techpubs.data.util.SecurityEscape;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.BookcaseTocModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.models.download.search.FacetQueryRestObj;
import com.geaviation.techpubs.models.download.search.PsvcSearchRequestRestObj;
import com.geaviation.techpubs.models.download.search.PsvcSearchRequester;
import com.geaviation.techpubs.models.reviewer.NotifIc;
import com.geaviation.techpubs.models.reviewer.NotifIcResponse;
import com.geaviation.techpubs.models.reviewer.NotifSb;
import com.geaviation.techpubs.models.reviewer.NotifTr;
import com.geaviation.techpubs.models.reviewer.PublishPageblkRequest;
import com.geaviation.techpubs.models.techlib.dto.BookcaseVersionStatusDTO;
import com.geaviation.techpubs.services.api.IProgramApp;
import com.geaviation.techpubs.services.impl.BookcaseTOCApp;
import com.geaviation.techpubs.services.impl.admin.AuthServiceImpl;
import com.google.gson.Gson;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.awt.print.Pageable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.geaviation.techpubs.data.util.DataConstants.*;
import static com.geaviation.techpubs.services.util.AppConstants.PORTAL_ID;
import static com.geaviation.techpubs.services.util.AppConstants.SM_SSOID;

@RestController
@RefreshScope
public class BookcaseTOCVersionControllerImpl {
    private static final Logger log = LogManager.getLogger(BookcaseTOCVersionControllerImpl.class);
    public static final String SSO_IN_PORTAL_DOES_NOT_HAVE_ACCESS_TO_BOOKCASE = "SSO {} in portal {} does not have access to bookcase {}";

    @Value("${techpubs.services.sqlInjection}")
    private boolean sqlInjection;

    @Value("${techpubs.services.NotifIcSbTr}")
    private boolean notifIcSbTr;

    @Value("${techpubs.services.categoryFlag}")
    private boolean categoryFlag;

    @Value("${PSVC.SEARCH.URL}")
    private String searchUrl;

    @Autowired
    private AwsResourcesService awsResourcesService;

    @Autowired
    private IProgramApp iProgramApp;

    @Autowired
    private BookcaseTOCApp bookcaseTOCApp;

    @Autowired
    private AuthServiceImpl authServiceImpl;

    @Autowired
    private IPageBlkData iPageBlkData;

    @Autowired
    private IBookData iBookData;

    /**
     * getBookcaseVersions service returns all of the versions associated to the bookcase passed in
     *
     * @return
     */
    @GetMapping(value = "/toc/bookcases/{bookcase}/versions", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<BookcaseVersionStatusDTO>> getBookcaseVersions(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "bookcase", value = "eg. gek114118")
            @PathVariable("bookcase") String bookcaseKey) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            bookcaseKey = SecurityEscape.cleanString(bookcaseKey);
        }

        //Ensure user has access to bookcase
        if (!iProgramApp.getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(bookcaseKey)) {
            log.error(SSO_IN_PORTAL_DOES_NOT_HAVE_ACCESS_TO_BOOKCASE,
                    ssoId, portalId, bookcaseKey);
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

        return ResponseEntity.ok(bookcaseTOCApp.getBookcaseVersionStatuses(bookcaseKey));

    }

    /**
     * getBookcaseTopVersionTOC service returns the contents of the bookcase -> books levels
     * corresponding to the particular engine program selected by the user and version of the
     * bookcase.
     *
     * @return
     */
    @GetMapping(value = "/toc/bookcases/{bookcase}/books/{version}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<BookcaseTocModel> getBookcaseTopVersionTOC(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "bookcase", value = "eg. gek114118")
            @PathVariable("bookcase") String bookcaseKey,
            @ApiParam(name = "version", value = "eg. 1.2")
            @PathVariable("version") String version,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            bookcaseKey = SecurityEscape.cleanString(bookcaseKey);
            version = SecurityEscape.cleanString(version);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId, "review-overlay", request);

            //Ensure user has access to bookcase
            if (!iProgramApp.getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(bookcaseKey)) {
                log.error(SSO_IN_PORTAL_DOES_NOT_HAVE_ACCESS_TO_BOOKCASE,
                        ssoId, portalId, bookcaseKey);
                throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
            }
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(bookcaseTOCApp.getBookcaseTOC(bookcaseKey, "", Boolean.TRUE, version));
    }

    /**
     * getBookVersionTOC service returns the contents of the book -> section -> pageblk levels
     * corresponding to the particular engine program selected by the user and the version of the
     * bookcase.
     *
     * @return
     */
    @GetMapping(value = "/toc/bookcases/{bookcase}/{book}/{version}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<BookcaseTocModel> getBookVersionTOC(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "bookcase", value = "eg. gek114118")
            @PathVariable("bookcase") String bookcaseKey,
            @ApiParam(name = "version", value = "eg. 1.2") @PathVariable("version") String version,
            @ApiParam(name = "book", value = "eg. gek114121") @PathVariable("book") String bookKey,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            bookcaseKey = SecurityEscape.cleanString(bookcaseKey);
            version = SecurityEscape.cleanString(version);
            bookKey = SecurityEscape.cleanString(bookKey);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId, "review-overlay", request);

            //Ensure user has access to bookcase
            if (!iProgramApp.getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(bookcaseKey)) {
                log.error(SSO_IN_PORTAL_DOES_NOT_HAVE_ACCESS_TO_BOOKCASE,
                        ssoId, portalId, bookcaseKey);
                throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
            }
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(bookcaseTOCApp.getBookcaseTOC(bookcaseKey, bookKey, Boolean.FALSE, version));
    }

    /**
     * getBookcaseTopTOC service returns the contents of the bookcase -> books levels only
     * corresponding to the particular engine program selected by the user.
     *
     * @return
     */
    @GetMapping(value = "/toc/bookcases/{bookcase}/books", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<BookcaseTocModel> getBookcaseTopTOC(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @ApiParam(name = "bookcase", value = "eg. gek114118")
            @PathVariable("bookcase") String bookcaseKey) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
            bookcaseKey = SecurityEscape.cleanString(bookcaseKey);
        }

        //Ensure user has access to bookcase
        if (!iProgramApp.getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(bookcaseKey)) {
            log.error(SSO_IN_PORTAL_DOES_NOT_HAVE_ACCESS_TO_BOOKCASE,
                    ssoId, portalId, bookcaseKey);
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }
        return ResponseEntity.ok(bookcaseTOCApp.getBookcaseTOC(bookcaseKey, "", Boolean.TRUE, null));
    }

    /**
     * This service updates the publish flag of the book -> section -> pageblk levels
     * corresponding to the particular engine program selected by the user and the version of the
     * bookcase.
     */
    @PostMapping(value = "/toc/bookcases/book/pageblk/publish", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> publishPageblockDocument(
            @RequestHeader(SM_SSOID) String ssoId,
            @RequestHeader(PORTAL_ID) String portalId,
            @RequestBody PublishPageblkRequest publishPageblkRequest) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            portalId = SecurityEscape.cleanString(portalId);
        }

        String[] splitString = StringUtils.split(publishPageblkRequest.getResourceUri(), '/');
        String bookcase = splitString[3];
        String version = splitString[5];
        String book = splitString[7];
        String fileName = splitString[9];

        //Ensure user has access to bookcase
        try {
            authServiceImpl.checkPostResourceAccessForReviewer(ssoId, "review-overlay", publishPageblkRequest);

            if (StringUtils.isEmpty(ssoId) || StringUtils.isEmpty(portalId) ||
                    !iProgramApp.getAuthorizedPrograms(ssoId, portalId, SubSystem.TD).contains(bookcase)) {
                log.error(SSO_IN_PORTAL_DOES_NOT_HAVE_ACCESS_TO_BOOKCASE, ssoId, portalId, bookcase);
                throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
            }
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String publicationTypeCode = publishPageblkRequest.getPublicationTypeCode();

        if (IC_PAGEBLK_TYPE.equals(publicationTypeCode)
                || SB_PAGEBLK_TYPE.equals(publicationTypeCode)
                || TR_PAGEBLK_TYPE.equals(publicationTypeCode)) {

            bookcaseTOCApp.publishPageblkDocument(bookcase, book, publicationTypeCode, version, fileName,
                    publishPageblkRequest.getKey(), publishPageblkRequest.isEmailNotification());
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String publishDate = df.format(new Date());
            awsResourcesService.writeReviewerAuditLog(ssoId, bookcase, version, "Approved for publish",
                    book, publicationTypeCode, publishPageblkRequest.getKey(), fileName,
                    publishPageblkRequest.getResourceUri(), publishDate, publishPageblkRequest.isEmailNotification());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            log.error("Invalid Parameter. Publication Type Code should be IC,TR or SB. Publication Type Code: {}", publicationTypeCode);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


  /**
   * retrieves values for notification for IC, SB, and TR for use in notifications.
   */
  @GetMapping(value = "/admin/notification/{type}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<String> getNotificationStatus(
          @RequestHeader(SM_SSOID) String ssoId,
          @RequestHeader(PORTAL_ID) String portalId,
          @PathVariable("type") String docType,
          HttpServletRequest request) throws TechpubsException {

    if(notifIcSbTr) {
        if (docType.equalsIgnoreCase("ic")) {
            NotifIcResponse notifIcResponse = new NotifIcResponse();
            ArrayList<String> geks = iBookData.getEngineModelsNotificationList();
            ArrayList<NotifIc> ics = iPageBlkData.getIcNotifications();
            notifIcResponse.processNotifMapping(geks, ics);
            iPageBlkData.disableNotificationFlag(docType);
            return ResponseEntity.ok(new Gson().toJson(notifIcResponse));
        } else if (docType.equalsIgnoreCase("sb")) {
            PsvcSearchRequester psvcSearchRequester = new PsvcSearchRequester();
            List<NotifSb> sbNotifications = iPageBlkData.getSbNotifications();

            if (categoryFlag){
                for (int notifNum = 0; notifNum < sbNotifications.size(); notifNum++) {
                    NotifSb currentNotif = sbNotifications.get(notifNum);
                    String id = "/techpubs/techdocs/pgms/" + currentNotif.getBookcaseKey() + "/versions/"
                            + currentNotif.getVersion() + "/mans/sbs/file/" + currentNotif.getFileName();
                    FacetQueryRestObj facet = new FacetQueryRestObj("overlayUri", Arrays.asList(id));
                    PsvcSearchRequestRestObj psvcSearchRequestRestObj = new PsvcSearchRequestRestObj("*", Arrays.asList(facet));
                    List<Map<String, Object>> results = psvcSearchRequester.requestResults(psvcSearchRequestRestObj, ssoId,
                            portalId, searchUrl);
                    String resultCategory = "";
                    if(results.size() > 0 && results.get(0).containsKey("category")) {
                        resultCategory = (String) results.get(0).get("category");
                    }
                    sbNotifications.get(notifNum).setCategory(resultCategory);
                }
            }

            iPageBlkData.disableNotificationFlag(docType);
            return ResponseEntity.ok(new Gson().toJson(sbNotifications));
        } else if (docType.equalsIgnoreCase("tr")) {
            List<NotifTr> trNotifications = iPageBlkData.getTRNotifications();
            iPageBlkData.disableNotificationFlag(docType);
            return ResponseEntity.ok(new Gson().toJson(trNotifications));
        }
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
  }
}