package com.geaviation.techpubs.models.techlib.dto;

import java.util.List;

public class TechLevelEngineResponse {

    private String boocaseKey;

    private List<TechLevelDto> techLevels;

    public TechLevelEngineResponse() { }


    public List<TechLevelDto> getTechLevels() {
        return techLevels;
    }

    public void setTechLevels(List<TechLevelDto> techLevels) {
        this.techLevels = techLevels;
    }

    public String getBoocaseKey() {
        return boocaseKey;
    }

    public void setBoocaseKey(String boocaseKey) {
        this.boocaseKey = boocaseKey;
    }
}
