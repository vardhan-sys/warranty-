package com.geaviation.techpubs.models.techlib.dto;

import java.util.UUID;

public class TechLevelDto {

    private UUID id;

    private String levelName;

    private Boolean previouslyEnabled;

    public TechLevelDto() { }

    public TechLevelDto(UUID id, String levelName, Boolean previouslyEnabled) {
        this.id = id;
        this.levelName = levelName;
        this.previouslyEnabled = previouslyEnabled;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public Boolean getPreviouslyEnabled() {
        return previouslyEnabled;
    }

    public void setPreviouslyEnabled(Boolean previouslyEnabled) {
        this.previouslyEnabled = previouslyEnabled;
    }
}
