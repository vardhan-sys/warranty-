package com.geaviation.techpubs.data.api.cwcadmin;

import com.geaviation.techpubs.models.cwcadmin.CwcadminOrgEntity;
import com.geaviation.techpubs.models.cwcadmin.dto.CompanyListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICwcadminOrgData extends PagingAndSortingRepository<CwcadminOrgEntity, Integer> {

    @Query("SELECT NEW com.geaviation.techpubs.models.cwcadmin.dto.CompanyListDto(org.company, org.icaoCode, org.dunsNum) " +
            "FROM CwcadminOrgEntity org " +
            "LEFT JOIN CwcadminPrtyAtrbtValEntity atrbt ON atrbt.prtyId = org.orgId " +
            "LEFT JOIN CwcadminPrtlEntity prtl ON atrbt.prtlId = prtl.portalId " +
            "WHERE prtl.portalName IN ('myGEAviation', 'myCFM', 'GEHonda', 'myEA') " +
            "GROUP BY org.orgId"
    )
    Page<CompanyListDto> findPortalAdminCompanies(Pageable pageRequest);

    @Query("SELECT NEW com.geaviation.techpubs.models.cwcadmin.dto.CompanyListDto(org.company, org.icaoCode, org.dunsNum) " +
            "FROM CwcadminOrgEntity org " +
            "LEFT JOIN CwcadminPrtyAtrbtValEntity atrbt ON atrbt.prtyId = org.orgId " +
            "LEFT JOIN CwcadminPrtlEntity prtl ON atrbt.prtlId = prtl.portalId " +
            "WHERE prtl.portalName IN ('myGEAviation', 'myCFM', 'GEHonda', 'myEA') " +
            "AND (org.company LIKE :companyTerm OR org.icaoCode LIKE :icaoCodeTerm OR org.dunsNum LIKE :dunsNumTerm) " +
            "GROUP BY org.orgId"
    )
    Page<CompanyListDto> searchPortalAdminCompanies(@Param("companyTerm") String companyTerm, @Param("icaoCodeTerm") String icaoCodeTerm,
                                                    @Param("dunsNumTerm") String dunsNumTerm, Pageable pageRequest);

    @Query("SELECT NEW com.geaviation.techpubs.models.cwcadmin.dto.CompanyListDto(org.company, org.icaoCode, org.dunsNum) " +
            "FROM CwcadminOrgEntity org " +
            "LEFT JOIN CwcadminPrtyAtrbtValEntity atrbt ON atrbt.prtyId = org.orgId " +
            "LEFT JOIN CwcadminPrtlEntity prtl ON atrbt.prtlId = prtl.portalId " +
            "WHERE prtl.portalName IN ('myGEAviation', 'myCFM', 'GEHonda', 'myEA') " +
            "GROUP BY org.orgId"
    )
    List<CompanyListDto> findPortalAdminCompanies();
}
