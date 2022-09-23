package com.geaviation.techpubs.models.techlib.response;

import com.geaviation.techpubs.models.techlib.dto.SalesforceCompanyAirframeEntitlementDto;

import java.util.List;

public class EntitlementResponse {
    private String userType;
    private List<SalesforceCompanyAirframeEntitlementDto> entitlementList;

    public EntitlementResponse() {
    }

    public EntitlementResponse(String userType, List<SalesforceCompanyAirframeEntitlementDto> entitlementList) {
        this.userType = userType;
        this.entitlementList = entitlementList;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public List<SalesforceCompanyAirframeEntitlementDto> getEntitlementList() {
        return entitlementList;
    }

    public void setEntitlementList(List<SalesforceCompanyAirframeEntitlementDto> entitlementList) {
        this.entitlementList = entitlementList;
    }
}
