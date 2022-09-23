package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.ArchivalDocumentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IArchivalRepo extends JpaRepository<ArchivalDocumentsEntity, UUID> {

    @Query("SELECT ad " +
            "FROM ArchivalDocumentsEntity ad " +
            "JOIN ad.engineModels em " +
            "JOIN em.companies c " +
            "WHERE c.icaoCode = :icao " +
            "GROUP BY ad")
    List<ArchivalDocumentsEntity> findByIcaoCode(@Param("icao") String icao);

    @Query("SELECT ad " +
            "FROM ArchivalDocumentsEntity ad " +
            "JOIN ad.engineModels em " +
            "JOIN em.companies c " +
            "GROUP BY ad")
    List<ArchivalDocumentsEntity> findAllDocuments();
}
