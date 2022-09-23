package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.SalesforceCompanyAirframeEntitlementEntity;
import com.geaviation.techpubs.models.techlib.dto.SalesforceCompanyAirframeEntitlementDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ISalesforceCompanyAirframeEntitlementData extends JpaRepository<SalesforceCompanyAirframeEntitlementEntity, UUID> {

    @Query("SELECT new com.geaviation.techpubs.models.techlib.dto.SalesforceCompanyAirframeEntitlementDto(al.airframe, " +
            "pale.value, pale.id, scae.entitlementStatus, scae.startDate, scae.endDate) " +
            "FROM SalesforceCompanyAirframeEntitlementEntity scae " +
            "JOIN scae.airframeLookup al " +
            "JOIN scae.salesforceCompanyLookupEntity scle " +
            "JOIN scae.publicationAccessLevelLookupEntity pale " +
            "WHERE scle.icaoCode = :icaoCode")
    Page<SalesforceCompanyAirframeEntitlementDto> getPaginatedEntitlementsByIcaoCode(@Param("icaoCode") String icaoCode, Pageable pageable);

    @Query("SELECT new com.geaviation.techpubs.models.techlib.dto.SalesforceCompanyAirframeEntitlementDto(al.airframe, " +
            "pale.value, pale.id, scae.entitlementStatus, scae.startDate, scae.endDate) " +
            "FROM SalesforceCompanyAirframeEntitlementEntity scae " +
            "JOIN scae.airframeLookup al " +
            "JOIN scae.salesforceCompanyLookupEntity scle " +
            "JOIN scae.publicationAccessLevelLookupEntity pale " +
            "WHERE scle.icaoCode = :icaoCode")
    List<SalesforceCompanyAirframeEntitlementDto> getEntitlementsByIcaoCode(@Param("icaoCode") String icaoCode);

    @Query("SELECT new com.geaviation.techpubs.models.techlib.dto.SalesforceCompanyAirframeEntitlementDto(al.airframe, " +
            "pale.value, pale.id, scae.entitlementStatus, scae.startDate, scae.endDate) " +
            "FROM SalesforceCompanyAirframeEntitlementEntity scae " +
            "JOIN scae.airframeLookup al " +
            "JOIN scae.salesforceCompanyLookupEntity scle " +
            "JOIN scae.publicationAccessLevelLookupEntity pale")
    List<SalesforceCompanyAirframeEntitlementDto> getAllEntitlements();

    @Query("SELECT COUNT(scae) > 0  from SalesforceCompanyAirframeEntitlementEntity scae " +
            "JOIN scae.airframeLookup al " +
            "JOIN scae.salesforceCompanyLookupEntity scle " +
            "JOIN scae.publicationAccessLevelLookupEntity pale " +
            "JOIN pale.publicationAccessLevelDocumentTypeEntities paldte " +
            "WHERE scle.icaoCode = :icaoCode " +
            "AND al.id in :airframeIds " +
            "AND paldte.systemDocumentTypeId = :docTypeId")
    boolean entitlementExistsForAirframeAndDocType(@Param("icaoCode") String icaoCode,
                                                   @Param("docTypeId") UUID docTypeId,
                                                   @Param("airframeIds") Set<UUID> airframeIds);


}
