package com.geaviation.techpubs.models.cwcadmin;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "cwcadmin_prtl")
public class CwcadminPrtlEntity {

    @Id
    @Column(name = "PRTL_ID", nullable = false)
    private Integer portalId;

    @Column(name = "PRTL_NM", length = 250)
    private String portalName;

    @JsonIgnore
    @Column(name = "PRTL_DESC", length = 2000)
    private String portalDescription;

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

    public Integer getPortalId() {
        return portalId;
    }

    public void setPortalId(Integer portalId) {
        this.portalId = portalId;
    }

    public String getPortalName() {
        return portalName;
    }

    public void setPortalName(String portalName) {
        this.portalName = portalName;
    }

    public String getPortalDescription() {
        return portalDescription;
    }

    public void setPortalDescription(String portalDescription) {
        this.portalDescription = portalDescription;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CwcadminPrtlEntity that = (CwcadminPrtlEntity) o;
        return Objects.equals(portalId, that.portalId) &&
            Objects.equals(portalName, that.portalName) &&
            Objects.equals(portalDescription, that.portalDescription) &&
            Objects.equals(crtdBy, that.crtdBy) &&
            Objects.equals(crtnDt, that.crtnDt) &&
            Objects.equals(lstUpdtdBy, that.lstUpdtdBy) &&
            Objects.equals(lstUpdtDt, that.lstUpdtDt);
    }

    @Override
    public int hashCode() {
        return Objects
            .hash(portalId, portalName, portalDescription, crtdBy, crtnDt, lstUpdtdBy, lstUpdtDt);
    }
}