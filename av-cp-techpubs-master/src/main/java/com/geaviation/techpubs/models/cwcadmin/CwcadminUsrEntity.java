package com.geaviation.techpubs.models.cwcadmin;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cwcadmin_usr")
public class CwcadminUsrEntity {
    @Id
    @Column(name = "USR_ID", nullable = false)
    private Integer userId;

    @Column(name = "USR_SSO", nullable = false, length = 50)
    private String sso;

    @Column(name = "FRST_NM", length = 250)
    private String firstName;

    @JsonIgnore
    @Column(name = "MDL_NM", length = 250)
    private String middleName;

    @Column(name = "LST_NM", length = 250)
    private String lastName;

    @Column(name = "PRIM_EML_ADD", length = 250)
    private String primaryEmail;

    @JsonIgnore
    @Column(name = "SEC_EML_ADD", length = 250)
    private String secondaryEmail;

    @Transient
    private List<CwcadminPrtlEntity> orgs;

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

    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getSso() {
        return sso;
    }

    public void setSso(String sso) {
        this.sso = sso;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    public String getSecondaryEmail() {
        return secondaryEmail;
    }

    public void setSecondaryEmail(String secondaryEmail) {
        this.secondaryEmail = secondaryEmail;
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

    public List<CwcadminPrtlEntity> getOrgs() {
        return orgs;
    }

    public void setOrgs(List<CwcadminPrtlEntity> orgs) {
        this.orgs = orgs;
    }

    public void setLstUpdtDt(Date lstUpdtDt) {
        this.lstUpdtDt = lstUpdtDt;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CwcadminUsrEntity that = (CwcadminUsrEntity) o;
        return userId == that.userId &&
            Objects.equals(sso, that.sso) &&
            Objects.equals(firstName, that.firstName) &&
            Objects.equals(middleName, that.middleName) &&
            Objects.equals(lastName, that.lastName) &&
            Objects.equals(primaryEmail, that.primaryEmail) &&
            Objects.equals(secondaryEmail, that.secondaryEmail) &&
            Objects.equals(crtdBy, that.crtdBy) &&
            Objects.equals(crtnDt, that.crtnDt) &&
            Objects.equals(lstUpdtdBy, that.lstUpdtdBy) &&
            Objects.equals(lstUpdtDt, that.lstUpdtDt);
    }

    @Override
    public int hashCode() {
        return Objects
            .hash(userId, sso, firstName, middleName, lastName, primaryEmail, secondaryEmail,
                crtdBy,
                crtnDt, lstUpdtdBy, lstUpdtDt);
    }
}