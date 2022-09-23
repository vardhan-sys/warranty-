package com.geaviation.techpubs.models.response;

public class EngineModel {
    private String family;
    private String model;

    public EngineModel() {
    }

    public EngineModel(String family, String model) {
        this.family = family;
        this.model = model;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
