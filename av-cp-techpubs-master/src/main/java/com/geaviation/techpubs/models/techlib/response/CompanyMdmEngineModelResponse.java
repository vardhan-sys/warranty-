package com.geaviation.techpubs.models.techlib.response;

import java.util.SortedMap;
import java.util.TreeMap;

public class CompanyMdmEngineModelResponse {

    private SortedMap<String, SortedMap<String, Boolean>> companyEngineFamilies = new TreeMap<>();

    /**
     *
     * @return A sorted map who's key is engine family and who's value is a sorted map
     *          with a key of engine model which belong to the given family and a value
     *          of sorted set of engine series which belong to the given engine model
     */
    public SortedMap<String, SortedMap<String, Boolean>> getCompanyMdmEngineModels() {
        return companyEngineFamilies;
    }

    public void addCompanyMdmEngineModels(String family, SortedMap<String, Boolean> companyEngineModels) {
        this.companyEngineFamilies.put(family, companyEngineModels);
    }
}
