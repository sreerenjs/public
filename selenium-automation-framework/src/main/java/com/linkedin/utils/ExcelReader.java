package com.linkedin.utils;

import com.linkedin.config.ConfigReader;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ExcelReader - Reads test data from Excel (.xlsx) files using Apache POI
 *
 * Excel Sheet Format (JobSearch sheet):
 * | jobTitle     | location  | experienceLevel | keyword       |
 * | Java Dev     | Bangalore | Mid-Senior      | Spring Boot   |
 * | QA Engineer  | Remote    | Entry Level     | Selenium      |
 */
public class ExcelReader {

    private Workbook workbook;
    private Sheet sheet;

    // Constructor: opens the workbook and selects the sheet from config
    public ExcelReader() {
        try {
            String filePath = ConfigReader.getExcelFilePath();
            String sheetName = ConfigReader.getExcelSheetName();
            FileInputStream fis = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fis);
            sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet '" + sheetName + "' not found in " + filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to open Excel file: " + e.getMessage());
        }
    }

    /**
     * Returns all rows as a list of maps: column header -> cell value
     * First row is treated as the header row
     */
    public List<Map<String, String>> getAllRows() {
        List<Map<String, String>> data = new ArrayList<>();
        Row headerRow = sheet.getRow(0);
        int totalRows = sheet.getLastRowNum();

        for (int rowIdx = 1; rowIdx <= totalRows; rowIdx++) {
            Row currentRow = sheet.getRow(rowIdx);
            if (currentRow == null) continue;

            Map<String, String> rowData = new LinkedHashMap<>();
            for (int colIdx = 0; colIdx < headerRow.getLastCellNum(); colIdx++) {
                String header = getCellValueAsString(headerRow.getCell(colIdx));
                String value  = getCellValueAsString(currentRow.getCell(colIdx));
                rowData.put(header, value);
            }
            data.add(rowData);
        }
        return data;
    }

    /**
     * Returns a single row by row index (0-based, excluding header)
     */
    public Map<String, String> getRowByIndex(int rowIndex) {
        List<Map<String, String>> allRows = getAllRows();
        if (rowIndex >= allRows.size()) {
            throw new RuntimeException("Row index " + rowIndex + " is out of bounds.");
        }
        return allRows.get(rowIndex);
    }

    /**
     * Returns all values of a specific column by column header name
     */
    public List<String> getColumnValues(String columnHeader) {
        List<String> values = new ArrayList<>();
        for (Map<String, String> row : getAllRows()) {
            if (row.containsKey(columnHeader)) {
                values.add(row.get(columnHeader));
            }
        }
        return values;
    }

    /**
     * Converts any cell type to a clean String value
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:  return cell.getStringCellValue().trim();
            case NUMERIC: return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA: return cell.getCellFormula();
            default:      return "";
        }
    }

    // Always close the workbook after use
    public void closeWorkbook() {
        try {
            if (workbook != null) workbook.close();
        } catch (IOException e) {
            System.err.println("Warning: Could not close workbook: " + e.getMessage());
        }
    }
}
