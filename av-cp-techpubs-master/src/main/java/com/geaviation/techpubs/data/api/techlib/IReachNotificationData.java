package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.BookcaseEntity;
import com.geaviation.techpubs.models.techlib.dto.ReachNotificationDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface IReachNotificationData extends JpaRepository<BookcaseEntity, UUID> {

    @Query("Select new com.geaviation.techpubs.models.techlib.dto.ReachNotificationDto (edtl.value ,ed.documentTitle, edem.engineModel, em.family, "
        +"ed.partName,  ecn.casNumber, ed.issueDate, ed.fileName ) "
        +"FROM EngineDocumentTypeLookupEntity edtl "
        +"JOIN EngineDocumentEntity ed on edtl.id = ed.engineDocumentTypeId "
        +"JOIN EngineDocumentEngineModelEntity edem on edem.engineDocumentId = ed.id "
        +"JOIN EngineModelEntity em on edem.engineModel = em.model "
        +"JOIN EngineCASNumberEntity ecn on ecn.engineDocumentId = ed.id "
        +"WHERE edtl.value ='REACH' "
        +"and ed.lastEmailSentDate = NULL "
        +"and ed.emailNotification = 'true' ")
   List<ReachNotificationDto> getNotifications();

}
