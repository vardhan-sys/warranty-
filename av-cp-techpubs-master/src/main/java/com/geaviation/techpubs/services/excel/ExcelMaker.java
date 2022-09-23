package com.geaviation.techpubs.services.excel;

import com.geaviation.techpubs.services.excel.annotation.Excel;
import com.geaviation.techpubs.services.excel.annotation.ExcelColumn;
import com.geaviation.techpubs.services.excel.exception.ExcelException;
import com.geaviation.techpubs.services.excel.model.ExcelCell;
import com.geaviation.techpubs.services.excel.model.ExcelColumnHelper;
import com.geaviation.techpubs.services.excel.model.ExcelRow;
import com.geaviation.techpubs.services.excel.model.ExcelSheet;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;


public class ExcelMaker {

    public ExcelMaker() { }

    /**
     * <p>Saves an excel sheet as a file.</p>
     * @param excelSheet Excel Sheet object
     * @param os Output stream for containing the content
     * @throws ExcelException
     */
    public static void excelSheetToFile(ExcelSheet excelSheet, ByteArrayOutputStream os)
            throws ExcelException {
        Workbook workbook = new XSSFWorkbook();
        String sheetName = excelSheet.getFilename();
        sheetName = sheetName.replace(".xlsx", "");
        Sheet sheet = workbook.createSheet(sheetName);

        CellStyle headerCellStyle = setFancyFormats(workbook);

        Row headerRow = sheet.createRow(0);

        List<ExcelColumnHelper> excelColumnHelpers = excelSheet.getExcelColumnHelpers();

        for (int i = 0; i < excelColumnHelpers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(excelColumnHelpers.get(i).getName());
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for (ExcelRow excelRow : excelSheet.getExcelRows()) {
            Row row = sheet.createRow(rowNum++);
            int cellNum = 0;

            for (ExcelCell excelCell : excelRow.getExcelCells()) {
                row.createCell(cellNum++).setCellValue(excelCell.getValue());
            }
        }

        int columnNum = 0;
        for (ExcelColumnHelper ignored : excelColumnHelpers) {
            sheet.autoSizeColumn(columnNum++);
        }
        try {
            workbook.write(os);
            workbook.close();
        } catch (IOException e) {
            throw new ExcelException("Could not write/close the file!", e);
        }
    }

    private static CellStyle setFancyFormats(Workbook workbook) {
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        return headerCellStyle;
    }

    /**
     * <p>Creates an excel sheet.</p>
     * @param objects Collection of objects containing @Excel and @ExcelColumn annotations.
     * @return A an excel sheet object
     * @throws ExcelException
     */
    public static ExcelSheet buildExcelSheet(Collection<?> objects) throws ExcelException {
        ExcelSheet excelSheet = new ExcelSheet();
        for (Object object : objects) {
            ExcelRow excelRow = new ExcelRow();
            List<ExcelCell> excelCells = new ArrayList<>();
            addMethodReturnToExcelRow(object, excelCells);
            addInstanceFieldValuesToRow(object, excelCells);
            handleOrder(excelCells, excelRow, object, excelSheet);
            handleName(object, excelSheet);
            excelSheet.addRow(excelRow);
        }
        excelSheet.handleOrder();
        return excelSheet;
    }

    private static void handleName(Object object, ExcelSheet excelSheet) throws ExcelException {
        if (null == excelSheet.getFilename()) {
            Excel excel = getExcel(object);
            excelSheet.setFilename(excel.fileName());
        }
    }

    private static void handleOrder(List<ExcelCell> excelCells, ExcelRow excelRow, Object object,
                                    ExcelSheet excelSheet)
            throws ExcelException {
        Excel excel = getExcel(object);

        if (excel.orderMatters()) {
            excelCells.sort(Comparator.comparingInt(ExcelCell::getOrder));
            excelSheet.setSortingMatters(true);
        }
        excelCells.forEach(excelRow::addExcelCell);
    }

    private static Excel getExcel(Object object) throws ExcelException {
        Class<?> clazz = object.getClass();
        if (!clazz.isAnnotationPresent(Excel.class)) {
            throw new ExcelException(
                    "The Object is not annotated with Excel and cannot be excel-able");
        }
        return clazz.getAnnotation(Excel.class);
    }

    private static void addMethodReturnToExcelRow(Object object, List<ExcelCell> excelCells)
            throws ExcelException {
        for (Method method : object.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.isAnnotationPresent(ExcelColumn.class)) {
                ExcelColumn excelColumn = method.getAnnotation(ExcelColumn.class);
                try {
                    String value = method.invoke(object).toString();
                    String columnName = excelColumn.name();
                    ExcelColumnHelper excelRowsColumn =
                            new ExcelColumnHelper(columnName, excelColumn.order());
                    ExcelCell excelCell = new ExcelCell(value, excelRowsColumn, excelColumn.order());
                    excelCells.add(excelCell);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new ExcelException(
                            "Could not get the method value of a Excel Column!" + excelColumn.name(), e);
                }
            }
        }
    }

    private static void addInstanceFieldValuesToRow(Object object, List<ExcelCell> excelCells) throws ExcelException {
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(ExcelColumn.class)) {
                ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
                try {
                    String value;
                    if (null == field.get(object)) {
                        value = "";
                    } else {
                        value = field.get(object).toString();
                    }
                    String columnName = excelColumn.name();
                    ExcelColumnHelper excelRowsColumn =
                            new ExcelColumnHelper(columnName, excelColumn.order());
                    ExcelCell excelCell = new ExcelCell(value, excelRowsColumn, excelColumn.order());
                    excelCells.add(excelCell);
                } catch (IllegalAccessException e) {
                    throw new ExcelException(
                            "Could not get the field value of a Excel Column:" + excelColumn.name(), e);
                }
            }
        }
    }
}
