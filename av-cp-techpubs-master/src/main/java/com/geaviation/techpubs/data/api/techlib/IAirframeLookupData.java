package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.AirframeLookupEntity;
import com.geaviation.techpubs.models.techlib.dto.AirframeDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IAirframeLookupData extends JpaRepository<AirframeLookupEntity, UUID> {

    @Query("SELECT new com.geaviation.techpubs.models.techlib.dto.AirframeDto(airframe.id, airframe.airframe) " +
            "FROM AirframeLookupEntity airframe")
    List<AirframeDto> getAllAirframes();

    Optional<AirframeLookupEntity> findByAirframe(String airframe);

    @Query("SELECT new com.geaviation.techpubs.models.techlib.dto.AirframeDto(al.id, al.airframe) "+
            "FROM SalesforceCompanyAirframeEntitlementEntity scae "+
            "JOIN scae.airframeLookup al "+
            "JOIN scae.salesforceCompanyLookupEntity scle "+
            "WHERE scle.icaoCode = :icaoCode "+
            "AND scae.entitlementStatus = 'Active' "+
            "AND scle.enabled = true ")
    List<AirframeDto> getAllEntitledAirframes(@Param("icaoCode") String icaoCode);

    @Query("SELECT new com.geaviation.techpubs.models.techlib.dto.AirframeDto(al.id, al.airframe) "+
            "FROM AirframeLookupEntity al "+
            "WHERE al.airframe = 'COTS' ")
    AirframeDto getCotsAirFrames();
}
