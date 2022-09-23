package com.geaviation.techpubs.models.techlib;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class SalesforceCompanyAirframeEntitlementEntityPK implements Serializable {

    private UUID salesforceCompanyLookupEntity;

    private UUID airframeLookup;

    private UUID agreementSubtypeLookupEntity;

    public SalesforceCompanyAirframeEntitlementEntityPK() {}

    public SalesforceCompanyAirframeEntitlementEntityPK(UUID salesforceCompanyLookupEntity, UUID airframeLookup, UUID agreementSubtypeLookupEntity) {
        this.salesforceCompanyLookupEntity = salesforceCompanyLookupEntity;
        this.airframeLookup = airframeLookup;
        this.agreementSubtypeLookupEntity = agreementSubtypeLookupEntity;
    }


    public UUID getSalesforceCompanyLookupEntity() {
        return salesforceCompanyLookupEntity;
    }

    public void setSalesforceCompanyLookupEntity(UUID salesforceCompanyLookupEntity) {
        this.salesforceCompanyLookupEntity = salesforceCompanyLookupEntity;
    }

    public UUID getAirframeLookup() {
        return airframeLookup;
    }

    public void setAirframeLookup(UUID airframeLookup) {
        this.airframeLookup = airframeLookup;
    }

    public UUID getAgreementSubtypeLookupEntity() {
        return agreementSubtypeLookupEntity;
    }

    public void setAgreementSubtypeLookupEntity(UUID agreementSubtypeLookupEntity) {
        this.agreementSubtypeLookupEntity = agreementSubtypeLookupEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalesforceCompanyAirframeEntitlementEntityPK that = (SalesforceCompanyAirframeEntitlementEntityPK) o;
        return Objects.equals(salesforceCompanyLookupEntity, that.salesforceCompanyLookupEntity) && Objects.equals(airframeLookup, that.airframeLookup) && Objects.equals(agreementSubtypeLookupEntity, that.agreementSubtypeLookupEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(salesforceCompanyLookupEntity, airframeLookup, agreementSubtypeLookupEntity);
    }
}
