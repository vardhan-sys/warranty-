package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.AirframeSystemDocumentEntity;
import com.geaviation.techpubs.models.techlib.dto.AvSystemsNotificationDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface IAvSystemsNotificationData extends JpaRepository<AirframeSystemDocumentEntity, UUID> {
    //List<BookcaseEntity> findByBookcaseKeyIgnoreCase(String bookcaseKey);

    @Query("select new com.geaviation.techpubs.models.techlib.dto.AvSystemsNotificationDto(sd.id, al.airframe, sd.documentNumber, sd.documentDescription, sd.revisionDate,  "
            + "sd.revision, sd.fileName) "
            + "FROM SystemDocumentEntity sd, "
            + "AirframeLookupEntity al, "
            + "AirframeSystemDocumentEntity asd "
            + "WHERE sd.emailNotification = 'true' "
            + "and sd.id = asd.systemDocumentId "
            + "and al.id = asd.airframeId "
            + "and sd.lastEmailSentDate IS NULL ")
    List<AvSystemsNotificationDto> getNotifications();
}
