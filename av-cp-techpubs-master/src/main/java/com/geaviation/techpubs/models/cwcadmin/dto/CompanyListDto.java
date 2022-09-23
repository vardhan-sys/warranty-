package com.geaviation.techpubs.models.cwcadmin.dto;

import com.geaviation.techpubs.services.excel.annotation.Excel;
import com.geaviation.techpubs.services.excel.annotation.ExcelColumn;
import com.geaviation.techpubs.services.util.AppConstants;

@Excel(fileName = "companies_summary", orderMatters = true)
public class CompanyListDto {

    @ExcelColumn(name = AppConstants.COMPANY, order = 1)
    private String company;

    @ExcelColumn(name = AppConstants.ICAO_CODE, order = 2)
    private String icaoCode;

    @ExcelColumn(name = AppConstants.DUNS_NUMBER, order = 3)
    private String dunsNum;

    public CompanyListDto() { }

    public CompanyListDto(String company, String icaoCode, String dunsNum) {
        this.company = company;
        this.icaoCode = icaoCode;
        this.dunsNum = dunsNum;
    }

    public String getCompany() { return company; }

    public void setCompany(String company) { this.company = company; }

    public String getIcaoCode() { return icaoCode; }

    public void setIcaoCode(String icaoCode) { this.icaoCode = icaoCode; }

    public String getDunsNum() { return dunsNum; }

    public void setDunsNum(String dunsNum) { this.dunsNum = dunsNum; }
}
