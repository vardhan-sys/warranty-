package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.ArchivalCompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IArchivalCompanyRepo extends JpaRepository<ArchivalCompanyEntity, UUID> {

    @Query("SELECT ac FROM ArchivalCompanyEntity ac WHERE ac.icaoCode = :icao")
    ArchivalCompanyEntity findByIcaoCode(@Param("icao") String icao);
}
