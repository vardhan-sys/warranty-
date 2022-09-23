package com.geaviation.techpubs.services.excel.model;

import com.geaviation.techpubs.services.excel.exception.ExcelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ExcelSheet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelSheet.class);

    private boolean firstConstructed = true;
    private boolean sortingMatters = false;
    private List<ExcelRow> excelRows = new ArrayList<>();
    private List<ExcelColumnHelper> excelColumnHelpers = new ArrayList<>();
    private String filename;

    public void addRow(ExcelRow excelRow) throws ExcelException {
        if (null != excelRow && !excelRow.getExcelCells().isEmpty()) {
            if (firstConstructed) {
                this.excelRows.add(excelRow);
                this.excelColumnHelpers = excelRow.getExcelCells().stream()
                        .map(ExcelCell::getExcelColumnHelper)
                        .collect(Collectors.toList());
                firstConstructed = false;
            } else {
                List<ExcelColumnHelper> incomingColumns = excelRow.getExcelCells().stream()
                        .map(ExcelCell::getExcelColumnHelper).collect(Collectors.toList());

                boolean equalColumnsFlag = true;

                if (incomingColumns.size() != excelColumnHelpers.size()) {
                    equalColumnsFlag = false;
                } else {
                    for (int i = 0; i < incomingColumns.size(); i++) {
                        if (!incomingColumns.get(i).getName().equals(excelColumnHelpers.get(i).getName())) {
                            equalColumnsFlag = false;
                        }
                    }
                }

                if (!equalColumnsFlag) {
                    throw new ExcelException("Every row must have the same columns and the same order!!");
                }
                this.excelRows.add(excelRow);
            }
        } else {
            LOGGER.error("The excel row was null or empty!");
        }
    }

    public List<ExcelRow> getExcelRows() {
        return excelRows;
    }

    public void setSortingMatters(boolean sortingMatters) {
        this.sortingMatters = sortingMatters;
    }

    public List<ExcelColumnHelper> getExcelColumnHelpers() {
        return excelColumnHelpers;
    }

    public void handleOrder() {
        excelColumnHelpers.sort(Comparator.comparingInt(ExcelColumnHelper::getOrder));
    }

    public String getFilename() {
        if (filename == null) {
            return null;
        }
        return filename + ".xlsx";
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
