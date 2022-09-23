package com.geaviation.techpubs.services.impl;


import com.geaviation.techpubs.data.api.techlib.IAvSystemsNotificationData;
import com.geaviation.techpubs.data.api.techlib.ISystemDocumentData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;


@Service
public class AvSystemsNotificationSvcImpl {

    private static final Logger log = LogManager.getLogger(AvSystemsNotificationSvcImpl.class);

    @Autowired
    private IAvSystemsNotificationData iAvSystemsNotificationData;

    @Autowired
    private ISystemDocumentData iSystemDocumentData;




    /**
     * Update the date that an email notifications was last sent an system document type
     * @param documentNumber The engine document title that will be updated
     * @param action The action to be taken reset or update. reset: set the title date to null: update: to current date
     * @return
     */
    public boolean updateEmailNotificationDate(String action, String documentNumber, String systemDocumentId ){
        Date date = new Date();
        boolean isUpdate = false;
        String message = "No match found for bookcase";
        Timestamp timestamp = null;

        if(action.equals("update")){
            timestamp = new Timestamp(date.getTime());
        }

        if(iSystemDocumentData.updateLastEmailSentDate(timestamp, UUID.fromString(systemDocumentId))==1){
            isUpdate = true;
            message ="Last Notification date was successfully updated for Title";
        }
          log.info(message+" : {},", documentNumber);
        return isUpdate;
    }


    /**
     * Update the email notification flag for engine document title
     * @param emailFlag The flag true or false emailFlag,documentNumber,revisionDate,revision
     * @param documentNumber The engine document title that will be updated
     * @return
     */
    public boolean updateEngineDocumentEmailFlag(boolean emailFlag, String documentNumber,  String systemDocumentId){
        boolean isUpdate=false;

        String message = "No match found for Title";
        if(iSystemDocumentData.updateEmailNotificationFlag(emailFlag, UUID.fromString(systemDocumentId)) == 1) {

            isUpdate=true;
            message ="Email Flag was successfully updated for Title";
        }
        log.info(message+" : {},", documentNumber);
        return isUpdate;
    }

}
