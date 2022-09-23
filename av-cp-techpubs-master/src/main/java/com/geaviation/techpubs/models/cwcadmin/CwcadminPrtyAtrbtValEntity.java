package com.geaviation.techpubs.models.cwcadmin;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "cwcadmin_prty_atrbt_val", schema = "cwcadmin", catalog = "")
public class CwcadminPrtyAtrbtValEntity {

    @Id
    @Column(name = "PRTY_ATRBT_VAL_ID", nullable = false)
    private Integer prtyAtrbtValId;

    @Column(name = "prty_atrbt_val", length = 4000)
    private String prtyAtrbtVal;

    @Column(name = "PRTY_ATRBT_VAL_STRT_DTTM", nullable = false)
    private Timestamp prtyAtrbtValStrtDttm;

    @Column(name = "PRTY_ATRBT_VAL_END_DTTM")
    private Timestamp prtyAtrbtValEndDttm;

    @Column(name = "CRTD_BY", nullable = false, length = 20)
    private String crtdBy;

    @Column(name = "CRTN_DT", nullable = false)
    private Date crtnDt;

    @Column(name = "LST_UPDTD_BY", nullable = false, length = 20)
    private String lstUpdtdBy;

    @Column(name = "LST_UPDT_DT", nullable = false)
    private Date lstUpdtDt;

    @ManyToOne
    @JoinColumn(name = "PRNT_PRTY_ATRBT_VAL_ID", referencedColumnName = "PRTY_ATRBT_VAL_ID", nullable = false, insertable = false, updatable = false)
    private CwcadminPrtyAtrbtValEntity cwcadminPrtyAtrbtValByPrntPrtyAtrbtValId;

    @Column(name = "PRTY_ID")
    private Integer prtyId;

    @Column(name = "ATRBT_ID")
    private Integer atrbtId;

    @Column(name = "PRTL_ID")
    private Integer prtlId;

    @Column(name = "PRNT_PRTY_ATRBT_VAL_ID")
    private Integer prntPrtyAtrbtValId;

    public Integer getPrtyAtrbtValId() { return prtyAtrbtValId; }

    public void setPrtyAtrbtValId(Integer prtyAtrbtValId) { this.prtyAtrbtValId = prtyAtrbtValId; }

    public String getPrtyAtrbtVal() { return prtyAtrbtVal; }

    public void setPrtyAtrbtVal(String prtyAtrbtVal) { this.prtyAtrbtVal = prtyAtrbtVal; }

    public Timestamp getPrtyAtrbtValStrtDttm() { return prtyAtrbtValStrtDttm; }

    public void setPrtyAtrbtValStrtDttm(Timestamp prtyAtrbtValStrtDttm) { this.prtyAtrbtValStrtDttm = prtyAtrbtValStrtDttm; }

    public Timestamp getPrtyAtrbtValEndDttm() { return prtyAtrbtValEndDttm; }

    public void setPrtyAtrbtValEndDttm(Timestamp prtyAtrbtValEndDttm) { this.prtyAtrbtValEndDttm = prtyAtrbtValEndDttm; }

    public String getCrtdBy() { return crtdBy; }

    public void setCrtdBy(String crtdBy) { this.crtdBy = crtdBy; }

    public Date getCrtnDt() { return crtnDt; }

    public void setCrtnDt(Date crtnDt) { this.crtnDt = crtnDt; }

    public String getLstUpdtdBy() { return lstUpdtdBy; }

    public void setLstUpdtdBy(String lstUpdtdBy) { this.lstUpdtdBy = lstUpdtdBy; }

    public Date getLstUpdtDt() { return lstUpdtDt; }

    public void setLstUpdtDt(Date lstUpdtDt) { this.lstUpdtDt = lstUpdtDt; }

    public CwcadminPrtyAtrbtValEntity getCwcadminPrtyAtrbtValByPrntPrtyAtrbtValId() {
        return cwcadminPrtyAtrbtValByPrntPrtyAtrbtValId;
    }

    public void setCwcadminPrtyAtrbtValByPrntPrtyAtrbtValId(CwcadminPrtyAtrbtValEntity cwcadminPrtyAtrbtValByPrntPrtyAtrbtValId) {
        this.cwcadminPrtyAtrbtValByPrntPrtyAtrbtValId = cwcadminPrtyAtrbtValByPrntPrtyAtrbtValId;
    }

    public Integer getPrtyId() { return prtyId; }

    public void setPrtyId(Integer prtyId) { this.prtyId = prtyId; }

    public Integer getAtrbtId() { return atrbtId; }

    public void setAtrbtId(Integer atrbtId) { this.atrbtId = atrbtId; }

    public Integer getPrtlId() { return prtlId; }

    public void setPrtlId(Integer prtlId) { this.prtlId = prtlId; }

    public Integer getPrntPrtyAtrbtValId() { return prntPrtyAtrbtValId; }

    public void setPrntPrtyAtrbtValId(Integer prntPrtyAtrbtValId) { this.prntPrtyAtrbtValId = prntPrtyAtrbtValId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CwcadminPrtyAtrbtValEntity that = (CwcadminPrtyAtrbtValEntity) o;
        return prtyAtrbtValId == that.prtyAtrbtValId &&
                Objects.equals(prtyAtrbtVal, that.prtyAtrbtVal) &&
                Objects.equals(prtyAtrbtValStrtDttm, that.prtyAtrbtValStrtDttm) &&
                Objects.equals(prtyAtrbtValEndDttm, that.prtyAtrbtValEndDttm) &&
                Objects.equals(crtdBy, that.crtdBy) &&
                Objects.equals(crtnDt, that.crtnDt) &&
                Objects.equals(lstUpdtdBy, that.lstUpdtdBy) &&
                Objects.equals(lstUpdtDt, that.lstUpdtDt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prtyAtrbtValId, prtyAtrbtVal, prtyAtrbtValStrtDttm, prtyAtrbtValEndDttm, crtdBy, crtnDt, lstUpdtdBy, lstUpdtDt);
    }
}
