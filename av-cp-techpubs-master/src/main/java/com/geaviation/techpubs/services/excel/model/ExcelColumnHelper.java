package com.geaviation.techpubs.services.excel.model;

public class ExcelColumnHelper {

    private String name;
    private int order;

    public ExcelColumnHelper(String name, int order) {
        this.name = name;
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
    }
}
