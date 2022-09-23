package com.geaviation.techpubs.models.cwcadmin;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@IdClass(CwcadminUsrRlEntityPK.class)
@Table(name = "cwcadmin_usr_rl")
public class CwcadminUsrRlEntity {
    @Id
    @Column(name = "USR_ID", nullable = false)
    private Integer userId;

    @Id
    @Column(name = "RL_ID", nullable = false)
    private Integer roleId;

    @JsonIgnore
    @Column(name = "CRTD_BY", nullable = false, length = 20)
    private String crtdBy;

    @JsonIgnore
    @Column(name = "CRTN_DT", nullable = false)
    private Date crtnDt;

    @JsonIgnore
    @Column(name = "LST_UPDTD_BY", nullable = false, length = 20)
    private String lstUpdtdBy;

    @JsonIgnore
    @Column(name = "LST_UPDT_DT", nullable = false)
    private Date lstUpdtDt;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getCrtdBy() {
        return crtdBy;
    }

    public void setCrtdBy(String crtdBy) {
        this.crtdBy = crtdBy;
    }

    public Date getCrtnDt() {
        return crtnDt;
    }

    public void setCrtnDt(Date crtnDt) {
        this.crtnDt = crtnDt;
    }

    public String getLstUpdtdBy() {
        return lstUpdtdBy;
    }

    public void setLstUpdtdBy(String lstUpdtdBy) {
        this.lstUpdtdBy = lstUpdtdBy;
    }

    public Date getLstUpdtDt() {
        return lstUpdtDt;
    }

    public void setLstUpdtDt(Date lstUpdtDt) {
        this.lstUpdtDt = lstUpdtDt;
    }

    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CwcadminUsrRlEntity that = (CwcadminUsrRlEntity) o;
        return Objects.equals(userId, that.userId) &&
            Objects.equals(roleId, that.roleId) &&
            Objects.equals(crtdBy, that.crtdBy) &&
            Objects.equals(crtnDt, that.crtnDt) &&
            Objects.equals(lstUpdtdBy, that.lstUpdtdBy) &&
            Objects.equals(lstUpdtDt, that.lstUpdtDt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, roleId, crtdBy, crtnDt, lstUpdtdBy, lstUpdtDt);
    }
}