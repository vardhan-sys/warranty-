package com.geaviation.techpubs.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PortalUserProperty {
    private String name;
    private String value;

    public PortalUserProperty(
            @JsonProperty("propName") String name,
            @JsonProperty("propValue") String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
