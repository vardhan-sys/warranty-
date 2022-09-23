package com.geaviation.techpubs.data.api.techlib;

import com.geaviation.techpubs.models.techlib.SalesforceCompanyLookupEntity;
import com.geaviation.techpubs.models.techlib.dto.SalesforceCompanyDto;
import com.geaviation.techpubs.models.techlib.dto.SalesforceCompanyLookupDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ISalesforceCompanyData extends JpaRepository<SalesforceCompanyLookupEntity, UUID> {

    @Query("SELECT new com.geaviation.techpubs.models.techlib.dto.SalesforceCompanyDto(company.id ,company.companyName) " +
            "FROM SalesforceCompanyLookupEntity company where company.enabled= true")
    List<SalesforceCompanyDto> getAllSalesforceCompanies();
    
    @Query("SELECT new com.geaviation.techpubs.models.techlib.dto.SalesforceCompanyDto(company.id ,company.companyName) " +
            "FROM SalesforceCompanyLookupEntity company "
            +"join  SalesforceCompanyAirframeEntitlementEntity entitle ON company.id= entitle.salesforceCompanyLookupEntity  "
            +"join  PublicationAccessLevelLookupEntity pubacess ON pubacess.id= entitle.publicationAccessLevelLookupEntity  "
    	    + "WHERE entitle.airframeLookup.id IN (:airframe) "
    	    + "And pubacess.value in (:entitlementType) "
    	    + "And entitle.entitlementStatus = :entitlementStatus " 
    	    + "And company.enabled = true " 
    	    + "group by company order by company.companyName asc "
    		)
    List<SalesforceCompanyDto> getCustomerSpecificSalesforceCompaniesByPublicationAceess(List<UUID> airframe, List<String> entitlementType, String entitlementStatus);

    @Query("SELECT new com.geaviation.techpubs.models.techlib.dto.SalesforceCompanyDto(company.id ,company.companyName) " +
            "FROM SalesforceCompanyLookupEntity company "
            +"join  SalesforceCompanyAirframeEntitlementEntity entitle ON company.id= entitle.salesforceCompanyLookupEntity  "
    	    + "WHERE entitle.airframeLookup.id IN (:airframe)  "
    	    + "And entitle.entitlementStatus = :entitlementStatus " 
    	    + "And company.enabled = true "
    	    + " group by company order by company.companyName asc "
    		)
    List<SalesforceCompanyDto> getCustomerSpecificSalesforceCompanies(List<UUID> airframe, String entitlementStatus);

    SalesforceCompanyLookupEntity findBySalesforceId(String salesforceId);

	@Query("SELECT new com.geaviation.techpubs.models.techlib.dto.SalesforceCompanyLookupDTO(company.id ,company.salesforceId ,company.companyName ,company.icaoCode ,company.dunsNumber ,company.enabled ,company.lastUpdateDate) " + "FROM SalesforceCompanyLookupEntity company WHERE company.icaoCode = :icaoCode ")
	List<SalesforceCompanyLookupDTO> getSalesforceCompanyLookup(@Param("icaoCode") String icaoCode);
	
    List<SalesforceCompanyLookupEntity> findByIdIn(List<UUID> ids);

}
