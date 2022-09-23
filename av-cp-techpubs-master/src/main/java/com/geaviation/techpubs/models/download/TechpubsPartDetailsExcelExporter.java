package com.geaviation.techpubs.models.download;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.geaviation.techpubs.models.download.utils.ExcelExportUtils;

public class TechpubsPartDetailsExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<TechpubsPartDetailsExcelEntry> excelEntries;

    /**
     * instantiates the ExcelExporter with the data it will export
     *
     * @param excelEntries the entries to be used in the creation of the excel sheet
     */
    public TechpubsPartDetailsExcelExporter(List<TechpubsPartDetailsExcelEntry> excelEntries) {
        this.excelEntries = excelEntries;
        workbook = new XSSFWorkbook();
    }

    /**
     * writes the headers for the excel sheet
     */
    private void writeHeaderLine() {
        sheet = workbook.createSheet("Part Details");
        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        ExcelExportUtils.createCell(row, 0, "Part Number", style);
        ExcelExportUtils.createCell(row, 1, "Part Description", style);
        ExcelExportUtils.createCell(row, 2, "Part Quantity", style);
        ExcelExportUtils.createCell(row, 3, "CSN", style);

    }

    /**
     * fills out the excel sheet with the corresponding data using the entries given in the constructor
     */
    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (TechpubsPartDetailsExcelEntry entry : excelEntries) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            ExcelExportUtils.createCell(row, columnCount++, entry.getPartNumber(), style);
            ExcelExportUtils.createCell(row, columnCount++, entry.getPartDescription(), style);
            ExcelExportUtils.createCell(row, columnCount++, entry.getQuantity(), style);
            ExcelExportUtils.createCell(row, columnCount, entry.getCsn(), style);
        }
        for(int currentRow=1; currentRow <= rowCount; currentRow++){
            sheet.autoSizeColumn(currentRow);
        }
    }

    /**
     * Takes a fileOutputStream and writes to it excelsheet data
     *
     * @param outputStream
     * @throws IOException
     */
    public void excelExport(FileOutputStream outputStream) throws IOException {
        writeHeaderLine();
        writeDataLines();

        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }
}
