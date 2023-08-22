package com.poiorm.mapper;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

public class ExcelOrmWriter {

    public static <T> void toExcel(final Sheet sheet, List<T> data) {

    }

    public static <T> void writeColumn(List<T> data, Sheet sheet, int startRowIndex, int columnIndex) {
        int rowIndex = startRowIndex;
        for (T item: data) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                row = sheet.createRow(rowIndex);
            }
            Cell cell = row.getCell(columnIndex);
            if (cell == null) {
                // TODO определения типа ячейки
                cell = row.createCell(columnIndex, CellType.STRING);
            }
        }

    }
}
