package com.geaviation.techpubs.models.cwcadmin;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "cwcadmin_auth_user_grp")
public class CwcadminAuthUserGrpEntity {

    @Id
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "SSO_ID", nullable = false)
    private String ssoId;

    @Column(name = "GROUP_ID", nullable = false)
    private int groupId;

    @Column(name = "DESCRIPTION", length = -1)
    private String description;

    @Column(name = "CREATED_BY", length = 20)
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "MODIFIED_BY", length = 25)
    private String modifiedBy;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSsoId() {
        return ssoId;
    }

    public void setSsoId(String ssoId) {
        this.ssoId = ssoId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CwcadminAuthUserGrpEntity that = (CwcadminAuthUserGrpEntity) o;
        return id == that.id &&
                Objects.equals(ssoId, that.ssoId) &&
                groupId == that.groupId &&
                Objects.equals(description, that.description) &&
                Objects.equals(createdBy, that.createdBy) &&
                Objects.equals(createdDate, that.createdDate) &&
                Objects.equals(modifiedBy, that.modifiedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ssoId, groupId, description, createdBy, createdDate, modifiedBy);
    }
}
