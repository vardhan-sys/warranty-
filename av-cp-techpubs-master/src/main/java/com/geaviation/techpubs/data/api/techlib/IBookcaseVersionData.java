package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.BookcaseEntity;
import com.geaviation.techpubs.models.techlib.BookcaseVersionEntity;
import com.geaviation.techpubs.models.techlib.dto.BookcaseVersionStatusDTO;
import com.geaviation.techpubs.models.techlib.dto.OfflineDVDInfoDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface IBookcaseVersionData extends JpaRepository<BookcaseVersionEntity, UUID> {
    @Query("SELECT new com.geaviation.techpubs.models.techlib.dto.BookcaseVersionStatusDTO(bv.bookcaseVersion, bv.bookcaseVersionStatus, bv.id) " +
            "FROM BookcaseVersionEntity bv " +
            "WHERE bv.bookcaseId = :bookcase " +
            "AND bv.bookcaseVersionStatus <> 'archived'")
    List<BookcaseVersionStatusDTO> findBookcaseVersionAndStatus(BookcaseEntity bookcase);

    /**
     * Return the online bookcase version for a given bookcase key
     *
     * @param bookcaseKey - Bookcase key
     * @return String - Online bookcase version number
     */
    @Query("SELECT bv.bookcaseVersion " +
            "FROM BookcaseVersionEntity bv " +
            "JOIN BookcaseEntity b " +
            "ON b.id = bv.bookcaseId " +
            "WHERE b.bookcaseKey = :bookcaseKey " +
            "AND bv.bookcaseVersionStatus = 'online'")
    String findOnlineBookcaseVersion(String bookcaseKey);

    /**
     * Return the online bookcase version for a given bookcase key
     *
     * @param bookcaseKey - Bookcase key
     * @return String - Online bookcase version number
     */
    @Query("SELECT bv " +
        "FROM BookcaseVersionEntity bv " +
        "JOIN BookcaseEntity b " +
        "ON b.id = bv.bookcaseId " +
        "WHERE b.bookcaseKey = :bookcaseKey " +
        "AND bv.bookcaseVersion < :bookcaseVersion " +
        "AND bv.bookcaseVersionStatus <> 'archived' " +
        "ORDER BY bv.bookcaseVersion desc")
    List<BookcaseVersionEntity> findPreviousBookcaseVersion(String bookcaseKey, String bookcaseVersion);

    @Query("SELECT new com.geaviation.techpubs.models.techlib.dto.OfflineDVDInfoDto(ep.offlineFilename, bv.bookcaseVersion, bv.versionTimestamp) " +
            "FROM BookcaseVersionEntity bv " +
            "JOIN BookcaseEntity b " +
            "ON b.id = bv.bookcaseId " +
            "JOIN EngineProgramEntity ep " +
            "ON ep.bookcaseKey = b.bookcaseKey " +
            "WHERE b.bookcaseKey = :bookcaseKey " +
            "AND bv.bookcaseVersionStatus <> 'archived'")
    List<OfflineDVDInfoDto> findAllOfflineDVDDownloads(String bookcaseKey);

    @Query("SELECT new com.geaviation.techpubs.models.techlib.dto.OfflineDVDInfoDto(ep.offlineFilename, bv.bookcaseVersion, bv.versionTimestamp) " +
            "FROM BookcaseVersionEntity bv " +
            "JOIN BookcaseEntity b " +
            "ON b.id = bv.bookcaseId " +
            "JOIN EngineProgramEntity ep " +
            "ON ep.bookcaseKey = b.bookcaseKey " +
            "WHERE b.bookcaseKey = :bookcaseKey " +
            "AND bv.bookcaseVersionStatus = :bookcaseVersionStatus")
    List<OfflineDVDInfoDto> findOfflineDVDDownloadsByVersionStatus(String bookcaseKey, String bookcaseVersionStatus);

    @Modifying
    @Transactional
    @Query("UPDATE BookcaseVersionEntity " +
            "SET bookcaseVersionStatus = :bookcaseVersionStatus " +
            "WHERE id = :bookcaseVersionId")
    int updateBookcaseVersionStatus(@Param("bookcaseVersionStatus") String bookcaseVersionStatus,
                                    @Param("bookcaseVersionId") UUID bookcaseVersionId);

    @Modifying
    @Transactional
    @Query("UPDATE BookcaseVersionEntity " +
            "SET bookcaseVersionStatus = :bookcaseVersionStatus, versionTimestamp = :bookcaseReleaseDate " +
            "WHERE id = :bookcaseVersionId")
    int updateBookcaseVersionStatusAndReleaseDate(@Param("bookcaseVersionStatus") String bookcaseVersionStatus,
                                                  @Param("bookcaseReleaseDate") Timestamp bookcaseReleaseDate,
                                                  @Param("bookcaseVersionId") UUID bookcaseVersionId);
}
