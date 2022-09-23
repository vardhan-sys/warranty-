package com.geaviation.techpubs.models.techlib;

import java.util.List;

public class UserRoleAttributes {

    private List<String> engineModels;
    private List<String> airFrames;
    private List<String> docTypes;

    public UserRoleAttributes() { }

    public UserRoleAttributes(List<String> engineModels, List<String> airFrames, List<String> docTypes) {
        this.engineModels = engineModels;
        this.airFrames = airFrames;
        this.docTypes = docTypes;
    }

    public List<String> getEngineModels() {
        return engineModels;
    }

    public void setEngineModels(List<String> engineModels) {
        this.engineModels = engineModels;
    }

    public List<String> getAirFrames() {
        return airFrames;
    }

    public void setAirFrames(List<String> airFrames) {
        this.airFrames = airFrames;
    }

    public List<String> getDocTypes() { return docTypes; }

    public void setDocTypes(List<String> docTypes) { this.docTypes = docTypes; }

    @Override
    public String toString() {
        return "UserRoleAttributes{" +
                "engineModels=" + engineModels +
                ", airFrames=" + airFrames +
                ", docTypes=" + docTypes +
                '}';
    }
}
