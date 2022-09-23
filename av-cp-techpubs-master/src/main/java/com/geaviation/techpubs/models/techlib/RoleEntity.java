package com.geaviation.techpubs.models.techlib;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "role", schema = "techlib")
@TypeDef(
        name = "jsonb",
        typeClass = JsonBinaryType.class
)
public class RoleEntity {

    @Id
    @Column(name = "name", nullable = false, length = -1)
    private String name;

    @Column(name = "label", nullable = false, length = -1)
    private String label;

    @Column(name = "description", nullable = false, length = -1)
    private String description;

    @Type(type = "jsonb")
    @Column(name = "policy", nullable = false, columnDefinition = "jsonb")
    private RolePolicy policy;

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

    @JsonIgnore
    @OneToMany(mappedBy = "roleByRole")
    private List<UserRoleEntity> userRoles;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() { return label; }

    public void setLabel(String label) { this.label = label; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RolePolicy getPolicy() { return policy; }

    public void setPolicy(RolePolicy policy) { this.policy = policy; }

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

    public List<UserRoleEntity> getUserRoles() { return userRoles; }

    public void setUserRoles(List<UserRoleEntity> userRoles) { this.userRoles = userRoles; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleEntity that = (RoleEntity) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(label, that.label) &&
                Objects.equals(description, that.description) &&
                Objects.equals(policy, that.policy) &&
                Objects.equals(createdBy, that.createdBy) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(lastUpdatedBy, that.lastUpdatedBy) &&
                Objects.equals(lastUpdatedDate, that.lastUpdatedDate) &&
                Objects.equals(userRoles, that.userRoles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, label, description, policy, createdBy, creationDate, lastUpdatedBy, lastUpdatedDate, userRoles);
    }
}
