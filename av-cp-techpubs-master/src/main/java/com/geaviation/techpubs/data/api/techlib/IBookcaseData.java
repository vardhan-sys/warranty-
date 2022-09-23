package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.BookcaseEntity;
import com.geaviation.techpubs.models.techlib.dto.BookcaseWithOnlineVersionDto;
import com.geaviation.techpubs.models.techlib.dto.PublisherBookcaseVersionStatusDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface IBookcaseData extends JpaRepository<BookcaseEntity, UUID> {
        List<BookcaseEntity> findByBookcaseKeyIgnoreCase(String bookcaseKey);

        @Query("SELECT b " +
                "FROM BookcaseEntity b " +
                "INNER JOIN BookcaseVersionEntity bv on bv.bookcaseId = b.id " +
                "WHERE b.bookcaseKey = :bookcaseKey " +
                "AND bv.bookcaseVersion = :bookcaseVersion")
        BookcaseEntity findByBookcaseKeyAndBookcaseVersion(@Param("bookcaseKey") String bookcaseKey,
                                                           @Param("bookcaseVersion") String bookcaseVersion);

        @Query(nativeQuery = true)
        List<BookcaseWithOnlineVersionDto> findBookcasesWithOnlineVersion(String column, String order, String searchTerm);

        @Query("SELECT DISTINCT NEW com.geaviation.techpubs.models.techlib.dto.PublisherBookcaseVersionStatusDto(bv.bookcaseVersion, bv.bookcaseVersionStatus, bv.versionTimestamp) " +
                "FROM BookcaseVersionEntity bv " +
                "INNER JOIN BookcaseEntity b ON b.id = bv.bookcaseId " +
                "INNER JOIN EngineModelProgramEntity emp ON b.bookcaseKey = emp.bookcaseKey " +
                "INNER JOIN EngineModelEntity em ON emp.engineModel = em.model " +
                "WHERE b.bookcaseKey = :bookcaseKey")
        List<PublisherBookcaseVersionStatusDto> getBookcaseVersions(@Param("bookcaseKey") String bookcaseKey);

        @Query("SELECT DISTINCT NEW com.geaviation.techpubs.models.techlib.dto.PublisherBookcaseVersionStatusDto(bv.bookcaseVersion, bv.bookcaseVersionStatus, bv.versionTimestamp) " +
                "FROM BookcaseVersionEntity bv " +
                "INNER JOIN BookcaseEntity b ON b.id = bv.bookcaseId " +
                "INNER JOIN EngineProgramEntity ep on b.bookcaseKey = ep.bookcaseKey " +
                "WHERE b.bookcaseKey = :bookcaseKey")
        List<PublisherBookcaseVersionStatusDto> getSpmBookcaseVersions(@Param("bookcaseKey") String bookcaseKey);

        @Query("SELECT DISTINCT emp.engineModel " +
                "FROM EngineModelProgramEntity emp " +
                "WHERE emp.bookcaseKey = :bookcaseKeyTerm")
        List<String> getBookcaseEngineModels(@Param("bookcaseKeyTerm") String bookcaseKeyTerm);

        @Query("SELECT bv.id " +
                "FROM BookcaseEntity b " +
                "INNER JOIN BookcaseVersionEntity bv " +
                "ON b.id = bv.bookcaseId " +
                "WHERE b.bookcaseKey = :bookcaseKeyTerm " +
                "AND bv.bookcaseVersion = :bookcaseVersionTerm")
        UUID getBookcaseVersionId(@Param("bookcaseKeyTerm") String bookcaseKeyTerm,
                                  @Param("bookcaseVersionTerm") String bookcaseVersionTerm);

        @Modifying
        @Transactional
        @Query("UPDATE BookcaseEntity " +
                "SET lastEmailSentDate = :lastEmailSentDate " +
                "WHERE bookcaseKey = :bookcaseKey")
        int updateLastEmailSentDate(@Param("lastEmailSentDate") Timestamp lastEmailSentDate,
                                    @Param("bookcaseKey") String bookcaseKey);

        @Modifying
        @Transactional
        @Query("UPDATE BookcaseEntity " +
                "SET sendEmail = :sendEmail " +
                "WHERE bookcaseKey = :bookcaseKey")
        int updateSendEmail(@Param("sendEmail") Boolean sendEmail,
                            @Param("bookcaseKey") String bookcaseKey);
}
