package com.geaviation.techpubs.models.techlib.dto;

public class UserPermissionsDto {

    private String resource;
    private String action;
    private String type;
    private String product;

    public UserPermissionsDto() {
        super();
    }

    public UserPermissionsDto(String resource, String action, String type) {
        this.resource = resource;
        this.action = action;
        this.type = type;
    }
    
    public UserPermissionsDto(String resource, String action, String type, String product) {
        this.resource = resource;
        this.action = action;
        this.type = type;
        this.product = product;
    }

    public String getResource() { return resource; }

    public void setResource(String resource) { this.resource = resource; }

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }
   
    public String getProduct() { return product; }
    
    public void setProduct(String product) { this.product = product; }
}
