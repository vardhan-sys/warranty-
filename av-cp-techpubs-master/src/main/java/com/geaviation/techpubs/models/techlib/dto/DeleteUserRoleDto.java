package com.geaviation.techpubs.models.techlib.dto;

public class DeleteUserRoleDto {

    private String role;

    public DeleteUserRoleDto() { }

    public DeleteUserRoleDto(String role) { this.role = role; }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }
}
