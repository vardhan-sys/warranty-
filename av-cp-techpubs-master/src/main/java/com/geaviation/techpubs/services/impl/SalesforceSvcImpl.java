package com.geaviation.techpubs.services.impl;

import com.geaviation.techpubs.controllers.requests.EnableStatusBody;
import com.geaviation.techpubs.controllers.requests.SortBy;
import com.geaviation.techpubs.data.api.techlib.IAirframeLookupData;
import com.geaviation.techpubs.data.api.techlib.ISalesforceCompanyAirframeEntitlementData;
import com.geaviation.techpubs.data.api.techlib.ISalesforceCompanyData;
import com.geaviation.techpubs.data.api.techlib.ISystemDocumentTypeLookupData;
import com.geaviation.techpubs.data.mapper.EntitlementMapper;
import com.geaviation.techpubs.data.util.PageableUtil;
import com.geaviation.techpubs.exceptions.TechpubsException;
import com.geaviation.techpubs.models.techlib.AirframeLookupEntity;
import com.geaviation.techpubs.models.techlib.SalesforceCompanyLookupEntity;
import com.geaviation.techpubs.models.techlib.SystemDocumentTypeLookupEntity;
import com.geaviation.techpubs.models.techlib.dto.AirframeDto;
import com.geaviation.techpubs.models.techlib.dto.SalesforceCompanyAirframeEntitlementDto;
import com.geaviation.techpubs.models.techlib.dto.SalesforceCompanyDto;
import com.geaviation.techpubs.models.techlib.response.EntitlementResponse;
import com.geaviation.techpubs.services.api.ISalesforceSvc;
import com.geaviation.techpubs.services.util.admin.AdminAppUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SalesforceSvcImpl implements ISalesforceSvc {

    private static final Logger log = LogManager.getLogger(SalesforceSvcImpl.class);

    private static final String cotsAirframeName = "COTS";

    @Autowired
    private ISalesforceCompanyData iSalesforceCompanyData;

    @Autowired
    private IAirframeLookupData iAirframeLookupData;

    @Autowired
    private ISalesforceCompanyAirframeEntitlementData entitlementData;

    @Autowired
    private ISystemDocumentTypeLookupData iSystemDocumentTypeLookupData;

    @Autowired
    private AdminAppUtil adminAppUtil;

    public List<AirframeDto> getEntitledAirframes(String icaoCode) {
        if (icaoCode.equals("GEAE"))
        {
            return iAirframeLookupData.getAllAirframes();
        } else {
            List<AirframeDto> entitledAirframes = new ArrayList<>();
            entitledAirframes = iAirframeLookupData.getAllEntitledAirframes(icaoCode);
            if (entitledAirframes.size() > 0) {
                AirframeDto cotsFilter = iAirframeLookupData.getCotsAirFrames();
                entitledAirframes.add(cotsFilter);
            }
            return entitledAirframes;
        }
    }

	@Override
	public List<SalesforceCompanyDto> getSalesforceCompanies(List<String> airframes, List<String> entitlementType) throws TechpubsException {
		List<UUID> airframesList = new ArrayList<>();
		if (airframes != null && !airframes.isEmpty()) {
			for (String airframe : airframes) {
				airframesList.add(UUID.fromString(airframe));
			}
		}

        // Any company can have a customer-specific document added for COTS
        // There aren't entitlements in Salesforce for COTS because it isn't a real airframe
        Optional<AirframeLookupEntity> cotsAirframeOptional = iAirframeLookupData.findByAirframe(cotsAirframeName);
        if (!cotsAirframeOptional.isPresent()) {
            log.error("Unable to find COTS airframe");
            throw new TechpubsException(TechpubsException.TechpubsAppError.INTERNAL_ERROR);
        }
        AirframeLookupEntity cotsAirframe = cotsAirframeOptional.get();

		if (airframesList == null || airframesList.isEmpty()) {
			return iSalesforceCompanyData.getAllSalesforceCompanies();
		} else if (airframesList != null && !airframesList.isEmpty() && airframesList.contains(cotsAirframe.getId())) {
            // return all companies
            return iSalesforceCompanyData.getAllSalesforceCompanies();
        } else if ((airframesList != null && !airframesList.isEmpty())
				&& (entitlementType != null && !entitlementType.isEmpty())) {
			return iSalesforceCompanyData.getCustomerSpecificSalesforceCompaniesByPublicationAceess(airframesList,
					entitlementType, "Active");
		} else {
			return iSalesforceCompanyData.getCustomerSpecificSalesforceCompanies(airframesList, "Active");
		}
	}

    @Override
    public List<AirframeDto> getSalesforceAirframes(){
        return iAirframeLookupData.getAllAirframes();
    }

    /**
     * Get a list of Avsystem entitlements
     *
     * @param icaoCode Owner of the entitlements
     * @param page Page number of database records to retrieve
     * @param size Number of records to retrieve
     * @param sortBy The column and direction to sort by, e.g. airframe|asc
     * @return List of entitlements for a company
     */
    @Override
    public Page<SalesforceCompanyAirframeEntitlementDto> getPaginatedEntitlements(String icaoCode, int page, int size, SortBy sortBy) {

        String mappedField = EntitlementMapper.sortMapper(sortBy.field());
        String direction = sortBy.direction();

        Pageable pageable = PageableUtil.create(page, size, mappedField, direction);

        return entitlementData.getPaginatedEntitlementsByIcaoCode(icaoCode, pageable);
    }

    /**
     * Get a list of Avsystem entitlements
     *
     * @param icaoCode Owner of the entitlements
     * @return List of entitlements for a company
     */
    @Override
    public EntitlementResponse getEntitlements(String icaoCode) {
        EntitlementResponse entitlementResponse = new EntitlementResponse();
        List<SalesforceCompanyAirframeEntitlementDto> entitlements = new ArrayList<>();

        if (icaoCode.equals("GEAE")) {
            entitlementResponse.setUserType("INTERNAL");
            entitlements = entitlementData.getAllEntitlements();
        } else {
            entitlementResponse.setUserType("CUSTOMER");
            entitlements = entitlementData.getEntitlementsByIcaoCode(icaoCode);
        }

        entitlements.forEach(entitlement -> {
            List<String> associatedDocumentTypes = iSystemDocumentTypeLookupData.getSystemDocumentTypes(entitlement.getPublicationAccessLevelId());
            entitlement.setAssociatedDocumentTypes(associatedDocumentTypes);
        });

        entitlementResponse.setEntitlementList(entitlements);

        return entitlementResponse;
    }

    /**
     * Update the enable flag for the provided salesforce id
    */
	@Override
	public void updateEnableStatus(EnableStatusBody body) {
		List<UUID> companyIds = new ArrayList<>();
		if (body.getCompanyIds() != null && !body.getCompanyIds().isEmpty()) {
			for (String companyId : body.getCompanyIds()) {
				companyIds.add(UUID.fromString(companyId));
			}
		}
		List<SalesforceCompanyLookupEntity> companyEntity = iSalesforceCompanyData.findByIdIn(companyIds);
		if (companyEntity != null && !companyEntity.isEmpty()) {
			companyEntity.forEach(companyData -> {
				companyData.setEnabled(body.isEnabled());
				companyData.setLastUpdateDate(LocalDate.now());
			});
			iSalesforceCompanyData.saveAll(companyEntity);
		}
	}

    @Override
    public boolean entitlementExistsForAirframeandDocType(String icaoCode, String docTypeValue,
                  Set<AirframeLookupEntity> airframes) throws TechpubsException {
        Optional<SystemDocumentTypeLookupEntity> systemDocumentTypeLookupEntityOptional = iSystemDocumentTypeLookupData
                .findByValue(docTypeValue);
        if (!systemDocumentTypeLookupEntityOptional.isPresent()) {
            log.error("System document type not found: " + docTypeValue);
            throw new TechpubsException(TechpubsException.TechpubsAppError.INVALID_PARAMETER);
        }
        SystemDocumentTypeLookupEntity systemDocumentTypeLookupEntity = systemDocumentTypeLookupEntityOptional.get();
        Set<UUID> airframeUUIDs = airframes.stream().map(AirframeLookupEntity::getId).collect(Collectors.toSet());
        return entitlementData.entitlementExistsForAirframeAndDocType(icaoCode, systemDocumentTypeLookupEntity.getId(),
                airframeUUIDs);
    }

}
