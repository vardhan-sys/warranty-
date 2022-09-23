package com.geaviation.techpubs.models.techlib.dto;

public class UserRoleAttributesDto {

    private String role;
    private Object attributes;

    public UserRoleAttributesDto() { }

    public UserRoleAttributesDto(String role, Object attributes) {
        this.role = role;
        this.attributes = attributes;
    }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }

    public Object getAttributes() { return attributes; }

    public void setAttributes(Object attributes) { this.attributes = attributes; }

    @Override
    public String toString() {
        return "UserRoleAttributesDto{" +
                "role='" + role + '\'' +
                ", attributes=" + attributes +
                '}';
    }
}
