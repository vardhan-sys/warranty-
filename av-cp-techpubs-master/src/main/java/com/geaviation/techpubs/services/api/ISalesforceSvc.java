package com.geaviation.techpubs.services.api;

import com.geaviation.techpubs.controllers.requests.EnableStatusBody;
import com.geaviation.techpubs.controllers.requests.SortBy;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.techlib.AirframeLookupEntity;
import com.geaviation.techpubs.models.techlib.dto.AirframeDto;
import com.geaviation.techpubs.models.techlib.dto.SalesforceCompanyAirframeEntitlementDto;
import com.geaviation.techpubs.models.techlib.dto.SalesforceCompanyDto;
import com.geaviation.techpubs.models.techlib.response.EntitlementResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

public interface ISalesforceSvc {
    List<SalesforceCompanyDto> getSalesforceCompanies(List<String> airframes, List<String> entitlementType) throws TechpubsException;

    List<AirframeDto> getSalesforceAirframes();

    Page<SalesforceCompanyAirframeEntitlementDto> getPaginatedEntitlements(String icaoCode, int page,
                                                                           int size, SortBy sortBy);

    EntitlementResponse getEntitlements(String icaoCode);

    void updateEnableStatus(EnableStatusBody body);

    boolean entitlementExistsForAirframeandDocType(String icaoCode, String docTypeValue,
                                                   Set<AirframeLookupEntity> airframes) throws TechpubsException;
}
