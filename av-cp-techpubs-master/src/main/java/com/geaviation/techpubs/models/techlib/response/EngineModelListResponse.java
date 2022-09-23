package com.geaviation.techpubs.models.techlib.response;

import java.util.List;

public class EngineModelListResponse {

    private List<String> engineModels;

    public EngineModelListResponse() { }

    public EngineModelListResponse(List<String> engineModels) {
        this.engineModels = engineModels;
    }

    public List<String> getEngineModels() { return engineModels; }

    public void setEngineModels(List<String> engineModels) { this.engineModels = engineModels; }
}
