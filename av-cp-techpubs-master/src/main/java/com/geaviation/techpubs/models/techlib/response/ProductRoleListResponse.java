package com.geaviation.techpubs.models.techlib.response;

import java.util.List;

import com.geaviation.techpubs.models.techlib.dto.ProductRolesDto;

public class ProductRoleListResponse {

	private List<ProductRolesDto> roles;

	public ProductRoleListResponse() {
	}

	public ProductRoleListResponse(List<ProductRolesDto> roles) {
		this.roles = roles;
	}

	public List<ProductRolesDto> getRoles() {
		return roles;
	}

	public void setRoles(List<ProductRolesDto> roles) {
		this.roles = roles;
	}
}
