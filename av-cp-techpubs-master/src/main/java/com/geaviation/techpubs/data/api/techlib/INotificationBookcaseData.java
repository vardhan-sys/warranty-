package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.BookcaseEntity;
import com.geaviation.techpubs.models.techlib.dto.NotificationDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface INotificationBookcaseData extends JpaRepository<BookcaseEntity, UUID> {
    List<BookcaseEntity> findByBookcaseKeyIgnoreCase(String bookcaseKey);

    @Query("select new com.geaviation.techpubs.models.techlib.dto.NotificationDto(ep.program, em.model, b2.bookType, bv.revision, "
            + "bv.revisionDate, bv.title, b.bookcaseKey, em.lastUpdatedAt, bv.creationDate, b2.bookKey) "
            + "FROM BookcaseEntity b "
            + "JOIN BookcaseVersionEntity bcv on b.id =  bcv.bookcaseId "
            + "JOIN EngineModelProgramEntity emp on b.bookcaseKey = emp.bookcaseKey "
            + "JOIN EngineProgramEntity ep on b.bookcaseKey = ep.bookcaseKey "
            + "JOIN EngineModelEntity em on emp.engineModel = em.model "
            + "JOIN BookEntity b2 on b.id = b2.bookcase "
            + "JOIN BookVersionEntity bv on b2.id =bv.book "
            + "WHERE b2.bookType not in ('tr','ic','sb') "
            + "and bcv.bookcaseVersionStatus = 'online' "
            + "and b.sendEmail = 'true' "
            + "and b.lastEmailSentDate = NULL "
            + "and bv.bookcaseVersion = bcv.bookcaseVersion ")
    List<NotificationDto> getNotifications();


}
