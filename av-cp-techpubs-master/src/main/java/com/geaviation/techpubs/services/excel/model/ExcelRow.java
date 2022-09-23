package com.geaviation.techpubs.services.excel.model;

import java.util.ArrayList;
import java.util.List;

public class ExcelRow {

    private List<ExcelCell> excelCells = new ArrayList<>();

    public void addExcelCell(ExcelCell excelCell) {
        excelCells.add(excelCell);
    }

    public List<ExcelCell> getExcelCells() {
        return excelCells;
    }
}
