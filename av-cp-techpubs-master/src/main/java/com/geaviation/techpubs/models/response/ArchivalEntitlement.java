package com.geaviation.techpubs.models.response;

import java.util.List;

public class ArchivalEntitlement {
    private String company;
    private List<ArchivalDocument> documents;

    public ArchivalEntitlement() {
    }

    public ArchivalEntitlement(String company) {
        this.company = company;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public List<ArchivalDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<ArchivalDocument> documents) {
        this.documents = documents;
    }
}
