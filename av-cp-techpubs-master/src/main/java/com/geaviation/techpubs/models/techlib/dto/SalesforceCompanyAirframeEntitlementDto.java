package com.geaviation.techpubs.models.techlib.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class SalesforceCompanyAirframeEntitlementDto {
    private String airframe;
    private String publicationAccessLevel;
    private UUID publicationAccessLevelId;
    private List<String> associatedDocumentTypes;
    private String entitlementStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    public SalesforceCompanyAirframeEntitlementDto() {
    }

    public SalesforceCompanyAirframeEntitlementDto(String airframe, String publicationAccessLevel, UUID publicationAccessLevelId, String entitlementStatus, LocalDate startDate, LocalDate endDate) {
        this.airframe = airframe;
        this.publicationAccessLevel = publicationAccessLevel;
        this.publicationAccessLevelId = publicationAccessLevelId;
        this.entitlementStatus = entitlementStatus;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getAirframe() {
        return airframe;
    }

    public void setAirframe(String airframe) {
        this.airframe = airframe;
    }

    public String getPublicationAccessLevel() {
        return publicationAccessLevel;
    }

    public void setPublicationAccessLevel(String publicationAccessLevel) {
        this.publicationAccessLevel = publicationAccessLevel;
    }

    public UUID getPublicationAccessLevelId() {
        return publicationAccessLevelId;
    }

    public void setPublicationAccessLevelId(UUID publicationAccessLevelId) {
        this.publicationAccessLevelId = publicationAccessLevelId;
    }

    public List<String> getAssociatedDocumentTypes() {
        return associatedDocumentTypes;
    }

    public void setAssociatedDocumentTypes(List<String> associatedDocumentTypes) {
        this.associatedDocumentTypes = associatedDocumentTypes;
    }

    public String getEntitlementStatus() {
        return entitlementStatus;
    }

    public void setEntitlementStatus(String entitlementStatus) {
        this.entitlementStatus = entitlementStatus;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
