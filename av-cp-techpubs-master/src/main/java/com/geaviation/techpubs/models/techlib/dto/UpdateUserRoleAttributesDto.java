package com.geaviation.techpubs.models.techlib.dto;

import com.geaviation.techpubs.models.techlib.UserRoleAttributes;

public class UpdateUserRoleAttributesDto {

    private String oldRole;
    private String newRole;
    private UserRoleAttributes attributes;

    public UpdateUserRoleAttributesDto() { }

    public UpdateUserRoleAttributesDto(String oldRole, String newRole, UserRoleAttributes attributes) {
        this.oldRole = oldRole;
        this.newRole = newRole;
        this.attributes = attributes;
    }

    public String getOldRole() { return oldRole; }

    public void setOldRole(String oldRole) { this.oldRole = oldRole; }

    public String getNewRole() { return newRole; }

    public void setNewRole(String newRole) { this.newRole = newRole; }

    public UserRoleAttributes getAttributes() { return attributes; }

    public void setAttributes(UserRoleAttributes attributes) { this.attributes = attributes; }
}
