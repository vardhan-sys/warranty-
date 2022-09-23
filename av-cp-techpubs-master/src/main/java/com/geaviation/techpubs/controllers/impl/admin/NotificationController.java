package com.geaviation.techpubs.controllers.impl.admin;

import com.geaviation.techpubs.data.impl.AvSystemsDataImpl;
import com.geaviation.techpubs.data.impl.NotificationDataImpl;
import com.geaviation.techpubs.data.impl.ReachNotificationDataImpl;
import com.geaviation.techpubs.data.util.SecurityEscape;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.techlib.dto.AvSystemsNotificationDto;
import com.geaviation.techpubs.models.techlib.dto.NotificationDto;
import com.geaviation.techpubs.models.techlib.dto.ReachNotificationDto;
import com.geaviation.techpubs.services.impl.AvSystemsNotificationSvcImpl;
import com.geaviation.techpubs.services.impl.NotificationSvcImpl;
import com.geaviation.techpubs.services.impl.BookcaseApp;
import com.geaviation.techpubs.services.impl.admin.AuthServiceImpl;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.geaviation.techpubs.services.util.AppConstants.SM_SSOID;

//TODO Split Endpoints by Tab and split implementations into their own packages / files.
@RestController
@RequestMapping("/admin/notification")
public class NotificationController {

    @Autowired
    NotificationDataImpl notificationDataImpl;

    @Autowired
    AuthServiceImpl authServiceImpl;

    @Autowired
    private BookcaseApp bookcaseApp;

    @Autowired
    ReachNotificationDataImpl reachNotificationDataImpl;

    @Autowired
    private NotificationSvcImpl notificationSvc;

    @Autowired
    private AvSystemsNotificationSvcImpl avSystemsNotificationSvc;

    @Autowired
    private AvSystemsDataImpl avSystemsDataImpl;

    @Value("${techpubs.services.sqlInjection}")
    private boolean sqlInjection;

    @GetMapping(value = "/manuals", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NotificationDto>> getOnlineNotification(
            @RequestHeader(SM_SSOID) String ssoId,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId, "publisher", request);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(notificationDataImpl.getNotifications());
    }

    /**
     *  Set the email notification flag to true or false
     */
    @PostMapping(value = "/bookcases/{bookcase}/email-flag", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> updateEmailFlag(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "bookcase", value = "eg. gek114118", allowMultiple = false, required = true) @PathVariable("bookcase") String bookcase,
            @ApiParam(name = "flag", value = "eg true / false", allowMultiple = false, required = true) @RequestParam("flag") Boolean emailFlag,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            bookcase = SecurityEscape.cleanString(bookcase);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId, "publisher", request);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(bookcaseApp.updateEmailFlag(emailFlag,bookcase));
    }

    /**
     *  update last email sent date for a bookcase or reset it
     */
    @PostMapping(value = "/bookcases/{bookcase}/email-notification-date", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> updateEmailLastNotification(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "bookcase", value = "eg. gek114118", allowMultiple = false, required = true) @PathVariable("bookcase") String bookcase,
            @ApiParam(name = "action", value = "eg. update or reset", allowMultiple = false, required = true) @RequestParam("action") String action,
            HttpServletRequest request)  throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            bookcase = SecurityEscape.cleanString(bookcase);
            action = SecurityEscape.cleanString(action);
        }

        try {
            authServiceImpl.checkResourceAccess(ssoId, "publisher", request);
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (action.equals("update") || action.equals("reset")) {
            return ResponseEntity.ok(bookcaseApp.updateEmailNotificationDate(bookcase, action));
        } else {
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }
    }

    @GetMapping(value = "/reach", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ReachNotificationDto>> getReachNotification(
            @RequestHeader(SM_SSOID) String ssoId,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }

        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, "publisher", request, "enginemanuals");
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(reachNotificationDataImpl.getNotifications());
    }

    /**
     *  update last email sent date for a bookcase or reset it
     */
    @PostMapping(value = "/reach/set-email-notification-date", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> updateEngineDocumentSentEmailDate(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "documentTitle", value = "eg. CFM Doc", allowMultiple = false, required = true) @RequestParam("documentTitle") String documentTitle,
            @ApiParam(name = "action", value = "eg. update or reset", allowMultiple = false, required = true) @RequestParam("action") String action,
            @ApiParam(name = "partName", value = "eg. ML1", allowMultiple = false, required = true) @RequestParam("partName") String partName,
            @ApiParam(name = "fileName", value = "eg. test.pdf", allowMultiple = false, required = true) @RequestParam("fileName") String fileName,
            HttpServletRequest request)  throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            documentTitle = SecurityEscape.cleanString(documentTitle);
            partName = SecurityEscape.cleanString(partName);
            fileName = SecurityEscape.cleanString(fileName);
            action = SecurityEscape.cleanString(action);
        }

        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, "publisher", request, "enginemanuals");
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (action.equals("update") || action.equals("reset")) {
            return ResponseEntity.ok(notificationSvc.updateEmailNotificationDate(documentTitle, action, partName, fileName));
        } else {
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }
    }

    /**
     *  Set the email notification flag to true or false
     */
    @PostMapping(value = "/reach/set-email-flag", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> updateEngineDocumentEmailFlag(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "documentTitle", value = "eg. Document", allowMultiple = false, required = true) @RequestParam("documentTitle") String documentTitle,
            @ApiParam(name = "partName", value = "eg. ML1", allowMultiple = false, required = true) @RequestParam("partName") String partName,
            @ApiParam(name = "fileName", value = "eg. test.pdf", allowMultiple = false, required = true) @RequestParam("fileName") String fileName,
            @ApiParam(name = "flag", value = "eg true / false", allowMultiple = false, required = true) @RequestParam("flag") Boolean emailFlag,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            documentTitle = SecurityEscape.cleanString(documentTitle);
            partName = SecurityEscape.cleanString(partName);
            fileName = SecurityEscape.cleanString(fileName);
        }

        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, "publisher", request, "enginemanuals");
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(notificationSvc.updateEngineDocumentEmailFlag(emailFlag,documentTitle,partName,fileName));
    }


    @GetMapping(value = "/avsystems", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AvSystemsNotificationDto>> getAvSystemsNotification(
            @RequestHeader(SM_SSOID) String ssoId,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
        }

        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, "publisher", request, "enginemanuals");
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(avSystemsDataImpl.getNotifications());
    }

    /**
     *  update last email sent date for a bookcase or reset it
     */
    @PostMapping(value = "/avsystems/set-email-notification-date", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> updateEngineDocumentSentEmailDate(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "action", value = "eg. update or reset", allowMultiple = false, required = true) @RequestParam("action") String action,
            @ApiParam(name = "documentNumber", value = "eg. 12321233", allowMultiple = false, required = true) @RequestParam("documentNumber") String documentNumber,
            @ApiParam(name = "systemDocumentId", value = "eg. asdflkj", allowMultiple = false, required = true) @RequestParam("systemDocumentId") String systemDocumentId,
            HttpServletRequest request)  throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            action = SecurityEscape.cleanString(action);
            documentNumber = SecurityEscape.cleanString(documentNumber);
            systemDocumentId = SecurityEscape.cleanString(systemDocumentId);
        }

        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, "publisher", request, "enginemanuals");
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (action.equals("update") || action.equals("reset")) {
            return ResponseEntity.ok(avSystemsNotificationSvc.updateEmailNotificationDate(action, documentNumber, systemDocumentId));
        } else {
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }
    }

    /**
     *  Set the email notification flag to true or false
     */
    @PostMapping(value = "/avsystems/set-email-flag", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> updateAvSystemsEmailFlag(
            @RequestHeader(SM_SSOID) String ssoId,
            @ApiParam(name = "documentNumber", value = "eg. 12321233", allowMultiple = false, required = true) @RequestParam("documentNumber") String documentNumber,
            @ApiParam(name = "flag", value = "eg true / false", allowMultiple = false, required = true) @RequestParam("flag") Boolean emailFlag,
            @ApiParam(name = "systemDocumentId", value = "eg. asdflkj", allowMultiple = false, required = true) @RequestParam("systemDocumentId") String systemDocumentId,
            HttpServletRequest request) throws TechpubsException {

        if(sqlInjection) {
            ssoId = SecurityEscape.cleanString(ssoId);
            documentNumber = SecurityEscape.cleanString(documentNumber);
            systemDocumentId = SecurityEscape.cleanString(systemDocumentId);
        }

        try {
            authServiceImpl.checkResourceAccessForProduct(ssoId, "publisher", request, "enginemanuals");
        } catch (TechpubsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(avSystemsNotificationSvc.updateEngineDocumentEmailFlag(emailFlag,documentNumber,systemDocumentId));
    }

}
