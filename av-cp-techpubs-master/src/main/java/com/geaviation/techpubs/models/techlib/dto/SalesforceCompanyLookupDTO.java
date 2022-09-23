package com.geaviation.techpubs.models.techlib.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.UUID;

public class SalesforceCompanyLookupDTO {

    private UUID id;
    private String salesforceId;
    private String companyName;
    private String icaoCode;
    private String dunsNumber;
    private boolean enabled;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate lastUpdateDate;

    public SalesforceCompanyLookupDTO() {
    }

    public SalesforceCompanyLookupDTO(UUID id, String salesforceId, String companyName, String icaoCode, String dunsNumber, boolean enabled, LocalDate lastUpdateDate) {
        this.id = id;
        this.salesforceId = salesforceId;
        this.companyName = companyName;
        this.icaoCode = icaoCode;
        this.dunsNumber = dunsNumber;
        this.enabled = enabled;
        this.lastUpdateDate = lastUpdateDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSalesforceId() {
        return salesforceId;
    }

    public void setSalesforceId(String salesforceId) {
        this.salesforceId = salesforceId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getIcaoCode() {
        return icaoCode;
    }

    public void setIcaoCode(String icaoCode) {
        this.icaoCode = icaoCode;
    }

    public String getDunsNumber() {
        return dunsNumber;
    }

    public void setDunsNumber(String dunsNumber) {
        this.dunsNumber = dunsNumber;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDate getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(LocalDate lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

}



