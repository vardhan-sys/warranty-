package com.geaviation.techpubs.models.cwcadmin;

import java.io.Serializable;
import java.util.Objects;

public class CwcadminUsrRlEntityPK implements Serializable {
    private String userId;
    private String roleId;

    public CwcadminUsrRlEntityPK(String userId, String roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CwcadminUsrRlEntityPK usrRlId = (CwcadminUsrRlEntityPK) o;
        return Objects.equals(userId, usrRlId.userId) &&
                Objects.equals(roleId, usrRlId.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, roleId);
    }
}