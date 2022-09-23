package com.geaviation.techpubs.models.cwcadmin;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "cwcadmin_atrbt")
public class CwcadminAtrbtEntity {

    @Id
    @Column(name = "ATRBT_ID", nullable = false)
    private int atrbtId;

    @Column(name = "PRTY_TYP_ID")
    private Integer prtyTypId;

    @Column(name = "ATRBT_NM", nullable = false, length = 250)
    private String atrbtNm;

    @Column(name = "ATRBT_DESC", length = 2000)
    private String atrbtDesc;

    @Column(name = "ATRBT_TYP", length = 20)
    private String atrbtTyp;

    @Column(name = "CMN_ATRBT_IND", length = 1)
    private String cmnAtrbtInd;

    @Column(name = "CRTD_BY", nullable = false, length = 20)
    private String crtdBy;

    @Column(name = "CRTN_DT", nullable = false)
    private Date crtnDt;

    @Column(name = "LST_UPDTD_BY", nullable = false, length = 20)
    private String lstUpdtdBy;

    @Column(name = "LST_UPDT_DT", nullable = false)
    private Date lstUpdtDt;

    public int getAtrbtId() {
        return atrbtId;
    }

    public void setAtrbtId(int atrbtId) {
        this.atrbtId = atrbtId;
    }

    public Integer getPrtyTypId() {
        return prtyTypId;
    }

    public void setPrtyTypId(Integer prtyTypId) {
        this.prtyTypId = prtyTypId;
    }

    public String getAtrbtNm() {
        return atrbtNm;
    }

    public void setAtrbtNm(String atrbtNm) {
        this.atrbtNm = atrbtNm;
    }

    public String getAtrbtDesc() {
        return atrbtDesc;
    }

    public void setAtrbtDesc(String atrbtDesc) {
        this.atrbtDesc = atrbtDesc;
    }

    public String getAtrbtTyp() {
        return atrbtTyp;
    }

    public void setAtrbtTyp(String atrbtTyp) {
        this.atrbtTyp = atrbtTyp;
    }

    public String getCmnAtrbtInd() {
        return cmnAtrbtInd;
    }

    public void setCmnAtrbtInd(String cmnAtrbtInd) {
        this.cmnAtrbtInd = cmnAtrbtInd;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CwcadminAtrbtEntity that = (CwcadminAtrbtEntity) o;
        return atrbtId == that.atrbtId &&
                Objects.equals(prtyTypId, that.prtyTypId) &&
                Objects.equals(atrbtNm, that.atrbtNm) &&
                Objects.equals(atrbtDesc, that.atrbtDesc) &&
                Objects.equals(atrbtTyp, that.atrbtTyp) &&
                Objects.equals(cmnAtrbtInd, that.cmnAtrbtInd) &&
                Objects.equals(crtdBy, that.crtdBy) &&
                Objects.equals(crtnDt, that.crtnDt) &&
                Objects.equals(lstUpdtdBy, that.lstUpdtdBy) &&
                Objects.equals(lstUpdtDt, that.lstUpdtDt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(atrbtId, prtyTypId, atrbtNm, atrbtDesc, atrbtTyp, cmnAtrbtInd, crtdBy, crtnDt, lstUpdtdBy, lstUpdtDt);
    }
}
