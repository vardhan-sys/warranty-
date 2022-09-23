package com.geaviation.techpubs.models.techlib;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "role_permission", schema = "techlib")
@IdClass(RolePermissionEntityPK.class)
public class RolePermissionEntity {

    @Id
    private String role;

    @Id
    private String resource;

    @Id
    private String action;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "role", referencedColumnName = "name", nullable = false, insertable = false, updatable = false)
    private RoleEntity roleByName;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumns({
            @JoinColumn(name = "resource", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "action", nullable = false, insertable = false, updatable = false)
    })
    private PermissionEntity permission;

    @JsonIgnore
    @Column(name = "created_by", length = -1)
    private String createdBy;

    @JsonIgnore
    @Column(name = "creation_date")
    private Timestamp creationDate;

    @JsonIgnore
    @Column(name = "last_updated_by", length = -1)
    private String lastUpdatedBy;

    @JsonIgnore
    @Column(name = "last_updated_date")
    private Timestamp lastUpdatedDate;

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

    public RoleEntity getRoleByName() {
        return roleByName;
    }

    public void setRoleByName(RoleEntity roleByName) {
        this.roleByName = roleByName;
    }

    public PermissionEntity getPermission() { return permission; }

    public void setPermission(PermissionEntity permission) {
        this.permission = permission;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Timestamp getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Timestamp lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolePermissionEntity that = (RolePermissionEntity) o;
        return Objects.equals(role, that.role) &&
                Objects.equals(resource, that.resource) &&
                Objects.equals(action, that.action) &&
                Objects.equals(roleByName, that.roleByName) &&
                Objects.equals(permission, that.permission) &&
                Objects.equals(createdBy, that.createdBy) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(lastUpdatedBy, that.lastUpdatedBy) &&
                Objects.equals(lastUpdatedDate, that.lastUpdatedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, resource, action, roleByName, permission, createdBy, creationDate, lastUpdatedBy, lastUpdatedDate);
    }
}
