package com.geaviation.techpubs.data.mapper;

public class SystemDocumentMapper {

    private SystemDocumentMapper() {
    }

    public static String sortMapper(String field) {
        String sortBy;

        switch (field) {
            case "documentType":
                sortBy = "systemDocumentTypeLookupEntity.value";
                break;
            case "documentNumber":
                sortBy = "documentNumber";
                break;
            case "documentSite":
                sortBy = "systemDocumentSiteLookupEntity.value";
                break;
            case "documentTitle":
                sortBy = "documentDescription";
                break;
            case "documentRevision":
                sortBy = "revision";
                break;
            case "documentDistributionDate":
                sortBy = "distributionDate";
                break;
            default:
                sortBy = "";
        }

        return sortBy;
    }
}
