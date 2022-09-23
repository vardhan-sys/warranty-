package com.geaviation.techpubs.models.techlib.dto;

import com.geaviation.techpubs.models.techlib.UserRoleAttributes;

import java.util.List;

public class AddUserRoleAttributesDto {

    private List<String> ssoIds;
    private List<String> roles;
    private UserRoleAttributes attributes;

    public AddUserRoleAttributesDto() { }

    public AddUserRoleAttributesDto(List<String> ssos, List<String> roles, UserRoleAttributes attributes) {
        this.ssoIds = ssos;
        this.roles = roles;
        this.attributes = attributes;
    }

    public List<String> getSsoIds() { return ssoIds; }

    public void setSsoIds(List<String> ssoIds) { this.ssoIds = ssoIds; }

    public List<String> getRoles() { return roles; }

    public void setRoles(List<String> roles) { this.roles = roles; }

    public UserRoleAttributes getAttributes() { return attributes; }

    public void setAttributes(UserRoleAttributes attributes) { this.attributes = attributes; }

}
