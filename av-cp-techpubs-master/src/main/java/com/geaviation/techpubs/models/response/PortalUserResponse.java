package com.geaviation.techpubs.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PortalUserResponse {
    private String id;
    private String sso;
    private String firstName;
    private String middleName;
    private String lastName;
    private String emailAddress;
    private String secEmailAddress;
    private String address;
    private List<PortalUserProperty> userProperties;
    private List<PortalUserProperty> orgProperties;

    public PortalUserResponse(
            @JsonProperty("userID") String id,
            @JsonProperty("userName") String sso,
            @JsonProperty("givenName") String firstName,
            @JsonProperty("middleName") String middleName,
            @JsonProperty("familyName") String lastName,
            @JsonProperty("emailId") String emailAddress,
            @JsonProperty("secEmailId") String secEmailAddress,
            @JsonProperty("address") String address,
            @JsonProperty("userProperty") List<PortalUserProperty> userProperties,
            @JsonProperty("orgProperty") List<PortalUserProperty> orgProperties) {
        this.id = id;
        this.sso = sso;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.secEmailAddress = secEmailAddress;
        this.address = address;
        this.userProperties = userProperties;
        this.orgProperties = orgProperties;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSso() {
        return sso;
    }

    public void setSso(String sso) {
        this.sso = sso;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getSecEmailAddress() {
        return secEmailAddress;
    }

    public void setSecEmailAddress(String secEmailAddress) {
        this.secEmailAddress = secEmailAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<PortalUserProperty> getUserProperties() {
        return userProperties;
    }

    public void setUserProperties(List<PortalUserProperty> userProperties) {
        this.userProperties = userProperties;
    }

    public List<PortalUserProperty> getOrgProperties() {
        return orgProperties;
    }

    public void setOrgProperties(List<PortalUserProperty> orgProperties) {
        this.orgProperties = orgProperties;
    }
}
