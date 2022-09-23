package com.geaviation.techpubs.models.techlib;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "user", schema = "techlib")
public class UserEntity {

    @Id
    @Column(name = "sso", nullable = false, length = -1)
    private String sso;

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
    @OneToMany(mappedBy = "userBySso", fetch = FetchType.LAZY)
    private List<UserRoleEntity> userRoles;

    public UserEntity() { }

    public UserEntity(String sso, String createdBy, Timestamp creationDate, String lastUpdatedBy, Timestamp lastUpdatedDate) {
        this.sso = sso;
        this.createdBy = createdBy;
        this.creationDate = creationDate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public String getSso() {
        return sso;
    }

    public void setSso(String sso) {
        this.sso = sso;
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

    public List<UserRoleEntity> getUserRoles() { return userRoles; }

    public void setUserRoles(List<UserRoleEntity> userRoles) { this.userRoles = userRoles; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(sso, that.sso) &&
                Objects.equals(createdBy, that.createdBy) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(lastUpdatedBy, that.lastUpdatedBy) &&
                Objects.equals(lastUpdatedDate, that.lastUpdatedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sso, createdBy, creationDate, lastUpdatedBy, lastUpdatedDate);
    }
}
