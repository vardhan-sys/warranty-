package com.geaviation.techpubs.models.techlib.dto;

public class UserRolePolicyAttributesDto {

    private String role;
    private String label;
    private Object policy;
    private Object attributes;

    public UserRolePolicyAttributesDto() { }

    public UserRolePolicyAttributesDto(String role, Object policy, Object attributes) {
        this.role = role;
        this.policy = policy;
        this.attributes = attributes;
    }
    public UserRolePolicyAttributesDto(String role, Object policy, Object attributes, String label) {
        this.role = role;
        this.policy = policy;
        this.attributes = attributes;
        this.label = label;
    }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }

    public Object getPolicy() { return policy; }

    public void setPolicy(Object policy) { this.policy = policy; }

    public Object getAttributes() { return attributes; }

    public void setAttributes(Object attributes) { this.attributes = attributes; }

    public String getLabel() { return label; }

	public void setLabel(String label) { this.label = label; }

	@Override
    public String toString() {
        return "UserRolePolicyAttributesDto{" +
                "role='" + role + '\'' +
                ", label='" + label + '\'' +
                ", policy=" + policy +
                ", attributes=" + attributes +
                '}';
    }
}
