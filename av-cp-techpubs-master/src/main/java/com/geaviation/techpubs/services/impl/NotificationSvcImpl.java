package com.geaviation.techpubs.services.impl;


import com.geaviation.techpubs.data.api.techlib.IReachNotificationData;
import com.geaviation.techpubs.data.api.techlib.enginedoc.IEngineDocumentData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;


@Service
public class NotificationSvcImpl {

    private static final Logger log = LogManager.getLogger(NotificationSvcImpl.class);

    @Autowired
    private IReachNotificationData iReachNotificationData;

    @Autowired
    private IEngineDocumentData iEngineDocumentData;


    /**
     * Update the date that an email notifications was last sent an engine document type
     * @param documentTitle The engine document title that will be updated
     * @param action The action to be taken reset or update. reset: set the title date to null: update: to current date
     */

      public boolean updateEmailNotificationDate(String documentTitle, String action, String partName, String fileName){
        Date date = new Date();
        boolean isUpdate = false;
        String message = "No match found for bookcase";
        Timestamp timestamp = null;

        if(action.equals("update")){
            timestamp = new Timestamp(date.getTime());
        }

        if(iEngineDocumentData.updateLastEmailSentDate(timestamp, documentTitle, partName, fileName)==1){
            isUpdate = true;
            message ="Last Notification date was successfully updated for Title";
        }
          log.info(message+" : {},", documentTitle);
        return isUpdate;
    }

    /**
     * Update the email notification flag for engine document title
     * @param documentTitle The engine document title that will be updated
     * @param emailFlag The flag true or false emailFlag,documentTitle,partName,fileName
     */
    public boolean updateEngineDocumentEmailFlag(boolean emailFlag, String documentTitle, String partName, String fileName){
        boolean isUpdate=false;

        String message = "No match found for Title";
        if(iEngineDocumentData.updateEmailNotificationFlag(emailFlag, documentTitle, partName, fileName) == 1) {
            isUpdate=true;
            message ="Email Flag was successfully updated for Title";
        }
        log.info(message+" : {},", documentTitle);
        return isUpdate;
    }

}
