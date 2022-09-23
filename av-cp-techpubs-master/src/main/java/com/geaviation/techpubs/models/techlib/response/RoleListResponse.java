package com.geaviation.techpubs.models.techlib.response;

import com.geaviation.techpubs.models.techlib.RoleEntity;

import java.util.List;

public class RoleListResponse {

    List<RoleEntity> roles;

    public RoleListResponse() { }

    public RoleListResponse(List<RoleEntity> roles) { this.roles = roles; }

    public List<RoleEntity> getRoles() { return roles; }

    public void setRoles(List<RoleEntity> roles) { this.roles = roles; }
}
