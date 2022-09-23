package com.geaviation.techpubs.models.techlib.response;

import com.geaviation.techpubs.models.techlib.dto.UserRolePolicyAttributesDto;

import java.util.List;

public class RolePolicyAttributeListResponse {

    List<UserRolePolicyAttributesDto> roles;

    public RolePolicyAttributeListResponse() { }

    public RolePolicyAttributeListResponse(List<UserRolePolicyAttributesDto> roles) { this.roles = roles; }

    public List<UserRolePolicyAttributesDto> getRoles() { return roles; }

    public void setRoles(List<UserRolePolicyAttributesDto> roles) { this.roles = roles; }
}
