package com.geaviation.techpubs.models.techlib;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class RolePermissionEntityPK implements Serializable {

    @Id
    @Column(name = "role", nullable = false)
    private String role;

    @Id
    @Column(name = "resource", nullable = false)
    private String resource;

    @Id
    @Column(name = "action", nullable = false)
    private String action;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolePermissionEntityPK that = (RolePermissionEntityPK) o;
        return Objects.equals(role, that.role) &&
                Objects.equals(resource, that.resource) &&
                Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, resource, action);
    }
}
