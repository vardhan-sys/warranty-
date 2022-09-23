package com.geaviation.techpubs.services.impl;

import com.geaviation.techpubs.data.api.techlib.IPermissionData;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.BookcaseContentDAO;
import com.geaviation.techpubs.models.BookcaseContentModel;
import com.geaviation.techpubs.models.SubSystem;
import com.geaviation.techpubs.services.api.IBookcaseContentApp;
import com.geaviation.techpubs.services.util.AppConstants;
import com.geaviation.techpubs.services.util.TechpubsAppUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class BookcaseContentModelApp {

  //Remove this class during US478605 feature flag cleanup
    @Autowired
    private BookcaseApp bookcaseAppSvc;

    @Autowired
    private IPermissionData iPermissionData;

    @Autowired
    private BookcaseContentAppRegServices bookcaseContentAppRegServices;

    private static final Logger log = LogManager.getLogger(BookcaseContentModelApp.class);

    @Deprecated
    //Remove this  during US478605 feature flag cleanup
    public BookcaseContentModel getBookcaseItemModel(String sso, String portalId,  Map<String, String> searchFilters, Map<String, String> parameterMap) {
         BookcaseContentModel bookcaseContentModel;
         try {
           String pageBlkType = parameterMap.get(AppConstants.TYPE);
           if (AppConstants.MANUAL.equals(pageBlkType)) {
               pageBlkType = SubSystem.BOOK.name();
           }
           IBookcaseContentApp bookcaseContentApp = getBookcaseItemApp(pageBlkType);

           List<String> authorizedBookcaseKeysForRequest = bookcaseAppSvc.getAuthorizedBookcaseKeysForRequest(
                   sso, portalId, searchFilters);

           List<BookcaseContentDAO> bookcaseContentDAOS = bookcaseContentApp.getBookcaseItems(portalId,
                   authorizedBookcaseKeysForRequest, pageBlkType);

           TechpubsAppUtil.sortBooks(bookcaseContentDAOS, parameterMap);

           int resultSize = bookcaseContentDAOS.size();
           int iDisplayLength = Integer.parseInt(parameterMap.get(AppConstants.IDISPLAYLENGTH));
           int iDisplayStart = Integer.parseInt(parameterMap.get(AppConstants.IDISPLAYSTART));
           String sEcho = parameterMap.get(AppConstants.SECHO);

           bookcaseContentModel = new BookcaseContentModel();
           bookcaseContentModel.setIDisplayLength(iDisplayLength);
           bookcaseContentModel.setIDisplayStart(iDisplayStart);
           bookcaseContentModel.setITotalDisplayRecords(bookcaseContentDAOS.size());
           bookcaseContentModel.setITotalRecords(bookcaseContentDAOS.size());
           bookcaseContentModel.setSEcho(sEcho);

           bookcaseContentModel.setBookcaseITems(
               bookcaseContentDAOS.subList((Math.min(iDisplayStart, resultSize)),
                   (Math.min(iDisplayStart + iDisplayLength, resultSize))));
           bookcaseContentModel.setSuccess(true);

         } catch(Exception e) {
           log.error(e.getStackTrace());
           bookcaseContentModel = new BookcaseContentModel();
         }

         return bookcaseContentModel;
    }


    private IBookcaseContentApp getBookcaseItemApp(String type) throws TechpubsException {
        SubSystem subSystem = TechpubsAppUtil.getSubSystem(type);

        IBookcaseContentApp iBookcaseItemModelApp = bookcaseContentAppRegServices.getSubSystemService(subSystem);

        if (iBookcaseItemModelApp == null) {
            log.error("docs (" + TechpubsException.TechpubsAppError.INVALID_PARAMETER.getErrorCode()
                    + ") - "
                    + TechpubsException.TechpubsAppError.INVALID_PARAMETER.getErrorMsg() + "("
                    + AppConstants.TYPE + "="
                    + subSystem + ")");
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }

        return iBookcaseItemModelApp;
    }
}
