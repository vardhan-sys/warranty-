package com.geaviation.techpubs.models.cwcadmin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "cwcadmin_auth_groups")
public class CwcadminAuthGroupsEntity {

    @Id
    @Column(name = "GROUP_ID", nullable = false)
    private int groupId;

    @Column(name = "GROUP_NAME", nullable = false)
    private String groupName;

    @Column(name = "GROUP_DESC")
    private String groupDesc;

    @Column(name = "CREATED_BY", length = 20)
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "MODIFIED_BY", length = 25)
    private String modifiedBy;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDesc() {
        return groupDesc;
    }

    public void setGroupDesc(String groupDesc) {
        this.groupDesc = groupDesc;
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
        CwcadminAuthGroupsEntity that = (CwcadminAuthGroupsEntity) o;
        return groupId == that.groupId &&
                Objects.equals(groupName, that.groupName) &&
                Objects.equals(groupDesc, that.groupDesc) &&
                Objects.equals(createdBy, that.createdBy) &&
                Objects.equals(createdDate, that.createdDate) &&
                Objects.equals(modifiedBy, that.modifiedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, groupName, groupDesc, createdBy, createdDate, modifiedBy);
    }
}
