package com.geaviation.techpubs.data.api.techlib.enginedoc;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import com.geaviation.techpubs.models.techlib.enginedoc.EngineDocumentEntity;

@Repository
public interface IEngineDocumentData extends JpaRepository<EngineDocumentEntity, UUID> {

    Optional<List<EngineDocumentEntity>> findByEngineDocumentTypeLookupEntityValue(String value);

    @Query("SELECT DISTINCT doc FROM EngineDocumentEntity doc "
    + "JOIN doc.engineDocumentTypeLookupEntity type "
    + "JOIN doc.engineModelEntity model "
    + "WHERE type.value = :value "
    + "AND model.model in :models "
    + "AND doc.deleted = :deleted")
    Optional<List<EngineDocumentEntity>> findByDocumentTypeAndEngineModelAndDeleted(String value, List<String> models, Boolean deleted);

    Page<EngineDocumentEntity> findByDeleted(Boolean deleted, Pageable pageable);
    
    Optional<List<EngineDocumentEntity>> findByDeleted(Boolean isDeleted);
    
    @Query("SELECT doc FROM EngineDocumentEntity doc " 
    + "JOIN doc.engineDocumentTypeLookupEntity type "
	+ "JOIN doc.engineModelEntity model " 
    + "WHERE (:value is null or type.value = :value) "
    + "AND ((:models) is null or model.model in (:models)) "
    + "AND (:searchTerm is null or LOWER (doc.documentTitle) like %:searchTerm% or LOWER (model.model) like %:searchTerm%) "
    + "AND doc.deleted = :deleted "
	+ "GROUP BY doc, type")
	Page<EngineDocumentEntity> findByDocumentTypeAndEngineModelAndDeletedPaginated(String value, List<String> models, String searchTerm,
			Boolean deleted, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE EngineDocumentEntity " +
            "SET lastEmailSentDate = :lastEmailSentDate " +
            "WHERE documentTitle = :documentTitle " +
            "AND partName = :partName " +
            "AND fileName = :fileName" )
    int updateLastEmailSentDate(@Param("lastEmailSentDate") Timestamp lastEmailSentDate,
                                @Param("documentTitle") String documentTitle,
                                @Param("partName") String partName,
                                @Param("fileName") String fileName);

    @Modifying
    @Transactional
    @Query("UPDATE EngineDocumentEntity " +
            "SET emailNotification = :emailNotification " +
            "WHERE documentTitle = :documentTitle " +
            "AND partName = :partName " +
            "AND fileName = :fileName" )
    int updateEmailNotificationFlag(@Param("emailNotification") Boolean emailNotification,
                                    @Param("documentTitle") String documentTitle,
                                    @Param("partName") String partName,
                                    @Param("fileName") String fileName);
    
    Optional<EngineDocumentEntity> findByIdAndEngineDocumentTypeLookupEntityValue(
		String documentType,
		String documentId);
 
}
