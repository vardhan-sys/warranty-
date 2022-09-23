package com.geaviation.techpubs.models.techlib.dto;

import com.geaviation.techpubs.services.excel.annotation.Excel;
import com.geaviation.techpubs.services.excel.annotation.ExcelColumn;
import com.geaviation.techpubs.services.util.AppConstants;

import java.util.List;

@Excel(fileName = "users_summary", orderMatters = true)
public class UserRolesDto {

    @ExcelColumn(name = AppConstants.SSO_ID, order = 1)
    private String sso;

    @ExcelColumn(name = AppConstants.FIRST_NAME, order = 2)
    private String firstName;

    @ExcelColumn(name = AppConstants.LAST_NAME, order = 3)
    private String lastName;

    @ExcelColumn(name = AppConstants.COMPANY, order = 4)
    private String company;

    @ExcelColumn(name = AppConstants.EMAIL, order = 5)
    private String email;

    @ExcelColumn(name = AppConstants.ROLES, order = 6)
    private List<String> roles;

    public UserRolesDto() { }

    public UserRolesDto(String sso, String firstName, String lastName, String company, String email) {
        this.sso = sso;
        this.firstName = firstName;
        this.lastName = lastName;
        this.company = company;
        this.email = email;
    }

    public UserRolesDto(String sso, String firstName, String lastName, String company, String email, List<String> roles) {
        this.sso = sso;
        this.firstName = firstName;
        this.lastName = lastName;
        this.company = company;
        this.email = email;
        this.roles = roles;
    }

    public String getSso() { return sso; }

    public void setSso(String sso) { this.sso = sso; }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getCompany() { return company; }

    public void setCompany(String company) { this.company = company; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public List<String> getRoles() { return roles; }

    public void setRoles(List<String> roles) { this.roles = roles; }
}
