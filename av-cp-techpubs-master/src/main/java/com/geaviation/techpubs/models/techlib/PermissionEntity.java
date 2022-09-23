package com.geaviation.techpubs.models.techlib;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "permission", schema = "techlib")
@IdClass(PermissionEntityPK.class)
public class PermissionEntity {

    @Id
    private String resource;

    @Id
    private String action;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "resource", nullable = false, insertable = false, updatable = false)
    private ResourceEntity resourceByName;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "action", nullable = false, insertable = false, updatable = false)
    private ActionEntity actionByName;

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

    public String getResource() { return resource; }

    public void setResource(String resource) { this.resource = resource; }

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public ResourceEntity getResourceByName() { return resourceByName; }

    public void setResourceByName(ResourceEntity resourceByName) { this.resourceByName = resourceByName; }

    public ActionEntity getActionByName() { return actionByName; }

    public void setActionByName(ActionEntity actionByName) { this.actionByName = actionByName; }

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
        PermissionEntity that = (PermissionEntity) o;
        return Objects.equals(resource, that.resource) &&
                Objects.equals(action, that.action) &&
                Objects.equals(resourceByName, that.resourceByName) &&
                Objects.equals(actionByName, that.actionByName) &&
                Objects.equals(createdBy, that.createdBy) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(lastUpdatedBy, that.lastUpdatedBy) &&
                Objects.equals(lastUpdatedDate, that.lastUpdatedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resource, action, resourceByName, actionByName, createdBy, creationDate, lastUpdatedBy, lastUpdatedDate);
    }
}
