package com.geaviation.techpubs.data.mapper;

public class EntitlementMapper {

    private EntitlementMapper() {
    }

    public static String sortMapper(String field) {
        String sortBy;

        switch (field) {
            case "agreement":
                sortBy = "agreementSubtypeLookupEntity.value";
                break;
            case "airframe":
                sortBy = "airframeLookup.airframe";
                break;
            case "endDate":
                sortBy = "endDate";
                break;
            case "startDate":
                sortBy = "startDate";
                break;
            case "entitlementStatus":
                sortBy = "entitlementStatus";
                break;
            case "publicationAccessLevel":
                sortBy = "publicationAccessLevelLookupEntity.value";
                break;
            default:
                sortBy = "";
        }

        return sortBy;
    }
}
