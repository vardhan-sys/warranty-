package com.geaviation.techpubs.services.impl.admin;

import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.reviewer.PublishPageblkRequest;
import com.geaviation.techpubs.services.impl.BookcaseVersionApp;
import com.geaviation.techpubs.services.util.AppConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerMapping;
import javax.servlet.http.HttpServletRequest;

import java.util.Map;

@Service
public class AuthServiceImpl {

    @Autowired
    AuthorizationAppImpl authorizationAppimpl;

    @Autowired
    BookcaseVersionApp bookcaseVersionApp;

    private static final Logger log = LogManager.getLogger(AuthServiceImpl.class);
    private static final String REVIEWER_RESOURCE = "review-overlay";

    private String doesNotHaveAccessToMessage(String ssoId, String thing) {
        return "User " + ssoId + " does not have access to " + thing;
    }
   
    /**
     * going forward we will be checking resource access for a product(avsystems,enginemanuals,aero)
     * Below method call will be replaced by checkResourceAccessForProduct(String ssoId, String resource, HttpServletRequest request, String product)
     * once we implement product related changes in all endpoints.
     */
    @Deprecated
    /* Use checkResourceAccessForProduct */
    public void checkResourceAccess(String ssoId, String resource, HttpServletRequest request) throws TechpubsException {

        String httpMethod = request.getMethod();
        Object pathParameters = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        String action = AppConstants.EDIT_ACTION;

        if ("GET".equals(httpMethod)) {
            action = AppConstants.VIEW_ACTION;
        }

        if (resource.equals(REVIEWER_RESOURCE)) {
            Map<String, String> params = (Map<String, String>) pathParameters;
            String version = params.get("version");
            String bookcase = params.get("bookcase");
            String onlineVersion = bookcaseVersionApp.getOnlineBookcaseVersion(bookcase);

            boolean isOnlineVersion = false;

            if (version != null && version.equals(onlineVersion)) {
                isOnlineVersion = true;
            }

            if (!isOnlineVersion) {
                boolean isReviewer = authorizationAppimpl.checkIfUserHasPermission(ssoId, resource, action);
                if (isReviewer) {
                    boolean hasBookcaseAccess = authorizationAppimpl.checkIfUserHasBookcaseAccess(ssoId, resource, bookcase);
                    if (!hasBookcaseAccess) {
                        log.error(doesNotHaveAccessToMessage(ssoId, bookcase));
                        throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
                    }
                } else {
                    log.error(doesNotHaveAccessToMessage(ssoId, bookcase));
                    throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
                }
            }
        } else if (!authorizationAppimpl.checkIfUserHasPermission(ssoId, resource, action)) {
            log.error(doesNotHaveAccessToMessage(ssoId, resource));
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

    }

    public void checkPostResourceAccessForReviewer(String ssoId, String resource, PublishPageblkRequest publishPageblKRequest) throws TechpubsException {

        String action = AppConstants.EDIT_ACTION;
        String version;
        String bookcase;

        if (resource.equals(REVIEWER_RESOURCE)) {
            String[] splitString = StringUtils.split(publishPageblKRequest.getResourceUri(), '/');
            bookcase = splitString[3];
            version = splitString[5];

            String onlineVersion = bookcaseVersionApp.getOnlineBookcaseVersion(bookcase);
            boolean isOnlineVersion = false;

            if (version != null && version.equals(onlineVersion)) {
                isOnlineVersion = true;
            }

            if (!isOnlineVersion) {
                boolean isReviewer = authorizationAppimpl.checkIfUserHasPermission(ssoId, resource, action);
                if (isReviewer) {
                    boolean hasBookcaseAccess = authorizationAppimpl.checkIfUserHasBookcaseAccess(ssoId, resource, bookcase);
                    if (!hasBookcaseAccess) {
                        log.error(doesNotHaveAccessToMessage(ssoId, bookcase));
                        throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
                    }
                } else {
                    log.error(doesNotHaveAccessToMessage(ssoId, bookcase));
                    throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
                }
            }
        } else if (!authorizationAppimpl.checkIfUserHasPermission(ssoId, resource, action)) {
            log.error(doesNotHaveAccessToMessage(ssoId, resource));
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }

    }
    
    public void checkResourceAccessForProduct(String ssoId, String resource, HttpServletRequest request, String product) throws TechpubsException {

        String httpMethod = request.getMethod();
        Object pathParameters = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        String action = AppConstants.EDIT_ACTION;

        if ("GET".equals(httpMethod)) {
            action = AppConstants.VIEW_ACTION;
        }

        if (resource.equals(REVIEWER_RESOURCE)) {
            Map<String, String> params = (Map<String, String>) pathParameters;
            String version = params.get("version");
            String bookcase = params.get("bookcase");
            String onlineVersion = bookcaseVersionApp.getOnlineBookcaseVersion(bookcase);

            boolean isOnlineVersion = false;

            if (version != null && version.equals(onlineVersion)) {
                isOnlineVersion = true;
            }

            if (!isOnlineVersion) {
                boolean isReviewer = authorizationAppimpl.checkIfUserHasPermission(ssoId, resource, action,product);
                if (isReviewer) {
                    boolean hasBookcaseAccess = authorizationAppimpl.checkIfUserHasBookcaseAccess(ssoId, resource, bookcase);
                    if (!hasBookcaseAccess) {
                        log.error(doesNotHaveAccessToMessage(ssoId, bookcase));
                        throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
                    }
                } else {
                    log.error(doesNotHaveAccessToMessage(ssoId, bookcase));
                    throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
                }
            }
        } else if (!authorizationAppimpl.checkIfUserHasPermission(ssoId, resource, action,product)) {
            log.error(doesNotHaveAccessToMessage(ssoId, resource));
            throw new TechpubsException(TechpubsException.TechpubsAppError.NOT_AUTHORIZED);
        }
    }
}
