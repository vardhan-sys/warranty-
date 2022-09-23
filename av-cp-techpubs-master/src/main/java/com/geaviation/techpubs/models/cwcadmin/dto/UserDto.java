package com.geaviation.techpubs.models.cwcadmin.dto;

public class UserDto {

    private String ssoId;
    private String firstName;
    private String lastName;
    private String company;
    private String email;

    public UserDto() { }

    public UserDto(String ssoId, String firstName, String lastName, String company, String email) {
        this.ssoId = ssoId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.company = company;
        this.email = email;
    }

    public String getSsoId() { return ssoId; }

    public void setSsoId(String ssoId) { this.ssoId = ssoId; }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getCompany() { return company; }

    public void setCompany(String company) { this.company = company; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }
}
