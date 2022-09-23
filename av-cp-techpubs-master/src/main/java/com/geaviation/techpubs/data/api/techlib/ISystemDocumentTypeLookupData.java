package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.SystemDocumentTypeLookupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ISystemDocumentTypeLookupData extends JpaRepository<SystemDocumentTypeLookupEntity, UUID> {

    @Query("SELECT sdtl.value " +
            "FROM SystemDocumentTypeLookupEntity sdtl " +
            "JOIN sdtl.publicationAccessLevelDocumentTypeEntity paldt " +
            "JOIN paldt.publicationAccessLevelLookupEntity pale " +
            "WHERE pale.id = :publicationAccessLevelId")
    List<String> getSystemDocumentTypes(@Param("publicationAccessLevelId") UUID publicationAccessLevelId);

    Optional<SystemDocumentTypeLookupEntity> findByValue(String value);

}
