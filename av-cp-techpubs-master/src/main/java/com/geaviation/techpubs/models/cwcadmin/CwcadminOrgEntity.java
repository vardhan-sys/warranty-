package com.geaviation.techpubs.models.cwcadmin;


import com.geaviation.techpubs.services.excel.annotation.Excel;
import com.geaviation.techpubs.services.excel.annotation.ExcelColumn;
import com.geaviation.techpubs.services.util.AppConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
import java.util.Objects;

@Excel(fileName = "companies", orderMatters = true)
@Entity
@Table(name = "cwcadmin_org")
public class CwcadminOrgEntity {

    @Id
    @JsonIgnore
    @Column(name = "ORG_ID", nullable = false)
    private int orgId;

    @Column(name = "ORG_NM", nullable = false, length = 250)
    @ExcelColumn(name = AppConstants.COMPANY, order = 1)
    private String company;

    @JsonIgnore
    @Column(name = "ORG_DESC", length = 2000)
    private String orgDesc;

    @JsonIgnore
    @Column(name = "ADDR_TXT", length = 1500)
    private String addrTxt;

    @JsonIgnore
    @Column(name = "CTY_NM", length = 250)
    private String ctyNm;

    @JsonIgnore
    @Column(name = "ST_NM", length = 250)
    private String stNm;

    @JsonIgnore
    @Column(name = "ISO_CNTRY_NM", length = 250)
    private String isoCntryNm;

    @JsonIgnore
    @Column(name = "ZIP_CD", columnDefinition = "char", length = 250)
    private String zipCd;

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

    @Column(name = "MDM_ORG_ID", length = 100)
    @ExcelColumn(name = AppConstants.ICAO_CODE, order = 2)
    private String icaoCode;

    @JsonIgnore()
    @Column(name = "LST_MDM_UPDT_DT")
    private Date lstMdmUpdtDt;

    @Column(name = "DUNS_NUM", length = 9)
    @ExcelColumn(name = AppConstants.DUNS_NUMBER, order = 3)
    private String dunsNum;

    @JsonIgnore
    @Column(name = "SNECHMA_VALUE", length = 4)
    private String snechmaValue;

    public int getOrgId() {
        return orgId;
    }

    public String getCompany() {
        return company;
    }

    public String getOrgDesc() {
        return orgDesc;
    }

    public String getAddrTxt() {
        return addrTxt;
    }

    public String getCtyNm() {
        return ctyNm;
    }

    public String getStNm() {
        return stNm;
    }

    public String getIsoCntryNm() {
        return isoCntryNm;
    }

    public String getZipCd() {
        return zipCd;
    }

    public String getCrtdBy() {
        return crtdBy;
    }

    public Date getCrtnDt() {
        return crtnDt;
    }

    public String getLstUpdtdBy() {
        return lstUpdtdBy;
    }

    public Date getLstUpdtDt() {
        return lstUpdtDt;
    }

    public String getIcaoCode() {
        return icaoCode;
    }

    public Date getLstMdmUpdtDt() {
        return lstMdmUpdtDt;
    }

    public String getDunsNum() {
        return dunsNum;
    }

    public String getSnechmaValue() {
        return snechmaValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CwcadminOrgEntity that = (CwcadminOrgEntity) o;
        return orgId == that.orgId &&
                Objects.equals(company, that.company) &&
                Objects.equals(orgDesc, that.orgDesc) &&
                Objects.equals(addrTxt, that.addrTxt) &&
                Objects.equals(ctyNm, that.ctyNm) &&
                Objects.equals(stNm, that.stNm) &&
                Objects.equals(isoCntryNm, that.isoCntryNm) &&
                Objects.equals(zipCd, that.zipCd) &&
                Objects.equals(crtdBy, that.crtdBy) &&
                Objects.equals(crtnDt, that.crtnDt) &&
                Objects.equals(lstUpdtdBy, that.lstUpdtdBy) &&
                Objects.equals(lstUpdtDt, that.lstUpdtDt) &&
                Objects.equals(icaoCode, that.icaoCode) &&
                Objects.equals(lstMdmUpdtDt, that.lstMdmUpdtDt) &&
                Objects.equals(dunsNum, that.dunsNum) &&
                Objects.equals(snechmaValue, that.snechmaValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orgId, company, orgDesc, addrTxt, ctyNm, stNm, isoCntryNm, zipCd, crtdBy, crtnDt, lstUpdtdBy, lstUpdtDt, icaoCode, lstMdmUpdtDt, dunsNum, snechmaValue);
    }
}
