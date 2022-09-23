package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.SystemDocumentEntity;
import com.geaviation.techpubs.models.techlib.dto.SystemDocumentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ISystemDocumentData extends JpaRepository<SystemDocumentEntity, UUID> {

    Optional<List<SystemDocumentEntity>> findByDocumentNumber(String documentNumber);

    Optional<List<SystemDocumentEntity>> findByDeleted(Boolean isDeleted);

    @Query("SELECT new com.geaviation.techpubs.models.techlib.dto.SystemDocumentDTO(doc.id, type.value, type.id, doc.documentNumber, site.value, site.id, doc.documentDescription, doc.revision, doc.distributionDate, doc.powerDocument) " +
    "FROM SystemDocumentEntity doc " +
    "JOIN doc.systemDocumentTypeLookupEntity type " +
    "JOIN doc.systemDocumentSiteLookupEntity site " +
    "WHERE doc.deleted = :isDeleted")
    Page<SystemDocumentDTO> findByDeletedPaginated(@Param("isDeleted") Boolean isDeleted, Pageable pageable);

    @Query("SELECT s from SystemDocumentEntity s WHERE s.documentNumber = :documentNumber "
            + "AND s.systemDocumentSiteLookupEntity.id = :documentSiteId "
            + "AND s.systemDocumentTypeLookupEntity.id = :documentTypeId")
    Optional<SystemDocumentEntity> findByDocumentNumberSiteAndType(
            @Param("documentTypeId") UUID documentTypeId,
            @Param("documentNumber") String documentNumber,
            @Param("documentSiteId") UUID documentSiteId);

    @Query("SELECT s from SystemDocumentEntity s "
            + "JOIN s.systemDocumentSiteLookupEntity site "
            + "JOIN s.systemDocumentTypeLookupEntity type "
            + "WHERE s.documentNumber = :documentNumber "
            + "AND site.value = :docSiteValue "
            + "AND type.value = :docTypeValue "
            + "AND s.deleted = :deleted")
    Optional<SystemDocumentEntity> findByDocNumberSiteValueTypeValueAndDeleted(
            @Param("documentNumber") String documentNumber,
            @Param("docSiteValue") String docSiteValue,
            @Param("docTypeValue") String docTypeValue,
            @Param("deleted") Boolean deleted);

    @Query("SELECT new com.geaviation.techpubs.models.techlib.dto.SystemDocumentDTO(doc.id, type.value, type.id, doc.documentNumber, site.value, site.id, doc.documentDescription, doc.revision, doc.distributionDate, doc.powerDocument) " +
            "FROM SystemDocumentEntity doc " +
            "JOIN doc.systemDocumentTypeLookupEntity type " +
            "JOIN doc.systemDocumentSiteLookupEntity site " +
            "WHERE doc.deleted = :isDeleted " +
            "AND type.id = :documentTypeId")
    Page<SystemDocumentDTO> findByDocumentTypeAndDeleted(@Param("documentTypeId") UUID documentTypeId, @Param("isDeleted") Boolean isDeleted, Pageable pageable);

    @Query("SELECT new com.geaviation.techpubs.models.techlib.dto.SystemDocumentDTO(doc.id, type.value, type.id, doc.documentNumber, site.value, site.id, doc.documentDescription, doc.revision, doc.distributionDate, doc.powerDocument) " +
            "FROM SystemDocumentEntity doc " +
            "JOIN doc.systemDocumentTypeLookupEntity type " +
            "JOIN doc.systemDocumentSiteLookupEntity site " +
            "JOIN doc.partNumbersAffectedEntity partNumber " +
            "WHERE doc.deleted = :isDeleted " +
            "AND partNumber.partNumber = :partNumber")
    Page<SystemDocumentDTO> findByPartNumberAndDeleted(@Param("partNumber") String partNumber, @Param("isDeleted") Boolean isDeleted, Pageable pageable);


    @Query("SELECT new com.geaviation.techpubs.models.techlib.dto.SystemDocumentDTO(doc.id, type.value, type.id, doc.documentNumber, site.value, site.id, doc.documentDescription, doc.revision, doc.distributionDate, doc.powerDocument) " +
            "FROM SystemDocumentEntity doc " +
            "JOIN doc.systemDocumentTypeLookupEntity type " +
            "JOIN doc.systemDocumentSiteLookupEntity site " +
            "JOIN doc.partNumbersAffectedEntity partNumber " +
            "WHERE doc.deleted = :isDeleted " +
            "AND partNumber.partNumber = :partNumber " +
            "AND type.id = :documentTypeId")
    Page<SystemDocumentDTO> findByDocumentTypeAndPartNumberAndDeleted(
            @Param("documentTypeId") UUID documentTypeId, @Param("partNumber") String partNumber, @Param("isDeleted") Boolean isDeleted, Pageable pageable);



    @Modifying
    @Transactional
    @Query("UPDATE SystemDocumentEntity " +
            "SET lastEmailSentDate = :lastEmailSentDate " +
            "WHERE id = :systemDocumentId ")
    int updateLastEmailSentDate(@Param("lastEmailSentDate") Timestamp lastEmailSentDate,
                                @Param("systemDocumentId") UUID systemDocumentId);


    @Modifying
    @Transactional
    @Query("UPDATE SystemDocumentEntity " +
            "SET emailNotification = :emailNotification " +
            "WHERE id = :systemDocumentId ")
    int updateEmailNotificationFlag(@Param("emailNotification") Boolean emailNotification,
                                    @Param("systemDocumentId") UUID systemDocumentId);

}
