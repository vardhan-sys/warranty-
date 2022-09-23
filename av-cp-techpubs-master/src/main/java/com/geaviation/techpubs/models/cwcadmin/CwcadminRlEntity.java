package com.geaviation.techpubs.models.cwcadmin;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cwcadmin_rl")
public class CwcadminRlEntity {

    @Id
    @Column(name = "RL_ID", nullable = false)
    private Integer roleId;

    @Column(name = "RL_NM", length = 250)
    private String roleName;

    @JsonIgnore
    @Column(name = "PRTL_ID")
    private Integer portalId;

    @OneToMany(fetch= FetchType.EAGER)
    @JoinColumn(name = "PRTL_ID")
    private List<CwcadminPrtlEntity> orgs;

    @JsonIgnore
    @Column(name = "RL_TYP_ID")
    private Integer roleTypeId;

    @JsonIgnore
    @Column(name = "RL_DESC", length = 2000)
    private String roleDescription;

    @JsonIgnore
    @Column(name = "APLCTN_RL_ID", length = 100)
    private String applicationRoleId;

    @JsonIgnore
    @Column(name = "IS_READ_ONLY", length = 20)
    private String isReadOnly;

    @JsonIgnore
    @Column(name = "COLOB_CODE", length = 20)
    private String colobCode;

    @JsonIgnore
    @Column(name = "LDAP_REQ", length = 1)
    private String ldapRequest;

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

    @JsonIgnore
    @Column(name = "Managed_by", length = 20)
    private String managedBy;

    @JsonIgnore
    @Column(name = "BACKBASE_SYNC", length = 20)
    private String backbaseSync;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getPortalId() {
        return portalId;
    }

    public void setPortalId(Integer portalId) {
        this.portalId = portalId;
    }

    public Integer getRoleTypeId() {
        return roleTypeId;
    }

    public void setRoleTypeId(Integer roleTypeId) {
        this.roleTypeId = roleTypeId;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    public String getApplicationRoleId() {
        return applicationRoleId;
    }

    public void setApplicationRoleId(String applicationRoleId) {
        this.applicationRoleId = applicationRoleId;
    }

    public String getIsReadOnly() {
        return isReadOnly;
    }

    public void setIsReadOnly(String isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    public String getColobCode() {
        return colobCode;
    }

    public void setColobCode(String colobCode) {
        this.colobCode = colobCode;
    }

    public String getLdapRequest() {
        return ldapRequest;
    }

    public void setLdapRequest(String ldapRequest) {
        this.ldapRequest = ldapRequest;
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

    public String getManagedBy() {
        return managedBy;
    }

    public void setManagedBy(String managedBy) {
        this.managedBy = managedBy;
    }

    public String getBackbaseSync() {
        return backbaseSync;
    }

    public void setBackbaseSync(String backbaseSync) {
        this.backbaseSync = backbaseSync;
    }

    public List<CwcadminPrtlEntity> getOrgs() {
        return orgs;
    }

    public void setOrgs(List<CwcadminPrtlEntity> orgs) {
        this.orgs = orgs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CwcadminRlEntity that = (CwcadminRlEntity) o;
        return roleId == that.roleId &&
            portalId == that.portalId &&
            roleTypeId == that.roleTypeId &&
            Objects.equals(roleName, that.roleName) &&
            Objects.equals(roleDescription, that.roleDescription) &&
            Objects.equals(applicationRoleId, that.applicationRoleId) &&
            Objects.equals(isReadOnly, that.isReadOnly) &&
            Objects.equals(colobCode, that.colobCode) &&
            Objects.equals(ldapRequest, that.ldapRequest) &&
            Objects.equals(crtdBy, that.crtdBy) &&
            Objects.equals(crtnDt, that.crtnDt) &&
            Objects.equals(lstUpdtdBy, that.lstUpdtdBy) &&
            Objects.equals(lstUpdtDt, that.lstUpdtDt) &&
            Objects.equals(managedBy, that.managedBy) &&
            Objects.equals(backbaseSync, that.backbaseSync);
    }

    @Override
    public int hashCode() {
        return Objects
            .hash(roleId, roleName, portalId, roleTypeId, roleDescription, applicationRoleId,
                isReadOnly,
                colobCode, ldapRequest, crtdBy, crtnDt, lstUpdtdBy, lstUpdtDt, managedBy,
                backbaseSync);
    }
}