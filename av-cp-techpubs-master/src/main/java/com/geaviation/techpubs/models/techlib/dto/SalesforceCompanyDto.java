package com.geaviation.techpubs.models.techlib.dto;

import java.util.UUID;

public class SalesforceCompanyDto {

    private UUID id;

    private String companyName;

    public SalesforceCompanyDto() {}

    public SalesforceCompanyDto(UUID id, String companyName) {this.id = id;this.companyName = companyName;}

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public UUID getId() {return id;}

    public void setId(UUID id) {this.id = id;}
}
