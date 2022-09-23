package com.geaviation.techpubs.models.techlib.dto;

public class ProductRolesDto {
	
	private String label;
	private String role;
	private String description;
	
	public ProductRolesDto() {
	}
	public ProductRolesDto(String role,String label,String description) {
		this.label = label;
		this.role = role;
		this.description = description;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	

}
