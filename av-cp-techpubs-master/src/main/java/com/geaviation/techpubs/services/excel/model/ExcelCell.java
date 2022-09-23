package com.geaviation.techpubs.services.excel.model;

public class ExcelCell {

    private String value;
    private ExcelColumnHelper excelColumnHelper;
    private int order;

    public ExcelCell(String value, ExcelColumnHelper excelColumnHelper, int order) {
        this.value = value;
        this.excelColumnHelper = excelColumnHelper;
        this.order = order;
    }

    public String getValue() {
        return value;
    }

    public ExcelColumnHelper getExcelColumnHelper() {
        return excelColumnHelper;
    }

    public int getOrder() {
        return order;
    }
}
