package com.geaviation.techpubs.models.techlib;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "user_role", schema = "techlib")
@TypeDef(
        name = "jsonb",
        typeClass = JsonBinaryType.class
)
@IdClass(UserRoleEntityPK.class)
public class UserRoleEntity {

    @Id
    private String sso;

    @Id
    private String role;

    @Type(type = "jsonb")
    @Column(name = "attributes", nullable = false, columnDefinition = "jsonb")
    private UserRoleAttributes attributes;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn(name = "sso", nullable = false, insertable = false, updatable = false)
    private UserEntity userBySso;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn(name = "role", referencedColumnName = "name", nullable = false, insertable = false, updatable = false)
    private RoleEntity roleByRole;

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

    public UserRoleEntity() { }

    public UserRoleEntity(String sso, String role, UserRoleAttributes attributes, String createdBy, Timestamp creationDate, String lastUpdatedBy, Timestamp lastUpdatedDate) {
        this.sso = sso;
        this.role = role;
        this.attributes = attributes;
        this.createdBy = createdBy;
        this.creationDate = creationDate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public String getSso() { return sso; }

    public void setSso(String sso) { this.sso = sso; }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }

    public UserRoleAttributes getAttributes() { return attributes; }

    public void setAttributes(UserRoleAttributes attributes) { this.attributes = attributes; }

    public UserEntity getUserBySso() { return userBySso; }

    public void setUserBySso(UserEntity userBySso) { this.userBySso = userBySso; }

    public RoleEntity getRoleByRole() { return roleByRole; }

    public void setRoleByRole(RoleEntity roleByRole) { this.roleByRole = roleByRole; }

    public String getCreatedBy() { return createdBy; }

    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public Timestamp getCreationDate() { return creationDate; }

    public void setCreationDate(Timestamp creationDate) { this.creationDate = creationDate; }

    public String getLastUpdatedBy() { return lastUpdatedBy; }

    public void setLastUpdatedBy(String lastUpdatedBy) { this.lastUpdatedBy = lastUpdatedBy; }

    public Timestamp getLastUpdatedDate() { return lastUpdatedDate; }

    public void setLastUpdatedDate(Timestamp lastUpdatedDate) { this.lastUpdatedDate = lastUpdatedDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRoleEntity that = (UserRoleEntity) o;
        return Objects.equals(sso, that.sso) &&
                Objects.equals(role, that.role) &&
                Objects.equals(attributes, that.attributes) &&
                Objects.equals(userBySso, that.userBySso) &&
                Objects.equals(roleByRole, that.roleByRole) &&
                Objects.equals(createdBy, that.createdBy) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(lastUpdatedBy, that.lastUpdatedBy) &&
                Objects.equals(lastUpdatedDate, that.lastUpdatedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sso, role, attributes, userBySso, roleByRole, createdBy, creationDate, lastUpdatedBy, lastUpdatedDate);
    }
}
