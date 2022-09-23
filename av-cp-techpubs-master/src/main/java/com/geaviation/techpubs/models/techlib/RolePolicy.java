package com.geaviation.techpubs.models.techlib;

import java.util.List;

public class RolePolicy {

    private List<String> technologyLevels;

    public RolePolicy() { }

    public RolePolicy(List<String> technologyLevels) { this.technologyLevels = technologyLevels; }

    public List<String> getTechnologyLevels() {
        return technologyLevels;
    }

    public void setTechnologyLevels(List<String> technologyLevels) {
        this.technologyLevels = technologyLevels;
    }

    @Override
    public String toString() {
        return "RolePolicy{" +
                "technologyLevels=" + technologyLevels +
                '}';
    }
}
