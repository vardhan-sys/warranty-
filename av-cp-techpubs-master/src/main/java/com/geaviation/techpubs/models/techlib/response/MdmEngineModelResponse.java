package com.geaviation.techpubs.models.techlib.response;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MdmEngineModelResponse {

    private Map<String, List<String>> mdmEngineModels = new TreeMap<>();

    public Map<String, List<String>> getMdmEngineModels() {
        return mdmEngineModels;
    }

    public void addMdmEngineModels(Map<String, List<String>> mdmEngineModels) {
        this.mdmEngineModels = mdmEngineModels;
    }
}