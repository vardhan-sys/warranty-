package com.geaviation.techpubs.models.download.search;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.geaviation.techpubs.models.download.utils.ExcelExportUtils;

public class TechpubsExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<TechpubsSearchExcelEntry> techpubsSearchExcelEntries;
    private boolean hasCategory;

    /**
     * instantiates the ExcelExporter with the data it will export
     *
     * @param techpubsSearchExcelEntries the entries to be used in the creation of the excel sheet
     */
    public TechpubsExcelExporter(List<TechpubsSearchExcelEntry> techpubsSearchExcelEntries, boolean hasCategory) {
        this.techpubsSearchExcelEntries = techpubsSearchExcelEntries;
        this.hasCategory = hasCategory;
        workbook = new XSSFWorkbook();
    }

    /**
     * writes the headers for the excel sheet
     */
    private void writeHeaderLine() {
        sheet = workbook.createSheet("Search results");
        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        ExcelExportUtils.createCell(row, 0, "Title", style);
        ExcelExportUtils.createCell(row, 1, "Doc Type", style);
        ExcelExportUtils.createCell(row, 2, "Families", style);
        ExcelExportUtils.createCell(row, 3, "Models", style);
        ExcelExportUtils.createCell(row, 4, "Date", style);
        if(hasCategory) {
            ExcelExportUtils.createCell(row, 5, "Category", style);
            ExcelExportUtils.createCell(row, 6, "Details", style);
        } else {
            ExcelExportUtils.createCell(row, 5, "Details", style);
        }

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

        for (TechpubsSearchExcelEntry entry : techpubsSearchExcelEntries) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            StringBuilder families = new StringBuilder();
            for (int entryNumber = 0; entryNumber < entry.getEngineFamily().size(); entryNumber++) {
                if (entryNumber == entry.getEngineFamily().size() -1 ) {
                    families.append(entry.getEngineFamily().get(entryNumber));
                } else {
                    families.append(entry.getEngineFamily().get(entryNumber) + ", ");
                }
            }

            StringBuilder models = new StringBuilder();
            for (int entryNumber = 0; entryNumber < entry.getEngineModels().size(); entryNumber++) {
                if (entryNumber == entry.getEngineModels().size() -1 ) {
                    models.append(entry.getEngineModels().get(entryNumber));
                } else {
                    models.append(entry.getEngineModels().get(entryNumber) + ", ");
                }
            }

            ExcelExportUtils.createCell(row, columnCount++, entry.getTitle(), style);
            ExcelExportUtils.createCell(row, columnCount++, entry.getType(), style);
            ExcelExportUtils.createCell(row, columnCount++, families.toString(), style);
            ExcelExportUtils.createCell(row, columnCount++, models.toString(), style);
            ExcelExportUtils.createCell(row, columnCount++, entry.getDate(), style);
            if(hasCategory) {
                ExcelExportUtils.createCell(row, columnCount++, entry.getCategory(), style);
            }
            ExcelExportUtils.createCell(row, columnCount, entry.getDetails(), style);

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
