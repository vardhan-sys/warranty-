package com.geaviation.techpubs.models.techlib.response;

import com.geaviation.techpubs.models.techlib.dto.UserPermissionsDto;

import java.util.List;

public class PermissionListResponse {

    private List<UserPermissionsDto> permissions;

    public PermissionListResponse() { }

    public PermissionListResponse(List<UserPermissionsDto> permissions) { this.permissions = permissions; }

    public List<UserPermissionsDto> getPermissions() { return permissions; }

    public void setPermissions(List<UserPermissionsDto> permissions) { this.permissions = permissions; }
}
