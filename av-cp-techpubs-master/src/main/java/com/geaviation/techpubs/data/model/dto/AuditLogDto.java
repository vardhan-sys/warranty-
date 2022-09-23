package com.geaviation.techpubs.data.model.dto;

import java.util.Map;

public class AuditLogDto {

    private String fromDate;
    private String toDate;
    private String category;
    private String categorySearchTerm;
    private String action;
    private String ssoSearchTerm;
    private Map<String, String> lastEvaluatedKey;

    public AuditLogDto() { }

    public AuditLogDto(String fromDate, String toDate, String category, String categorySearchTerm, String action, String ssoSearchTerm, Map<String, String> lastEvaluatedKey) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.category = category;
        this.categorySearchTerm = categorySearchTerm;
        this.action = action;
        this.ssoSearchTerm = ssoSearchTerm;
        this.lastEvaluatedKey = lastEvaluatedKey;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategorySearchTerm() { return categorySearchTerm; }

    public void setCategorySearchTerm(String categorySearchTerm) { this.categorySearchTerm = categorySearchTerm; }

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public String getSsoSearchTerm() { return ssoSearchTerm; }

    public void setSsoSearchTerm(String ssoSearchTerm) { this.ssoSearchTerm = ssoSearchTerm; }

    public Map<String, String> getLastEvaluatedKey() {
        return lastEvaluatedKey;
    }

    public void setLastEvaluatedKey(Map<String, String> lastEvaluatedKey) {
        this.lastEvaluatedKey = lastEvaluatedKey;
    }
}
